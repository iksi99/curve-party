package com.jrti.curveparty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by luka on 6.11.16.
 */

public class Network {
    public static final String HOST  = "79.101.8.7";
    public static final String FIND  = "/find";
    public static final String START = "/start/";

    private static final String JSON_MM_NAME    = "name";
    private static final String JSON_MM_ID      = "id";
    private static final String JSON_MM_ROOM_ID = "roomId";

    private static final String JSON_GI_PLAYERS           = "players";
    private static final String JSON_GI_X                 = "x";
    private static final String JSON_GI_Y                 = "y";
    private static final String JSON_GI_TIMESTEP_DELAY    = "delay";
    private static final String JSON_GI_TIMESTEP_INTERVAL = "interval";

    private static final String JSON_PI_ID        = "id";
    private static final String JSON_PI_STATE     = "state";
    private static final String JSON_PI_X         = "x";
    private static final String JSON_PI_Y         = "y";
    private static final String JSON_PI_THICKNESS = "thk";
//    private static final String JSON_PI_DIRECTION = "dir";
//    private static final String JSON_PI_SPEED     = "speed";

    private static final String JSON_PU_X          = "x";
    private static final String JSON_PU_Y          = "y";
    private static final String JSON_PU_TYPE       = "type";
    private static final String JSON_PU_TIME_ALIVE = "ttl";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    //================== INTERFACES (callbacks) ==============================================================

    /**
     * Komunikacija sa pozivaocem {@link #findGame(String, MatchmakingCallbacks)}. Posao websocket-a se izvršava
     * u background thread-u, ali se metode iz ovog interfejsa pozivaju sa main thread-a (tako da su UI-safe).
     * Implementirati ili tako da klasa u kojoj se nalazi pozivalac implementuje ovaj interfejs i prosleđuje this,
     * ili preko anonimne unutrašnje klase
     * <pre>new MatchmakingCallbacks() { @Override void onGameFound(String, String, String) ... }</pre>.
     */
    public interface MatchmakingCallbacks {
        /**
         * Izvršava se kada je igra nađena, neposredno pre zatvaranja socket-a i završavanja ove metode. id-ovi
         * se prosleđuju {@link #joinGame(String, String, GameCallbacks)} metodi, koja pridružuju igrača igri.
         *
         * @param nickname nickname korisnika uređaja
         * @param id       id korisnika na serveru
         * @param gameId   id igre kojoj se priključuje
         */
        void onGameFound(String nickname, String id, String gameId);

        /**
         * U slučaju da dođe do greške u uspostavljanju konekcije ili tako nečem
         *
         * @param error uzrok greške, printovati stacktrace za više detalja (ako je instanceof Error, re-throw-ovati
         *              ga, inače ispitati)
         */
        void onError(Throwable error);
    }


    /**
     * Komunikacija sa pozivaocem {@link #joinGame(String, String, GameCallbacks)}, ujedno i tick za igru.
     */
    public interface GameCallbacks {
        int DIRECTION_LEFT = 0, DIRECTION_STRAIGHT = 1, DIRECTION_RIGHT = 2;

        /**
         * Poziva se u trenutku počinjanja igre sa svim podacima o istoj (za sada konstante, ali idealno bi bilo
         * kodirati za opšti slučaj i koristiti promenljive; v. klasu GameRoom na serveru), koristiti po potrebi
         *
         * @param numOfPlayers broj igrača
         * @param x            veličina grida (x osa)
         * @param y            veličina grida (y osa)
         * @param delay        inicijalno zakašnjenje (do drugog koraka), u milisekundama
         * @param interval     interval između svakog narednog koraka, u milisekundama
         */
        void onGameStarted(int numOfPlayers, int x, int y, int delay, int interval);

