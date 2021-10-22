package com.handlers;

import java.util.Arrays;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;
import com.db.dbOperations;

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
        } else if ("send".equalsIgnoreCase(cmd)) {
            SW.sendToAll("(An Alle) " +SW.getLogin()+": "+String.join(" ", args));
            return true;
        } else if ("sendto".equalsIgnoreCase(cmd)){
            if(!args[0].equals("group")){
                String[] a = Arrays.copyOfRange(args, 1, args.length);
                String message = "(Privat) "+SW.getLogin()+": "+String.join(" ", a);
                SW.sendToWithDBsafe(args[0], message);

                return true;
            }

            String argsGroupname = args[1];
            if(!dbOperations.tableExists("group"+argsGroupname) && !dbOperations.isMemberOfGroup(argsGroupname, SW.getLogin())){
                SW.send("Die Gruppe "+argsGroupname+" existiert nicht oder du bist kein Mitglied.");
                return  true;
            }

            String[] a = Arrays.copyOfRange(args, 2, args.length);
            String message = "(in "+argsGroupname+") "+SW.getLogin()+": "+String.join(" ", a);

            for (String member: dbOperations.readColumn("group"+argsGroupname, "members")) {
                if(!member.equals(SW.getLogin())){
                    SW.sendToWithDBsafe(member, message);
                }
            }

            return true;
        } else if ("whoami".equalsIgnoreCase(cmd)){
            SW.send("Du bist angemeldet als "+ SW.getLogin()+".");
            return true;
        }

        return false;

    }
}
