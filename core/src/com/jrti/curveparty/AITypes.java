package com.jrti.curveparty;

import static com.jrti.curveparty.GameState.SEC;
import static com.jrti.curveparty.Player.STEPS_TO_90_TURN;

/**
 * Created by luka on 24.11.16..
 */

public enum AITypes {
    DEFAULT {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game);
        }
    },
    TUNNEL_VISION {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN * 3,
                                Math.toRadians(10), false, (int)(SEC/1.5), 5, STEPS_TO_90_TURN * 2);
        }
    },
    WIDE_LOOK {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN * 3,
                                Math.toRadians(50), false, (int)(SEC/1.5), 5, STEPS_TO_90_TURN * 2);
        }
    },
    LOOK_FAR {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN * 5, Math.toRadians(30), false,
                                SEC/2, STEPS_TO_90_TURN / 4, STEPS_TO_90_TURN * 2);
        }
    },
    RISKY {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN*4, Math.toRadians(40), true,
                                (int) (SEC * 0.75), 10, STEPS_TO_90_TURN/2);
        }
    },
    NO_SAFETY_CHECKS {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN*4, Math.toRadians(30), false,
                                SEC/2, 5, 10);
        }
    };

    public abstract AIPlayer generate(int id, int x, int y, double direction, GameState game);
    private static AITypes[] allTypes = AITypes.values();

    public static AIPlayer generateRandom(int id, int x, int y, double direction, GameState game) {
        return allTypes[(int) (Math.random() * allTypes.length)].generate(id, x, y, direction, game);
    }
}
