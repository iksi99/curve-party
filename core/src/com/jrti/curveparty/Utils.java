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
    public static final int ALMOST_EQUAL=2;

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

    public static boolean almostEqual(int a, int b, int c, int thr) {
        return Math.abs(a-b)<=thr && Math.abs(a-c)<=thr && Math.abs(b-c)<=thr;
    }
    public static boolean almostEqual(int a, int b, int thr) {
        return Math.abs(a-b)<=thr;
    }

    public static GridPoint2[] randomPoints(Random random, int num, int x, int y, int marginX, int marginY) {
        GridPoint2[] res = new GridPoint2[num];
        for (int i = 0; i < num; i++) {
            do {
                res[i] = new GridPoint2(random.nextInt(x - 2 * marginX) + marginX,
                                        random.nextInt(y - 2 * marginY) + marginY);
            } while(isPointTooClose(res[i], res));
        }
        return res;
    }

    private static boolean isPointTooClose(GridPoint2 p, GridPoint2[] others) {
        for(GridPoint2 o : others) {
            if(o==null) break;
            if(p != o && p.dst(o) < GameState.STARTING_MIN_DISTANCE) return true;
        }
        return false;
    }
}
