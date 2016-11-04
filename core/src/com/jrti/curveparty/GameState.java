package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cactoss on 2.11.2016..
 */

public class GameState {
    private static final String TAG = "CurveParty.State";

    public static final Color[] PLAYER_COLORS = {Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN};

    private int numOfPlayers;


    private Rectangle[][] gameMatrix;
    private boolean[][] occupiedFields;
    private List<Player> playerList = new ArrayList<Player>();

    public GameState(int x, int y, int numOfPlayers)
    {
        gameMatrix = new Rectangle[x][y];
        occupiedFields = new boolean[x][y];
        this.numOfPlayers = numOfPlayers;

        for (int i = 0; i < x; i++)
        {
            for (int j = 0; j < y; j++)
            {
                //System.out.println("0");
                gameMatrix[i][j] = new Rectangle(i, j, 1, 1);
            }
        }

        makePlayers(1);
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Rectangle[][] getGameMatrix() {
        return gameMatrix;
    }

    public boolean[][] getOccupiedFields() {
        return occupiedFields;
    }

    public boolean isOccupied(int x, int y)
    {
        Gdx.app.debug(TAG, "isOccupied " + x + " " + y + ": " + occupiedFields[x][y]);
        return occupiedFields[x][y];
    }

    public void setOccupied(int x, int y)
    {
        if(Gdx.app != null) Gdx.app.debug(TAG, "setOccupied " + x + " " + y);
        occupiedFields[x][y] = true;
    }

    public void makePlayers(int id) {
            playerList.add(new Player(100, 100, PLAYER_COLORS[id], this));
    }
}