        /**
         * Poziva se u regularnim intervalima (u svakom tick-u), sa novim podacima za svakog igrača
         *
         * @param id    id igrača na koga se podaci odnose
         * @param state 0, 1 ili 2 u zavisnosti da li je visible, invisible ili dead
    //   * @param direction   smer kretanja (u radijanima)
    //   * @param speed brzina kretanja
         * @param x     nova x koordinata igrača (pretpostaviti da se kretao pravolinijski od prethodne)
         * @param y     nova y koordinata igrača (pretpostaviti da se kretao pravolinijski od prethodne)
         */
        void onPlayerAdvanced(int id, int state, int x, int y, double thickness);

        /**
         * Poziva se kada treba dodati powerup na ekran. Veličina je konstantna. Bilo bi lepo da ikonica bude krug,
         * bez obzira što se za potrebe collision detectiona uzima kvadrat. Pošto ne postoji onPowerUpRemoved,
         * treba proveravati i tickove (smanjivati pri svakom) i collision detection u odnosu na sve igrače, tako
         * da se powerup skloni sa mape u slučaju da ga neko pokupi.
         * @param type videti PowerUp klasu na serveru, PowerUp.Type#id
         * @param x x koordinata centra
         * @param y y koordinata centra
         * @param timeAlive vreme koliko ovaj powerup treba da postoji (tickova, iliti poziva onPlayerAdvanced metodi)
         */
        void onPowerUpAdded(int type, int x, int y, int timeAlive);

        /**
         * Treba da vrati u kom smeru igrač skreće. Poziva se u regularnom intervalu (v.
         * {@link #onGameStarted(int, int, int, int, int)}), nakon svih onPlayerAdvanced
         *
         * @return jedno od {@link #DIRECTION_LEFT}, {@link #DIRECTION_STRAIGHT}, {@link #DIRECTION_RIGHT}
         */
        int getTurningDirection();

        /**
         * Poziva se pri zatvaranju socketa da označi kraj igre
         */
        void onGameFinished(); //todo multiple rounds, see winner

        /**
         * Poziva se u slučaju greške, npr. ako se ne može konektovati
         *
         * @param error Throwable koji je uzrokovao grešku (ako je instanceof Error, re-throw-ovati ga, inače ispitati)
         */
        void onError(Throwable error);
    }


    //====================== METHODS ======================================================================

