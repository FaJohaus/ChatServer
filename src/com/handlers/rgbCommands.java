package com.handlers;

import com.muc.ServerWorker;
import com.utils.chatutils.commandhandler.commandHandler;

public class rgbCommands extends commandHandler {

    public rgbCommands(ServerWorker SW) {
        super(SW);
    }

    public boolean handler(String cmd, String[] args) {

        if (cmd.equals("rbf")) {
            if (args[0].equals("speed")) {
                if (args[1] != null) {
                    try {

                        double speed = Double.parseDouble(args[1]);

                        SW.rgbChat.setSpeed(speed);

                        SW.send(String.format("Der speed wurde erfolgreich auf %.4f", SW.rgbChat.getSpeed()));

                    } catch (NumberFormatException e) {
                        SW.send("Invalid argument: " + args[1]
                                + "\n Der Command muss wie Folgt aussehen rbf speed <number>");
                    }
                }
            } else if (args[0].equals("active")) {

                SW.rgbChat.setActive(!SW.rgbChat.isActive());
                SW.send("Now with rainbow effect");

            }

            return true;
        }

        return false;

    }

}
// 550

// 70+30=100