package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 14.11.16..
 */

public class NetworkGame {
    public static final boolean USE_TOUCH_COMMANDS = false; //not implemented
    public static final double TILT_THRESHOLD = 0.5;

    private List<PowerUp> powerups = new ArrayList<PowerUp>(2);
    private NetworkPlayer[] players;
    private int gridX, gridY;

    public NetworkGame() {
    }

    public void startGame(String userId, String gameId, final PixmapScreen screen) {
        Network.joinGame(userId, gameId, new Network.GameCallbacks() {
            int myId=-1;

            @Override
            public void onGameStarted(int numOfPlayers, int x, int y, int rounds, int delay, int interval) {
                players = new NetworkPlayer[numOfPlayers];
                gridX = x;
                gridY = y;
                screen.setPixmapSize(x, y);
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
                if(myId != -1) players[myId].setColor(Color.WHITE);
            }

            @Override
            public void setMyId(int id) {
                myId = id;
                if(players != null) players[id].setColor(Color.WHITE);
            }

            @Override
            public void onPlayerAdvanced(int id, int state, int x, int y, float thickness) {
                if (state != Player.STATE_DEAD) {
                    Player p = players[id];
                    List<GridPoint2> occupied = p.moveTo(x, y, thickness);
                    //System.out.println("advancing to " + x + "," + y);
                    if (state != Player.STATE_INVISIBLE) {
                        screen.drawPoints(occupied, p.getColor());
                        boolean gotPowerup = false;
                        for(GridPoint2 gp : occupied) {
                            for (int i=0; i<powerups.size(); i++) {
                                if (powerups.get(i).containsPoint(gp)) {
                                    screen.clearPoints(powerups.get(i).getPoints());
                                    powerups.remove(i);
                                    gotPowerup = true;
                                    break;
                                }
                            }
                            if(gotPowerup) break;
                        }
                    }
                }
                for(int i=0; i<powerups.size(); i++) {
                    if(!powerups.get(i).tick()) {
                        screen.clearPoints(powerups.get(i).getPoints());
                        powerups.remove(i);
                        i--;
                    }
                }
            }

            @Override
            public void onPowerUpAdded(int type, int x, int y, int timeAlive) {
                PowerUp pu = new PowerUp(type, x, y, timeAlive);
                powerups.add(pu);
                screen.drawPoints(pu.getPixels());
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