    /**
     * Pokreće pretragu za igrom u pozadini. O nađenoj igri obaveštava pozivaoca preko callback-ova. Pošto gdx
     * ne pruža mogućnost da vidim u kom thread-u se trenutno nalazim i uporedim to s UI thread-om, sadržaj ove
     * metode uvek ide u background (tj. očekuje da bude pozivana sa UI threada)
     *
     * @param nickname nickname koji će igrač koristiti u igri, trenutno se ne prikazuje nigde niti ga server šalje
     */
    public static void findGame(final String nickname, final MatchmakingCallbacks callbacks) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                WebSocket mmSocket = WebSockets.newSocket(HOST + FIND);
                mmSocket.addListener(new WebSocketListener() {
                    @Override
                    public boolean onOpen(WebSocket webSocket) {
                        JsonValue json = new JsonValue(nickname);
                        json.setName(JSON_MM_NAME);
                        webSocket.send(json.toString());
                        return FULLY_HANDLED;
                    }

                    @Override
                    public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                        return NOT_HANDLED;
                    }

                    @Override
                    public boolean onMessage(WebSocket webSocket, String packet) {
                        JsonReader   reader = new JsonReader();
                        JsonValue    json   = reader.parse(packet);
                        final String id     = json.getString(JSON_MM_ID);
                        final String roomId = json.getString(JSON_MM_ROOM_ID);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onGameFound(nickname, id, roomId);
                            }
                        });
                        return FULLY_HANDLED;
                    }

                    @Override
                    public boolean onMessage(WebSocket webSocket, byte[] packet) {
                        return NOT_HANDLED;
                    }

                    @Override
                    public boolean onError(WebSocket webSocket, final Throwable error) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onError(error);
                            }
                        });
                        return FULLY_HANDLED;
                    }
                });
                mmSocket.connect();
            }
        });
    }


    /**
     * Priključuje se igri sa datim id-ovima. Komunicira sa pozivaocem preko {@link GameCallbacks}, obaveštavajući ga
     * o promenama u stanju igrača.
     *
     * @param id
     * @param roomId
     * @param callbacks
     */
    public static void joinGame(final String id, final String roomId, final GameCallbacks callbacks) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                final WebSocket gameSocket = WebSockets.newSocket(HOST + START + roomId + "/" + id);
                gameSocket.addListener(new WebSocketListener() {
                    boolean gameStarted = false;
                    int players;

                    @Override
                    public boolean onOpen(WebSocket webSocket) {
                        return NOT_HANDLED;
                    }

                    @Override
                    public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onGameFinished();
                            }
                        });
                        return FULLY_HANDLED;
                    }

                    @Override
                    public boolean onMessage(final WebSocket webSocket, String packet) {
                        JsonReader reader = new JsonReader();
                        JsonValue  json   = reader.parse(packet);
                        int        l      = json.size;
                        if (!gameStarted) {
                            final JsonValue gameInfo = json.get(l - 1);
                            final int players = gameInfo.getInt(JSON_GI_PLAYERS),
                                    x = gameInfo.getInt(JSON_GI_X),
                                    y = gameInfo.getInt(JSON_GI_Y),
                                    intv = gameInfo.getInt(JSON_GI_TIMESTEP_INTERVAL),
                                    delay = gameInfo.getInt(JSON_GI_TIMESTEP_DELAY);
                            this.players = players;
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    callbacks.onGameStarted(players, x, y, delay, intv);
                                }
                            });
                            gameStarted = true;
                            l--;
                        }
                        for (int i = 0; i < this.players; i++) {
                            final JsonValue playerInfo = json.get(i);
                            final int id = playerInfo.getInt(JSON_PI_ID), state = playerInfo.getInt(JSON_PI_STATE), x, y;
                            if(state != Player.STATE_DEAD) {
                                x = playerInfo.getInt(JSON_PI_X);
                                y = playerInfo.getInt(JSON_PI_Y);
                            } else {
                                x=y=-1;
                            }
                            final double thickness = playerInfo.getDouble(JSON_PI_THICKNESS);
                            //final double dir = playerInfo.getInt(JSON_PI_DIRECTION),
                            //        spd = playerInfo.getInt(JSON_PI_SPEED);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    callbacks.onPlayerAdvanced(id, state, x, y, thickness);
                                }
                            });
                        }
                        if(this.players != l) {
                            final JsonValue powerupInfo = json.get(l-1);
                            final int x = powerupInfo.getInt(JSON_PU_X),
                                    y = powerupInfo.getInt(JSON_PU_Y),
                                    type = powerupInfo.getInt(JSON_PU_TYPE),
                                    timeAlive = powerupInfo.getInt(JSON_PU_TIME_ALIVE);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    callbacks.onPowerUpAdded(type, x, y, timeAlive);
                                }
                            });
                        }
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                final int dir
                                        = callbacks.getTurningDirection(); //jer koliko se sećam za interakciju
                                //s ulazom i izlazom moramo biti na UI thread-u, ali za networking u background-u
                                //a pošto je razumno očekivati da će ova metoda komunicirati sa spoljnim svetom
                                //(dodir ili nagib uređaja), wrap-ujem sve u postRunnable
                                executor.submit(new Runnable() { //jer komunikacija sa serverom ide u pozadinu
                                    @Override
                                    public void run() {
                                        webSocket.send(String.valueOf(dir));
                                    }
                                });
                            }
                        });
                        return FULLY_HANDLED;
                    }

                    @Override
                    public boolean onMessage(WebSocket webSocket, byte[] packet) {
                        return NOT_HANDLED;
                    }

                    @Override
                    public boolean onError(WebSocket webSocket, final Throwable error) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onError(error);
                            }
                        });
                        return FULLY_HANDLED;
                    }
                });
                gameSocket.connect();
            }
        });
    }
}
