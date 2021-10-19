package com.handlers;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;
import com.db.dbOperations;

import java.util.ArrayList;

public class dbCommands extends commandHandler {


    public dbCommands(ServerWorker SW) {
        super(SW);
    }

    @Override
    public boolean handler(String cmd, String[] args) {
        if("create".equalsIgnoreCase(cmd)){
            //Überprüfe, was neu erstellt werden soll (User, Gruppe?)
            if(args[0].equals("user")){
                if (args.length != 3){
                    SW.send("Fehlerhafte Eingabe, tippe 'create user <name> <pwd>'");
                }

                //Überprüfe, ob der Benutzername verfügbar ist
                //ArrayList<String> userNames = dbOperations.readColumn();

            } else if(args[0].equals("group")){
                // TODO UserGruppen erstellen
            }

        } else if ("login".equalsIgnoreCase(cmd)){

        } else if ("delete".equalsIgnoreCase(cmd)) {

        } else if ("change".equalsIgnoreCase(cmd)) {

        }

        return false;
    }
}
