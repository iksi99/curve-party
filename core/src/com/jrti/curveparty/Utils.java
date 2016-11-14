package com.jrti.curveparty;

import com.badlogic.gdx.math.GridPoint2;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by luka on 14.11.16..
 */

public class Utils {
    static Set<GridPoint2> bresenham(int x0, int y0, int x1, int y1) {
        Set<GridPoint2> result = new HashSet<GridPoint2>(4);
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
}
