package com.handlers;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;

public class minigameCommands extends commandHandler {

    public minigameCommands(ServerWorker SW) {
        super(SW);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean handler(String cmd, String[] args) {

        if ("minigames".equals(cmd)) {
            if ("help".equals(args[0])) {
                return true;
            } else if ("start".equals(args[0])) {
                if ("ttt".equals(args[1])) {
                    if (SW.aktive_game == null) {

                    }
                }
                return true;
            }

            SW.send("Help");
        }

        return false;
    }

}
