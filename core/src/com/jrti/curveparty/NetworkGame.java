package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.github.czyzby.websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the state of a multiplayer game.
 * Created by luka on 14.11.16.
 */

public class NetworkGame {
    /**
     * The threshold after which Accelerometer input is registered
     */
    public static final double TILT_THRESHOLD = 0.5;
    /**
     * The active instance of the game class
     */
    public static CurveParty game;
    /**
     * The websocket used to connect to the server
     */
    private WebSocket socket;

    /**
     * The list of currently active powerups
     */
    private List<PowerUp> powerups = new ArrayList<PowerUp>(2);
    /**
     * The list of players in the game
     */
    private NetworkPlayer[] players;
    /**
     * The size in the X and Y axes of the game field
     */
    private int gridX, gridY;

    /**
     * True if the player is turning left in the current frame
     */
    private boolean isTurningLeft = false;
    /**
     * True if the player is turning right in the current frame
     */
    private boolean isTurningRight = false;

    /**
     * Screen width of the device the application is running on
     */
    private int width = Gdx.graphics.getWidth();

    public NetworkGame() {
    }

    /**
     * Initializes the network game and handles game logic using callbacks from {@link com.jrti.curveparty.Network.MatchmakingCallbacks}.
     * @param userId A number used to differentiate multiple users
     * @param gameId A number used by the server to differentiate different games
     * @param screen The screen from which the method was called
     * @param game The instance of the game class
     * @return Returns the socket used for connecting to the server
     */
    public WebSocket startGame(String userId, String gameId, final PixmapScreen screen, final CurveParty game) {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (x <= width / 2) {
                    isTurningLeft = true;
                } else {
                    isTurningRight = true;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                isTurningLeft = false;
                isTurningRight = false;
                return true;
            }
        });

        socket = Network.joinGame(userId, gameId, new Network.GameCallbacks() {
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
                if(myId != -1) players[myId].setColor(Color.WHITE);
                for(NetworkPlayer p : NetworkGame.this.players) {
                    List<GridPoint2> l = new ArrayList<GridPoint2>();
                    for(int i=-2;i<=2;i++) for(int j=-2; j<=2;j++) l.add(new GridPoint2((int)p.getX()+i,(int)p.getY()+j));
                    screen.drawPoints(l, p.getColor());
                }
                screen.getPlayers(players);
            }

            @Override
            public void setMyId(int id) {
                myId = id;
                if(players != null) players[id].setColor(Color.WHITE);
            }

            @Override
            public void onPlayerAdvanced(int id, int state, int x, int y, float thickness) {
                screen.getPlayers(players);
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
                                    screen.drawPowerups(powerups);
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
                        screen.drawPowerups(powerups);
                    }/* else {
                        screen.drawSprite(powerups.get(i).getTexture(), powerups.get(i).getX(), powerups.get(i).getY());
                    }*/
                }
            }

            @Override
            public void onPowerUpAdded(int type, int x, int y, int timeAlive) {
                PowerUp pu = new PowerUp(type, x, y, timeAlive);
                powerups.add(pu);
                screen.drawPoints(pu.getPixels());
                screen.drawPowerups(powerups);
                //screen.drawSprite(pu.getTexture(), pu.getX(), pu.getY());
            }

            @Override
            public int getTurningDirection() {
                if(game.useTouchCommands == true) {
                    if(isTurningLeft) return DIRECTION_LEFT;
                    else if(isTurningRight) return DIRECTION_RIGHT;
                    return DIRECTION_STRAIGHT;
                } else {
                    double tilt = Gdx.input.getAccelerometerY();
                    if (tilt > TILT_THRESHOLD) return DIRECTION_RIGHT;
                    else if (tilt < -TILT_THRESHOLD) return DIRECTION_LEFT;
                    return DIRECTION_STRAIGHT;
                }

            }

            @Override
            public void onGameFinished() {
                game.setScreen(new VictoryScreen(game, players));
            }

            @Override
            public void onRoundFinished(int[] scores) {
                PixmapScreen.Score[] scs = new PixmapScreen.Score[scores.length];
                for(int i=0; i<scores.length; i++) {
                    players[i].setScore(scores[i]);
                    scs[i] = new PixmapScreen.Score(players[i].getColor(), scores[i]);
                }
                screen.drawScores(scs);
                powerups = new ArrayList<PowerUp>();
                screen.drawPowerups(powerups);
            }

            @Override
            public void onError(Throwable error) {
                screen.showError();
            }
        });
        return socket;
    }

    /**
     * Closes the socket
     */
    public void terminateGame() {
        if(socket != null && socket.isOpen()) {
            socket.close();
        }
    }

    /**
     *
     * @return Returns the list of powerups currently active
     */
    private List<PowerUp> getPowerups() {
        return powerups;
    }
}
