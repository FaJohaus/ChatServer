package com.handlers;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;
import com.db.dbOperations;

import java.util.ArrayList;

public class dbCommands extends commandHandler {


    public dbCommands(ServerWorker SW) {
        super(SW);
    }

    private boolean userExists(String name2check){
        ArrayList<String> userNames = dbOperations.readColumn("users", "name");

        boolean userExists = false;
        for (String name: userNames) {
            if (name.equals(name2check)) {
                userExists = true;
            }
        }
        return userExists;
    }

    private boolean pwdCorrect(String user2check, String pwd2check){
        String pwd = dbOperations.readValue("users", "name", user2check, "pwd");
        if(pwd.equals(pwd2check)){return true;}
        else{return false;}
    }

    @Override
    public boolean handler(String cmd, String[] args) {
        if ("create".equalsIgnoreCase(cmd)) {
            //Überprüfe, was erstellt werden soll (User, Gruppe?)
            if (args[0].equals("user")) {
                if (args.length != 3) {
                    SW.send("Fehlerhafte Eingabe, tippe 'create user <name> <pwd>'");
                    return true;
                }

                String argsUser = args[1];
                String argsPwd = args[2];

                //Überprüfe, ob der Benutzername verfügbar ist
                if(userExists(argsUser)){
                    SW.send("Nutzername "+argsUser+" vergeben, versuche einen anderen");
                    return true;
                }

                //Erstelle den Nutzer
                dbOperations.writeData("users", new String[]{"name", "pwd"}, new String[]{argsUser, argsPwd});
                SW.send("Nutzer " + argsUser + " erfolgreich erstellt.");

                return true;
            } else if (args[0].equals("group")) {
                // TODO UserGruppen erstellen
            }

        } else if ("login".equalsIgnoreCase(cmd)) {

        } else if ("delete".equalsIgnoreCase(cmd)) {
            //Überprüfe, was gelöscht werden soll (User, Gruppe?)
            if (args[0].equals("user")) {
                if (args.length != 3) {
                    SW.send("Fehlerhafte Eingabe, tippe 'delete user <name> <pwd>'");
                    return true;
                }

                String argsUser = args[1];
                String argsPwd = args[2];

                //Überprüfe, ob der Nutzer existiert
                if(!userExists(argsUser)){
                    SW.send("Der Nutzer "+argsUser+" existiert nicht.");
                    return true;
                }

                //Überprüfe, ob das eingebene Passwort richtig ist
                if(!pwdCorrect(argsUser, argsPwd)){
                    SW.send("Falsches Passwort für " +argsUser);
                    return true;
                }

                //Lösche den Nutzer
                dbOperations.deleteData("users", "name", argsUser);
                SW.send("Nutzer wurde erfolgreich gelöscht.");
                return true;

            } else if (args[0].equals("group")) {
                // TODO UserGruppen löschen
            }


        } else if ("change".equalsIgnoreCase(cmd)) {
            //Überprüfe, was verändert werden soll (User, Gruppe?)
            if (args[0].equals("user")) {
                if (args.length != 5) {
                    SW.send("Fehlerhafte Eingabe, tippe 'change user name/pwd <name> <pwd> <new name/pwd>'");
                    return true;
                }

                String argsUser = args[2];
                String argsPwd = args[3];
                String argsChange = args[1];
                String argsNewValue = args[4];

                //Überprüfe, ob der Nutzer existiert
                if(!userExists(argsUser)){
                    SW.send("Der Nutzer "+argsUser+" existiert nicht.");
                    return true;
                }
                //Überprüfe, ob das Passwort korrekt ist
                if(!pwdCorrect(argsUser, argsPwd)){
                    SW.send("Falsches Passwort für " +argsUser);
                    return true;
                }

                //Überprüfe, was am user verändert werden soll (name, pwd?)
                if(argsChange.equals("name")){
                    //Überprüfe, ob der neue Nutzername verfügbar ist
                    if(userExists(argsNewValue)){
                        SW.send("Nutzername "+argsNewValue+" vergeben, versuche einen anderen");
                        return true;
                    }

                    dbOperations.updateData("users", "name", argsUser, "name", argsNewValue);
                    SW.send("Nutzer "+argsUser+" wurde erfolgreich zu " +argsNewValue+" geändert.");
                    return true;

                } else if(argsChange.equals("pwd")){
                    dbOperations.updateData("users", "name", argsUser, "pwd", argsNewValue);
                    SW.send("Passwort von "+argsUser+" wurde erfolgreich geändert.");
                    return true;
                }

                return true;
            } else if (args[0].equals("group")) {
                // TODO UserGruppen verändern
                // TODO Wichtig: Gruppenliste mit Namen muss geändert werden, wenn ein Nutzer der Liste seinen Account löscht (er muss auch aus der Gruppenliste gelöscht werden),
                // TODO da sonst jemand einen account mit seinem namen erstellen kann und in den gruppen ist und das is no good no no :o

            }

        }
        return false;
    }
}
