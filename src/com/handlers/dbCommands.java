package com.handlers;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;
import com.db.dbOperations;

import java.util.ArrayList;
import java.util.Arrays;

public class dbCommands extends commandHandler {

    public dbCommands(ServerWorker SW) {
        super(SW);
    }

    @Override
    public boolean handler(String cmd, String[] args) {
        if ("create".equalsIgnoreCase(cmd)) {
            // Überprüfe, was erstellt werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 3) {
                    SW.send("Fehlerhafte Eingabe, tippe 'create user <name> <pwd>'");
                    return true;
                }

                String argsUser = args[1];
                String argsPwd = args[2];

                if (argsUser.equals("group")) {
                    SW.send("Du darfst dich nicht 'group' nennen, sonst fliegt hier alles in die Luft!!!");
                    return true;
                }

                if (argsUser.length() > 20 || argsPwd.length() > 20) {
                    SW.send("Nutzername und Passwort dürfen maximal 20 Zeichen lang sein");
                    return true;
                }

                // Überprüfe, ob der Benutzername verfügbar ist
                if (dbOperations.userExists(argsUser)) {
                    SW.send("Nutzername " + argsUser + " vergeben, versuche einen anderen");
                    return true;
                }

                // Erstelle den Nutzer
                dbOperations.writeData("users", new String[] { "name", "pwd" }, new String[] { argsUser, argsPwd });

                // Den Nutzer anmelden
                SW.handleLogin(SW.outputStream, new String[] { argsUser, argsPwd });

                SW.send("Nutzer " + argsUser + " erfolgreich erstellt.");
                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                String argsGroupName = args[1];
                if (argsGroupName.length() > 30) {
                    SW.send("Gruppennamen dürfen nicht länger als 30 Zeichen sein.");
                    return true;
                }

                // Überprüfe, ob der Gruppenname verfügbar ist
                if (dbOperations.tableExists("group" + argsGroupName)) {
                    SW.send("Der Gruppenname " + argsGroupName + " ist vergeben.");
                    return true;
                }

                String user = SW.getLogin();
                ArrayList<String> members = new ArrayList<>();
                members.add(user);
                members.addAll(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));

                // Überprüfe, ob alle Mitglieder existieren
                for (String member : members) {
                    if (!dbOperations.userExists(member)) {
                        members.remove(member);
                        SW.send("Der Nutzer " + member
                                + " existiert nicht und kann nicht zur Gruppe hinzugefügt werden.");
                    }
                }

                // Erstelle die Gruppe
                dbOperations.createTable("group" + argsGroupName, "members", 21);
                dbOperations.addUsersToGroup(argsGroupName, members);
                SW.send("Gruppe " + argsGroupName + " wurde erstellt.");

                // Teile den anderen Nutzern mit, dass sie zu einer Gruppe hinzugefügt wurden
                if (members.size() > 1) {
                    for (String member : members) {
                        if (!member.equals(user)) {
                            SW.sendToWithDBsafe(member,
                                    "!! Du wurdest von " + user + " zu " + argsGroupName + "  hinzugefügt !!");
                        }
                    }
                }

