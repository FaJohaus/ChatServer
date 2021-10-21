package com.handlers;

import java.util.Arrays;

import com.db.dbOperations;
import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;

public class generalCommands extends commandHandler {

    public generalCommands(ServerWorker SW) {
        super(SW);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean handler(String cmd, String[] args) {
        if(SW.loginIsNull() && !"create".equalsIgnoreCase(cmd) && !"login".equalsIgnoreCase(cmd)){
            SW.send2Null("Melde dich zuerst mit 'create <user> <pwd>' an oder logge dich mit 'login <user> <pwd>' ein.");
            return true;
        }

        if ("quit".equalsIgnoreCase(cmd) || "leave".equalsIgnoreCase(cmd)) {
            SW.handleQuit();
        } else if ("logout".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)){
            SW.handleLogout();
        } else if ("login".equalsIgnoreCase(cmd)) {
            SW.handleLogin(SW.outputStream, args);
            return true;
        } else if ("send".equalsIgnoreCase(cmd)) {
            SW.sendToAll("(An Alle) " +SW.getLogin()+": "+String.join(" ", args));
            return true;
        } else if ("sendto".equalsIgnoreCase(cmd)){
            String receiver = args[0];

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
                SW.send(receiver+" ist grade nicht online, deine Nachricht wurde gespeichert und er/sie kann sie lesen, wenn er online ist.");
            }

            return true;
        } else if ("whoami".equalsIgnoreCase(cmd)){
            SW.send(SW.getLogin());
            return true;
        }

        return false;

    }
}
