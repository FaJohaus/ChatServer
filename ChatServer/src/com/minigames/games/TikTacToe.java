package com.minigames.games;

import com.minigames.abs.Minigame;
import com.muc.ServerWorker;

public class TikTacToe extends Minigame {

    private int[] playboard = new int[] { -1, -1, -1, /** */
            -1, -1, -1, /** */
            -1, -1, -1, };

    public TikTacToe(ServerWorker[] players) {
        super(players);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String play(String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