                return true;
            }

        } else if ("login".equalsIgnoreCase(cmd)) {
            SW.handleLogin(SW.outputStream, args);
            // Überprüfe, ob der Nutzer noch Nachrichten zu lesen hat
            String user = SW.getLogin();
            if (dbOperations.tableExists("messages" + user)) {
                SW.send("Du hast noch Nachrichten zu lesen, du kannst sie mit 'list messages' sehen.");
            }

            return true;
        } else if ("delete".equalsIgnoreCase(cmd)) {
            // Überprüfe, was gelöscht werden soll (User, Gruppe, Message?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 2) {
                    SW.send("Fehlerhafte Eingabe, tippe 'delete user <pwd>'");
                    return true;
                }
                String user = SW.getLogin();
                String argsPwd = args[1];

                // Überprüfe, ob der Nutzer existiert
                if (!dbOperations.userExists(user)) {
                    SW.send("Der Nutzer " + user + " existiert nicht.");
                    return true;
                }

                // Überprüfe, ob das eingebene Passwort richtig ist
                if (!dbOperations.pwdCorrect(user, argsPwd)) {
                    SW.send("Falsches Passwort für " + user);
                    return true;
                }

                // Lösche den Nutzer
                dbOperations.deleteData("users", "name", user);
                SW.send("Nutzer wurde erfolgreich gelöscht.");

                // Lösche den table der ungelesenen Nachrichten des Nutzers
                dbOperations.deleteTable("messages" + user);

                // Lösche den Nutzer aus all seinen Gruppen und seinen table für seine Gruppen
                // (falls es welche gibt)
                if (dbOperations.tableExists("groupsof" + user)) {
                    for (String group : dbOperations.readColumn("groupsof" + user, "chatGroups")) {
                        dbOperations.deleteData("group" + group, "members", user);
                    }

                    dbOperations.deleteTable("groupsof" + user);
                }

                // Logge den Nutzer aus
                SW.handleLogout();
                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                String argsGroupname = args[1];
                if (!dbOperations.tableExists("group" + argsGroupname)) {
                    SW.send("Ein Gruppe mit dem Namen " + argsGroupname + " existiert nicht.");
                    return true;
                }

                String user = SW.getLogin();

                if (!dbOperations.isMemberOfGroup(argsGroupname, user)) {
                    SW.send("Du bist kein Mitglied dieser Gruppe und kannst sie deshalb nicht löschen.");
                    return true;
                }

                ArrayList<String> members = dbOperations.readColumn("group" + argsGroupname, "members");

                for (String member : dbOperations.readColumn("group" + argsGroupname, "members")) {
                    dbOperations.deleteData("groupsof" + member, "chatGroups", argsGroupname);
                }
                dbOperations.deleteTable("group" + argsGroupname);
                SW.send("Gruppe " + argsGroupname + " wurde gelöscht.");

                // Teile den anderen Nutzern mit, dass die Gruppe gelöscht wurde
                if (members.size() > 1) {
                    for (String member : members) {
                        if (!member.equals(user)) {
                            SW.sendToWithDBsafe(member,
                                    "!! Die Gruppe " + argsGroupname + " wurde von " + user + " gelöscht. !!");
                        }
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("messages")) {
                String user = SW.getLogin();
                if (!dbOperations.tableExists("messages" + user)) {
                    SW.send("Es gibt keine Nachrichten für dich, die gelöscht werden könnten.");
                    return true;
                }
                dbOperations.deleteTable("messages" + user);
                SW.send("Deine Nachrichten wurden gelöscht.");

                return true;
            }

        } else if ("change".equalsIgnoreCase(cmd)) {
            // Überprüfe, was verändert werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 4) {
                    SW.send("Fehlerhafte Eingabe, tippe 'change user name/pwd <pwd> <new name/pwd>'");
                    return true;
                }

                String user = SW.getLogin();
                String argsPwd = args[2];
                String argsChange = args[1];
                String argsNewValue = args[3];

                if (argsNewValue.length() > 20) {
                    SW.send("Nutzername und Passwort dürfen maximal 20 Zeichen lang sein");
                    return true;
                }

                // Überprüfe, ob der Nutzer existiert
                if (!dbOperations.userExists(user)) {
                    SW.send("Der Nutzer " + user + " existiert nicht.");
                    return true;
                }
                // Überprüfe, ob das Passwort korrekt ist
                if (!dbOperations.pwdCorrect(user, argsPwd)) {
                    SW.send("Falsches Passwort für " + user);
                    return true;
                }

                // Überprüfe, was am user verändert werden soll (name, pwd?)
                if (argsChange.equalsIgnoreCase("name")) {
                    // Überprüfe, ob der neue Nutzername verfügbar ist
                    if (dbOperations.userExists(argsNewValue)) {
                        SW.send("Nutzername " + argsNewValue + " vergeben, versuche einen anderen");
                        return true;
                    }

                    dbOperations.updateData("users", "name", user, "name", argsNewValue);
                    SW.send("Nutzer " + user + " wurde erfolgreich zu " + argsNewValue + " geändert.");

                    // Falls der Nutzer ungelesene Nachrichten hat, den table umbenenen
                    // TODO hier wäre es jetzt mit user IDs wesentlich praktischer, aber dann müsste
                    // ich eigentlich fast alles was hier mit der Datenbank zu tun hat
                    // TODO neu schreiben und da habe ich wirklich keine Lust zu
                    if (dbOperations.tableExists("messages" + user)) {
                        dbOperations.changeTableName("messages" + user, "messages" + argsNewValue);
                    }

                    // neuen Nutzernamen auch in Gruppen ändern
                    if (dbOperations.tableExists("groupsof" + user)) {
                        for (String group : dbOperations.readColumn("groupsof" + user, "chatGroups")) {
                            dbOperations.updateData("group" + group, "members", user, "members", argsNewValue);
                        }
                        dbOperations.changeTableName("groupsof" + user, "groupsof" + argsNewValue);
                    }

                    // Den Nutzer anmelden (Hier im Gegensatz zu nach change pwd notwendig, damit
                    // unter worker.getlogin nun der neue name hinterlegt ist)
                    SW.handleLogin(SW.outputStream, new String[] { argsNewValue, argsPwd });
                    return true;

                } else if (argsChange.equalsIgnoreCase("pwd")) {
                    dbOperations.updateData("users", "name", user, "pwd", argsNewValue);
                    SW.send("Passwort von " + user + " wurde erfolgreich geändert.");
                    return true;
                }

                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                String user = SW.getLogin();
                String argsGroupname = args[2];

                if (!dbOperations.tableExists("group" + argsGroupname)) {
                    SW.send("Ein Gruppe mit dem Namen " + argsGroupname + " existiert nicht.");
                    return true;
                }

                if (!dbOperations.isMemberOfGroup(argsGroupname, user)) {
                    SW.send("Du bist kein Mitglied dieser Gruppe und kannst sie deshalb nicht verändern.");
                    return true;
                }

                if (args[1].equalsIgnoreCase("name")) {
                    String argsNewGroupname = args[3];
                    for (String member : dbOperations.readColumn("group" + argsGroupname, "members")) {
                        dbOperations.updateData("groupsof" + member, "chatGroups", argsGroupname, "chatGroups",
                                argsNewGroupname);
                    }
                    dbOperations.changeTableName("group" + argsGroupname, "group" + argsNewGroupname);
                    SW.send("Gruppenname wurde von " + argsGroupname + " zu " + argsNewGroupname + " geändert.");

                    // Teile den anderen Nutzern mit, dass die Gruppe geändert wurde
                    ArrayList<String> members = dbOperations.readColumn("group" + argsNewGroupname, "members");
                    if (members.size() > 1) {
                        for (String member : members) {
                            if (!member.equals(user)) {
                                SW.sendToWithDBsafe(member, "!! Die Gruppe " + argsGroupname + " wurde von " + user
                                        + " zu " + argsNewGroupname + " umbenannt. !!");
                            }
                        }
                    }

                    return true;
                } else if (args[1].equalsIgnoreCase("members")) {
                    ArrayList<String> argsMembers = new ArrayList<>();
                    argsMembers.addAll(Arrays.asList(Arrays.copyOfRange(args, 4, args.length)));

                    if (args[3].equalsIgnoreCase("add")) {
                        // Überprüfe, ob alle Mitglieder existieren
                        for (int i = 0; i < argsMembers.size(); i++) {
                            if (!dbOperations.userExists(argsMembers.get(i))) {
                                System.out.println(argsMembers.get(i));
                                argsMembers.remove(argsMembers.get(i));
                                SW.send("Der Nutzer " + argsMembers.get(i)
                                        + " existiert nicht und kann nicht zur Gruppe hinzugefügt werden.");
                            }
                        }

                        // Überprüfe, ob user schon Mitglied der Gruppe sind
                        ArrayList<String> membersInDB = dbOperations.readColumn("group" + argsGroupname, "members");
                        for (int i = 0; i < membersInDB.size(); i++) {
                            for (int j = 0; j < argsMembers.size(); j++) {
                                if (membersInDB.get(i).equals(argsMembers.get(j))) {
                                    argsMembers.remove(membersInDB.get(i));
                                    SW.send("Der Nutzer " + membersInDB.get(i) + " ist bereits in der Gruppe.");
                                }
                            }
                        }

                        if (argsMembers.size() != 0) {
                            dbOperations.addUsersToGroup(argsGroupname, argsMembers);
                            SW.send("Die Nutzer " + argsMembers + " wurden zu " + argsGroupname + " hinzugefügt.");
                            for (String member : argsMembers) {
                                SW.sendToWithDBsafe(member,
                                        "!! Du wurdest von " + user + " zu " + argsGroupname + " hinzugefügt !!");
                            }

                            for (String member : membersInDB) {
                                if (!member.equals(user)) {
                                    SW.sendToWithDBsafe(member, "!! " + user + " hat " + argsMembers + " zu "
                                            + argsGroupname + " hinzugefügt. !!");
                                }
                            }
                        }

                        return true;
                    } else if (args[3].equalsIgnoreCase("remove")) {
                        // Überprüfe, ob die user überhaupt in der Gruppe sind
                        ArrayList<String> membersInDB = dbOperations.readColumn("group" + argsGroupname, "members");
                        for (String argsMember : argsMembers) {
                            if (!dbOperations.isMemberOfGroup(argsGroupname, argsMember)) {
                                argsMembers.remove(argsMember);
                                SW.send(argsMember + " ist kein Mitglied von " + argsGroupname + ".");
                            }
                        }

                        if (argsMembers.size() != 0) {
                            for (String member2Delete : argsMembers) {
                                dbOperations.deleteData("groupsof" + member2Delete, "chatGroups", argsGroupname);
                                dbOperations.deleteData("group" + argsGroupname, "members", member2Delete);
                            }

                            SW.send("Die Nutzer " + argsMembers + " wurden aus " + argsGroupname + " entfernt.");
                            for (String member : argsMembers) {
                                SW.sendToWithDBsafe(member,
                                        "!! Du wurdest von " + user + " aus " + argsGroupname + " entfernt. !!");
                            }

                            for (String member : membersInDB) {
                                if (!member.equals(user)) {
                                    SW.sendToWithDBsafe(member, "!! " + user + " hat " + argsMembers + " aus "
                                            + argsGroupname + " entfernt. !!");
                                }
                            }
                        }

                        return true;
                    }
                }

            }

        } else if ("list".equalsIgnoreCase(cmd)) {
            // Überprüfe, was aufgelistet werden soll
            if (args[0].equalsIgnoreCase("users")) {
                // Hole eine Liste aller User von der db und speichere sie mit ihrem lastonl
                // Wert
                ArrayList<String> users = dbOperations.readColumn("users", "name");
                ArrayList<String[]> usersWithLastonl = new ArrayList<String[]>();
                for (String user : users) {
                    usersWithLastonl
                            .add(new String[] { user, dbOperations.readValue("users", "name", user, "lastonl") });
                }

                long now = System.currentTimeMillis();
                for (String[] a : usersWithLastonl) {
                    // Sende alle Nutzer, außer den Nutzer der anfragt
                    if (!SW.equalsLogin(a[0])) {
                        SW.send(a[0] + mySpacing(20 - a[0].length()) + time2String(now, a[1]));
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("groups")) {
                String user = SW.getLogin();

                if (dbOperations.tableExists("groupsof" + user)) {
                    ArrayList<String> groups = dbOperations.readColumn("groupsof" + user, "chatGroups");

                    if (groups.size() == 0) {
                        SW.send("Du bist aktuell kein Mitglied einer Gruppe.");
                        return true;
                    }

                    SW.send("Du bist aktuell Mitglied in diesen Gruppen:");
                    for (String group : groups) {
                        SW.send(group);
                    }
                    return true;
                }
                SW.send("Du bist aktuell kein Mitglied einer Gruppe.");

                return true;
            } else if (args[0].equalsIgnoreCase("messages")) {
                String user = SW.getLogin();
                // Überprüfe, ob es Nachrichten für den Benutzer gibt
                if (!dbOperations.tableExists("messages" + user)) {
                    SW.send("Es wurden keine Nachrichten an dich geschickt, als du offline warst.");
                    return true;
                }
                ArrayList<String> messages = dbOperations.readColumn("messages" + user, "messages");
                for (String message : messages) {
                    SW.send("Nachricht:            " + message);
                }
                SW.send("Du kannst diese Nachrichten mit 'delete messages' löschen, wenn du willst.");

                return true;
            } else if(args[0].equalsIgnoreCase("members")){
                String argsGroupname = args[1];
                String user = SW.getLogin();

                if(!dbOperations.tableExists("group"+argsGroupname) || !dbOperations.isMemberOfGroup(argsGroupname, user)){
                    SW.send("Die Gruppe "+argsGroupname+"existiert nicht oder du bist kein Mitglied.");
                    return true;
                }

                SW.send("Mitglieder von "+argsGroupname+": ");
                for (String member: dbOperations.readColumn("group"+argsGroupname, "members")) {
                    SW.send(member);
                }
                return true;
            }
        }
        return false;
    }

    private String time2String(long current, String lastonlStr) {
        if (lastonlStr.equals("online")) {
            return "Online";
        }

        String s = "Zuletzt Online vor ";
        long lastonl = (current - Long.parseLong(lastonlStr)) / 1000;
        if (lastonl < 60) {
            return s + lastonl + " Sekunde(n)";
        }

        else {
            lastonl = lastonl / 60;
        }
        if (lastonl < 60) {
            return s + lastonl + " Minute(n)";
        }

        else {
            lastonl = lastonl / 60;
        }
        if (lastonl < 24) {
            return s + lastonl + " Stunde(n)";
        }

        else {
            lastonl = lastonl / 24;
        }
        if (lastonl < 7) {
            return s + lastonl + " Tag(en)";
        }

        else {
            lastonl = lastonl / 7;
        }
        if (lastonl < 4) {
            return s + lastonl + " Woche(n)";
        }

        else {
            lastonl = lastonl / 4;
        }
        if (lastonl < 12) {
            return s + lastonl + " Monat(en)";
        }

        else {
            lastonl = lastonl / 12;
        }
        return s + lastonl + " Jahr(en)";
    }

    private String mySpacing(int laenge) {
        String s = "  ";
        for (int i = 0; i < laenge; i++) {
            s += " ";
        }
        return s;
    }
}
