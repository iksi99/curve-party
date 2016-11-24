package com.jrti.curveparty;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jrti.curveparty.GameState.MAX_INVISIBILITY_DURATION;
import static com.jrti.curveparty.GameState.MIN_INISIBILITY_DURATION;

/**
 * Created by luka on 14.11.16..
 */

public class Utils {
    public static List<GridPoint2> bresenham(int x0, int y0, int x1, int y1) {
        List<GridPoint2> result = new ArrayList<GridPoint2>(4);
        if(Math.abs(x1-x0) <= 1 && Math.abs(y1-y0) <= 1) {
            result.add(new GridPoint2(x1, y1));
            return result;
        }
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = (dx > dy ? dx : -dy) / 2, e2;

        for (; ; ) {
            result.add(new GridPoint2(x0, y0));
            if (x0 == x1 && y0 == y1) break;
            e2 = err;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dy) {
                err += dx;
                y0 += sy;
            }
        }
        return result;
    }

    public static int rollInvisible(Random rnd) {
        if(rnd.nextDouble() < GameState.INVISIBILITY_CHANCE) {
            return rnd.nextInt(MAX_INVISIBILITY_DURATION-MIN_INISIBILITY_DURATION)+MIN_INISIBILITY_DURATION;
        } else {
            return 0;
        }
    }
}
