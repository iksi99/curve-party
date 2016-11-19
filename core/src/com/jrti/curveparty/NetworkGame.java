package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
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
    }

    //todo matchmaking ? (ovde ili unutar nekog screena, npr. PixmapScreen#startMuliplayer, šta prikazati dok traži igru?)

    public void startGame(String userId, String gameId, final PixmapScreen screen) {
        Network.joinGame(userId, gameId, new Network.GameCallbacks() {
            boolean doInitPlayers = false;

            @Override
            public void onGameStarted(int numOfPlayers, int x, int y, int rounds, int delay, int interval) {
                players = new NetworkPlayer[numOfPlayers];
                gridX = x;
                gridY = y;
                screen.setPixmapSize(x, y);
                doInitPlayers = true;
            }

            @Override
            public void onRoundStarted() {
                screen.clearScreen();
            }

            @Override
            public void initPlayers(NetworkPlayer[] players) {
                NetworkGame.this.players = players;
                for(NetworkPlayer p : NetworkGame.this.players) {
                    List<GridPoint2> l = new ArrayList<GridPoint2>();
                    for(int i=-1;i<=1;i++) for(int j=-1; j<=1;j++) l.add(new GridPoint2((int)p.getX()+i,(int)p.getY()+j));
                    screen.drawPoints(l, p.getColor());
                }
            }

            @Override
            public void setMyId(int id) {
            }

            @Override
            public void onPlayerAdvanced(int id, int state, int x, int y, float thickness) {
                if (state != Player.STATE_DEAD) {
                    Player p = players[id];
                    List<GridPoint2> occupied = p.moveTo(x, y, thickness);
                    if (state != Player.STATE_INVISIBLE) screen.drawPoints(occupied, p.getColor());
                    System.out.println(p.getX() + " " + p.getY());
                }
                System.out.println("advance");
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
                //todo display victory screen or smth
            }

            @Override
            public void onRoundFinished(int[] scores) {
                PixmapScreen.Score[] scs = new PixmapScreen.Score[scores.length];
                for(int i=0; i<scores.length; i++) {
                    players[i].setScore(scores[i]);
                    scs[i] = new PixmapScreen.Score(players[i].getColor(), scores[i]);
                }
                screen.drawScores(scs);
            }

            @Override
            public void onError(Throwable error) {
                System.out.println("kurcina");
            }
        });
    }
}
