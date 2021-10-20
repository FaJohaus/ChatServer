package com.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        } else if ("msg".equalsIgnoreCase(cmd)) {
            SW.msg(SW.outputStream, args);
            return true;
        } else if ("sendto".equalsIgnoreCase(cmd)){
            SW.sendTo(args[0], args[1]);
            return true;
        }

        return false;

    }
}
