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
            //Überprüfe, was erstellt werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 3) {
                    SW.send("Fehlerhafte Eingabe, tippe 'create user <name> <pwd>'");
                    return true;
                }

                String argsUser = args[1];
                String argsPwd = args[2];

                if(argsUser.length() > 20 || argsPwd.length() > 20){
                    SW.send("Nutzername und Passwort dürfen maximal 20 Zeichen lang sein");
                    return true;
                }

                //Überprüfe, ob der Benutzername verfügbar ist
                if(dbOperations.userExists(argsUser)){
                    SW.send("Nutzername "+argsUser+" vergeben, versuche einen anderen");
                    return true;
                }

                //Erstelle den Nutzer
                dbOperations.writeData("users", new String[]{"name", "pwd"}, new String[]{argsUser, argsPwd});

                //Den Nutzer anmelden
                SW.handleLogin(SW.outputStream, new String[]{argsUser, argsPwd});

                SW.send("Nutzer " + argsUser + " erfolgreich erstellt.");
                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                String argsGroupName = args[1];
                if(argsGroupName.length() > 30){
                    SW.send("Gruppennamen dürfen nicht länger als 30 Zeichen sein.");
                    return true;
                }

                //Überprüfe, ob der Gruppenname verfügbar ist
                if(dbOperations.tableExists("group"+argsGroupName)){
                    SW.send("Der Gruppenname " + argsGroupName + " ist vergeben.");
                    return true;
                }

                String user = SW.getLogin();
                ArrayList<String> members = new ArrayList<>();
                members.add(user);
                members.addAll(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));

                //Überprüfe, ob alle Mitglieder existieren
                for (String member: members) {
                    if(!dbOperations.userExists(member)){
                        members.remove(member);
                        SW.send("Der Nutzer " + member + " existiert nicht und kann nicht zur Gruppe hinzugefügt werden.");
                    }
                }

                //Erstelle die Gruppe
                dbOperations.createTable("group"+argsGroupName, "members", 21);
                for (String member: members) {
                    //Erstelle table für alle Gruppen des Nutzers, wenn er noch keinen hat
                    if(!dbOperations.tableExists("groupsof"+member)){
                        dbOperations.createTable("groupsof"+member, "chatGroups", 31);
                    }
                    //Füge diese Gruppe hinzu
                    dbOperations.writeData("groupsof"+member, new String[]{"chatGroups"}, new String[]{argsGroupName});

                    //Füge den Nutzer im table der Gruppe hinzu
                    dbOperations.writeData("group"+argsGroupName, new String[]{"members"}, new String[]{member});
                }

                return true;
            }

        } else if ("login".equalsIgnoreCase(cmd)) {
            SW.handleLogin(SW.outputStream, args);
            //Überprüfe, ob der Nutzer noch Nachrichten zu lesen hat
            String user = SW.getLogin();
            if(dbOperations.tableExists("messages"+user)){
                SW.send("Du hast noch Nachrichten zu lesen, du kannst sie mit 'list messages' sehen.");
            }

            return true;
        } else if ("delete".equalsIgnoreCase(cmd)) {
            //Überprüfe, was gelöscht werden soll (User, Gruppe, Message?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 2) {
                    SW.send("Fehlerhafte Eingabe, tippe 'delete user <pwd>'");
                    return true;
                }
                String user = SW.getLogin();
                String argsPwd = args[1];

                //Überprüfe, ob der Nutzer existiert
                if(!dbOperations.userExists(user)){
                    SW.send("Der Nutzer "+user+" existiert nicht.");
                    return true;
                }

                //Überprüfe, ob das eingebene Passwort richtig ist
                if(!dbOperations.pwdCorrect(user, argsPwd)){
                    SW.send("Falsches Passwort für " +user);
                    return true;
                }

                //Lösche den Nutzer
                dbOperations.deleteData("users", "name", user);
                SW.send("Nutzer wurde erfolgreich gelöscht.");

                //Lösche den table der ungelesenen Nachrichten des Nutzers
                dbOperations.deleteTable("messages"+user);

                //Logge den Nutzer aus
                SW.handleLogout();
                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                String argsGroupname = args[1];
                if(!dbOperations.tableExists("group"+argsGroupname)){
                    SW.send("Ein Gruppe mit dem Namen "+argsGroupname+" existiert nicht.");
                    return true;
                }

                String user = SW.getLogin();
                //Überprüfe, ob der Nutzer Mitglied dieser Gruppe ist
                boolean userInGroup = false;
                for (String member: dbOperations.readColumn("group"+argsGroupname, "members")) {
                    if(member.equals(user)){
                        userInGroup = true;
                    }
                }
                if(!userInGroup){
                    SW.send("Du bist kein Mitglied dieser Gruppe und kannst sie deshalb nicht löschen.");
                    return true;
                }

                for (String member: dbOperations.readColumn("group"+argsGroupname, "members")) {
                    dbOperations.deleteData("groupsof"+member, "chatGroups", argsGroupname);
                }
                dbOperations.deleteTable("group"+argsGroupname);
                SW.send("Gruppe "+argsGroupname+" wurde gelöscht.");

                return true;
            } else if (args[0].equalsIgnoreCase("messages")){
                String user = SW.getLogin();
                if(!dbOperations.tableExists("messages"+user)){
                    SW.send("Es gibt keine Nachrichten für dich, die gelöscht werden könnten.");
                    return true;
                }
                dbOperations.deleteTable("messages"+user);
                SW.send("Deine Nachrichten wurden gelöscht.");

                return true;
            }


        } else if ("change".equalsIgnoreCase(cmd)) {
            //Überprüfe, was verändert werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 4) {
                    SW.send("Fehlerhafte Eingabe, tippe 'change user name/pwd <pwd> <new name/pwd>'");
                    return true;
                }


                String user = SW.getLogin();
                String argsPwd = args[2];
                String argsChange = args[1];
                String argsNewValue = args[3];

                if(argsNewValue.length() > 20){
                    SW.send("Nutzername und Passwort dürfen maximal 20 Zeichen lang sein");
                    return true;
                }

                //Überprüfe, ob der Nutzer existiert
                if(!dbOperations.userExists(user)){
                    SW.send("Der Nutzer "+user+" existiert nicht.");
                    return true;
                }
                //Überprüfe, ob das Passwort korrekt ist
                if(!dbOperations.pwdCorrect(user, argsPwd)){
                    SW.send("Falsches Passwort für " +user);
                    return true;
                }

                //Überprüfe, was am user verändert werden soll (name, pwd?)
                if(argsChange.equalsIgnoreCase("name")){
                    //Überprüfe, ob der neue Nutzername verfügbar ist
                    if(dbOperations.userExists(argsNewValue)){
                        SW.send("Nutzername "+argsNewValue+" vergeben, versuche einen anderen");
                        return true;
                    }

                    dbOperations.updateData("users", "name", user, "name", argsNewValue);
                    SW.send("Nutzer "+user+" wurde erfolgreich zu " +argsNewValue+" geändert.");

                    //Falls der Nutzer ungelesene Nachrichten hat, den table umbenenen
                    //TODO hier wäre es jetzt mit user IDs wesentlich praktischer, aber dann müsste ich eigentlich fast alles was hier mit der Datenbank zu tun hat
                    //TODO neu schreiben und da habe ich wirklich keine Lust zu
                    if(dbOperations.tableExists("messages"+ user)){
                        dbOperations.changeTableName("messages"+ user, "messages"+ argsNewValue);
                    }

                    //Den Nutzer anmelden (Hier im Gegensatz zu nach change pwd notwendig, damit unter worker.getlogin nun der neue name hinterlegt ist)
                    SW.handleLogin(SW.outputStream, new String[]{argsNewValue, argsPwd});

                    return true;

                } else if(argsChange.equalsIgnoreCase("pwd")){
                    dbOperations.updateData("users", "name", user, "pwd", argsNewValue);
                    SW.send("Passwort von "+user+" wurde erfolgreich geändert.");
                    return true;
                }

                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                // TODO UserGruppen verändern
                // TODO Wichtig: Gruppenliste mit Namen muss geändert werden, wenn ein Nutzer der Liste seinen Account löscht (er muss auch aus der Gruppenliste gelöscht werden),
                // TODO da sonst jemand einen account mit seinem namen erstellen kann und in den gruppen ist und das is no good no no :o

            }

        } else if("list".equalsIgnoreCase(cmd)){
            //Überprüfe, was aufgelistet werden soll
            if(args[0].equalsIgnoreCase("users")){
                //Hole eine Liste aller User von der db und speichere sie mit ihrem lastonl Wert
                ArrayList<String> users = dbOperations.readColumn("users","name");
                ArrayList<String[]> usersWithLastonl = new ArrayList<String[]>();
                for (String user: users) {
                    usersWithLastonl.add(new String[]{user, dbOperations.readValue("users", "name", user, "lastonl")});
                }

                long now = System.currentTimeMillis();
                for (String[] a: usersWithLastonl) {
                    //Sende alle Nutzer, außer den Nutzer der anfragt
                    if(!SW.equalsLogin(a[0])){
                        SW.send(a[0] + mySpacing(20 - a[0].length()) + time2String(now, a[1]));
                    }
                }

                return true;
            } else if(args[0].equalsIgnoreCase("groups")){
                // TODO list groups (alle gruppen des Nutzers in einer column speichern)
            } else if(args[0].equalsIgnoreCase("messages")){
                String user = SW.getLogin();
                //Überprüfe, ob es Nachrichten für den Benutzer gibt
                if(!dbOperations.tableExists("messages"+user)){
                    SW.send("Es wurden keine Nachrichten an dich geschickt, als du offline warst.");
                    return true;
                }
                ArrayList<String> messages = dbOperations.readColumn("messages"+user, "messages");
                for (String message: messages) {
                    SW.send("Nachricht:            " + message);
                }
                SW.send("Du kannst diese Nachrichten mit 'delete messages' löschen, wenn du willst.");

                return true;
            }
        } else if ("sendto".equalsIgnoreCase(cmd)){
            String receiver = args[0];

            //Überprüfe, ob der Empfänger existiert
            if(!dbOperations.userExists(receiver)){
                SW.send("Der Nutzer " + receiver + " existiert nicht.");
                return true;
            }

            String[] a = Arrays.copyOfRange(args, 1, args.length);
            String message = "(Privat) "+SW.getLogin()+": "+String.join(" ", a);

            //Überprüfe, ob der Empfänger online ist, wenn nicht speichere die Nachricht in der db
            if(dbOperations.readValue("users", "name", receiver, "lastonl").equals("online")){
                SW.sendTo(receiver, message);
            } else {
                if(message.length() > 1000){
                    SW.send(receiver+" ist grade nicht online und deine Nachricht ist zu lang zum speichern, bitte fasse dich etwas kürzer.");
                }
                dbOperations.createTable("messages"+receiver,"messages", 1000);
                dbOperations.writeData("messages"+receiver, new String[]{"messages"}, new String[]{message});
                SW.send(receiver+" ist grade nicht online, deine Nachricht wurde gespeichert und er/sie kann sie lesen, wenn er/sie online ist.");
            }

            return true;
        }
        return false;
    }

    private String time2String(long current, String lastonlStr){
        if(lastonlStr.equals("online")){return "Online";}

        String s = "Zuletzt Online vor ";
        long lastonl = (current - Long.parseLong(lastonlStr)) / 1000;
        if(lastonl < 60){return s+ lastonl+" Sekunde(n)";}

        else {lastonl = lastonl / 60;}
        if(lastonl < 60){return s+ lastonl+ " Minute(n)";}

        else {lastonl = lastonl / 60;}
        if(lastonl < 24){return s+ lastonl+ " Stunde(n)";}

        else {lastonl = lastonl / 24;}
        if(lastonl < 7){return s+ lastonl+ " Tag(en)";}

        else {lastonl = lastonl / 7;}
        if(lastonl < 4){return s+ lastonl+ " Woche(n)";}

        else {lastonl = lastonl / 4;}
        if(lastonl < 12){return s+ lastonl+ " Monat(en)";}

        else {lastonl = lastonl / 12;}
        return s+ lastonl+ " Jahr(en)";
    }

    private String mySpacing(int laenge){
        String s = "  ";
        for (int i = 0; i < laenge; i++) {
            s += " ";
        }
        return s;
    }
}
