package com.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        if ("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
            SW.handleLogoff();
        } else if ("login".equalsIgnoreCase(cmd)) {
            SW.handleLogin(SW.outputStream, args);
            return true;
        } else if ("send".equalsIgnoreCase(cmd)) {
            SW.server.sendToAll(String.join(" " + args));
            return true;
        } else if ("sendto".equalsIgnoreCase(cmd)){
            String[] a = Arrays.copyOfRange(args, 1, args.length);
            SW.sendTo(args[0], "(Privat) "+String.join(" ", a));
            return true;
        }

        return false;

    }
}
