package com.minigames.abs;

import java.util.ArrayList;

import com.muc.ServerWorker;

public abstract class Minigame {

    final ArrayList<ServerWorker> players = new ArrayList<ServerWorker>();

    public Minigame(ServerWorker[] players) {

        for (ServerWorker sw : players) {
            this.players.add(sw);
        }
    }

    abstract public String play(String[] args);

    public ArrayList<ServerWorker> getPlayers() {
        return players;
    }

}
