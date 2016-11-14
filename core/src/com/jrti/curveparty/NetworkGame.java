package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

/**
 * Created by luka on 14.11.16..
 */

public class NetworkGame {
    public static final boolean USE_TOUCH_COMMANDS = false; //not implemented
    public static final double TILT_THRESHOLD = 0.5;

    private NetworkPlayer[] players;
    private int gridX, gridY;

    public NetworkGame() {
        //param initialization
    }

    //todo matchmaking ? (ovde ili unutar nekog screena, npr. PixmapScreen#startMuliplayer, šta prikazati dok traži igru?)

    public void startGame(String userId, String gameId, final PixmapScreen screen) {
        Network.joinGame(userId, gameId, new Network.GameCallbacks() {
            boolean doInitPlayers = false;

            @Override
            public void onGameStarted(int numOfPlayers, int x, int y, int delay, int interval) {
                players = new NetworkPlayer[numOfPlayers];
                gridX = x;
                gridY = y;
                screen.setPixmapSize(x, y);
                doInitPlayers = true;
            }

            @Override
            public void onPlayerAdvanced(int id, int state, int x, int y, float thickness) {
                if(doInitPlayers) {
                    players[id] = new NetworkPlayer(id, x, y, NetworkGame.this);
                    doInitPlayers = false;
                } else if(state != Player.STATE_DEAD) {
                    Player           p        = players[id];
                    List<GridPoint2> occupied = p.moveTo(x, y, thickness);
                    if(state!=Player.STATE_INVISIBLE) screen.drawPoints(occupied, p.getColor());
                }
            }

            @Override
            public void onPowerUpAdded(int type, int x, int y, int timeAlive) {
                //todo add powerup on screen
            }

            @Override
            public int getTurningDirection() {
                double tilt = Gdx.input.getAccelerometerY();
                if (tilt > TILT_THRESHOLD) return DIRECTION_RIGHT;
                else if (tilt < -TILT_THRESHOLD) return DIRECTION_LEFT;
                return DIRECTION_STRAIGHT;
            }

            @Override
            public void onGameFinished() {
                //todo display result, victory screen, whatever
            }

            @Override
            public void onError(Throwable error) {
                //todo display error dialog
            }
        });
    }
}
