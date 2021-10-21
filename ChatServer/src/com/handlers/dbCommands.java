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

                if(argsUser.length() > 30 || argsPwd.length() > 30){
                    SW.send("Nutzername und Passwort dürfen maximal 30 Zeichen lang sein");
                    return true;
                }

                //Überprüfe, ob der Benutzername verfügbar ist
                if(dbOperations.userExists(argsUser)){
                    SW.send("Nutzername "+argsUser+" vergeben, versuche einen anderen");
                    return true;
                }

                //Erstelle den Nutzer
                dbOperations.writeData("users", new String[]{"name", "pwd"}, new String[]{argsUser, argsPwd});
                SW.send("Nutzer " + argsUser + " erfolgreich erstellt.");

                //Den Nutzer anmelden
                SW.handleLogin(SW.outputStream, new String[]{argsUser, argsPwd});

                return true;
            } else if (args[0].equalsIgnoreCase("group")) {
                // TODO UserGruppen erstellen
            }

        } else if ("delete".equalsIgnoreCase(cmd)) {
            //Überprüfe, was gelöscht werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 3) {
                    SW.send("Fehlerhafte Eingabe, tippe 'delete user <name> <pwd>'");
                    return true;
                }

                String argsUser = args[1];
                String argsPwd = args[2];

                //Überprüfe, ob der Nutzer existiert
                if(!dbOperations.userExists(argsUser)){
                    SW.send("Der Nutzer "+argsUser+" existiert nicht.");
                    return true;
                }

                //Überprüfe, ob das eingebene Passwort richtig ist
                if(!dbOperations.pwdCorrect(argsUser, argsPwd)){
                    SW.send("Falsches Passwort für " +argsUser);
                    return true;
                }

                //Lösche den Nutzer
                dbOperations.deleteData("users", "name", argsUser);
                SW.send("Nutzer wurde erfolgreich gelöscht.");
                return true;

            } else if (args[0].equalsIgnoreCase("group")) {
                // TODO UserGruppen löschen
            }


        } else if ("change".equalsIgnoreCase(cmd)) {
            //Überprüfe, was verändert werden soll (User, Gruppe?)
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length != 5) {
                    SW.send("Fehlerhafte Eingabe, tippe 'change user name/pwd <name> <pwd> <new name/pwd>'");
                    return true;
                }

                String argsUser = args[2];
                String argsPwd = args[3];
                String argsChange = args[1];
                String argsNewValue = args[4];

                if(argsNewValue.length() > 30){
                    SW.send("Nutzername und Passwort dürfen maximal 30 Zeichen lang sein");
                    return true;
                }

                //Überprüfe, ob der Nutzer existiert
                if(!dbOperations.userExists(argsUser)){
                    SW.send("Der Nutzer "+argsUser+" existiert nicht.");
                    return true;
                }
                //Überprüfe, ob das Passwort korrekt ist
                if(!dbOperations.pwdCorrect(argsUser, argsPwd)){
                    SW.send("Falsches Passwort für " +argsUser);
                    return true;
                }

                //Überprüfe, was am user verändert werden soll (name, pwd?)
                if(argsChange.equalsIgnoreCase("name")){
                    //Überprüfe, ob der neue Nutzername verfügbar ist
                    if(dbOperations.userExists(argsNewValue)){
                        SW.send("Nutzername "+argsNewValue+" vergeben, versuche einen anderen");
                        return true;
                    }

                    dbOperations.updateData("users", "name", argsUser, "name", argsNewValue);
                    SW.send("Nutzer "+argsUser+" wurde erfolgreich zu " +argsNewValue+" geändert.");

                    //Den Nutzer anmelden (Hier im Gegensatz zu nach change pwd notwendig, damit unter worker.getlogin nun der neue name hinterlegt ist)
                    SW.handleLogin(SW.outputStream, new String[]{argsNewValue, argsPwd});
                    return true;

                } else if(argsChange.equalsIgnoreCase("pwd")){
                    dbOperations.updateData("users", "name", argsUser, "pwd", argsNewValue);
                    SW.send("Passwort von "+argsUser+" wurde erfolgreich geändert.");
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

                for (String[] a: usersWithLastonl) {
                    SW.send(Arrays.toString(a));
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
}
