package com.utils.chatutils.commandhandler;

import com.muc.ServerWorker;

public abstract class commandHandler {

    public ServerWorker SW;

    public commandHandler(ServerWorker SW) {
        this.SW = SW;
    }

    public abstract boolean handler(String cmd, String[] args);

}