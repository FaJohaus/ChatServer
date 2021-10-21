package com.handlers;

import java.util.Arrays;

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
            SW.sendToAll("(An Alle) "+String.join(" ", args));
            return true;
        } else if ("sendto".equalsIgnoreCase(cmd)){
            String[] a = Arrays.copyOfRange(args, 1, args.length);
            SW.sendTo(args[0], "(Privat) "+String.join(" ", a));
            return true;
        } else if ("whoami".equalsIgnoreCase(cmd)){
            SW.send(SW.getLogin());
            return true;
        }

        return false;

    }
}
