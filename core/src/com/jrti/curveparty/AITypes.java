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
                                Math.toRadians(10), false, 1, 2*SEC/3, 5, STEPS_TO_90_TURN * 2);
        }
    },
    WIDE_LOOK {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN * 3,
                                Math.toRadians(60), false, 4, 2*SEC/3, 5, STEPS_TO_90_TURN * 2);
        }
    },
    LOOK_FAR {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN * 5, Math.toRadians(30), false,
                                2, SEC/2, STEPS_TO_90_TURN / 4, STEPS_TO_90_TURN * 2);
        }
    },
    RISKY {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN*4, Math.toRadians(40), true,
                                4, 3*SEC/4, 10, STEPS_TO_90_TURN/2);
        }
    },
    NO_SAFETY_CHECKS {
        @Override
        public AIPlayer generate(int id, int x, int y, double direction, GameState game) {
            return new AIPlayer(id, x, y, direction, game, STEPS_TO_90_TURN*4, Math.toRadians(30), false,
                                1, SEC/2, 5, 10);
        }
    };

    public abstract AIPlayer generate(int id, int x, int y, double direction, GameState game);
    private static AITypes[] allTypes = AITypes.values();

    public static AIPlayer generateRandom(int id, int x, int y, double direction, GameState game) {
        return allTypes[(int) (Math.random() * allTypes.length)].generate(id, x, y, direction, game);
    }
}
