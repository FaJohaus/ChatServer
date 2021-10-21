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
                // TODO UserGruppen erstellen
            }

        } else if ("delete".equalsIgnoreCase(cmd)) {
            //Überprüfe, was gelöscht werden soll (User, Gruppe?)
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

                //Logge den Nutzer aus
                SW.handleLogout();

                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                // TODO UserGruppen löschen
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
                // TODO list messages (alle Nachrichten, die in Abwesenheit des Nutzers an ihn gingen in einer column speichern)
            }
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
