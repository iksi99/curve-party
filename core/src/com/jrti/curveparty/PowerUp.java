package com.jrti.curveparty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by luka on 20.11.16..
 */

public class PowerUp {
    public static final int EDGE_TO_CENTER = 9;
    public enum Type {
        DOUBLE_SPEED(0) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.FIREBRICK));
                return l;
            }
        },
        HALF_SPEED(1) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.FOREST));
                return l;
            }
        },
        INVISIBILITY_50(2) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.SKY));
                return l;
            }
        },
        INVISIBILITY_100(3) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.BLUE));
                return l;
            }
        },
        WIDEN(4) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.BROWN));
                return l;
            }
        },
        TIGHTEN(5) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.CORAL));
                return l;
            }
        },
        QUICK_TURN(6) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.GOLD));
                return l;
            }
        },
        SLOW_TURN(7) {
            @Override
            public List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area) {
                List<PixmapScreen.ColouredPoint> l = new ArrayList<PixmapScreen.ColouredPoint>(area.size());
                for(GridPoint2 gp : area)
                    l.add(new PixmapScreen.ColouredPoint(gp, Color.GOLDENROD));
                return l;
            }
        };

        private int id;

        Type(int id) {
            this.id = id;
        }

        public abstract List<PixmapScreen.ColouredPoint> draw(Set<GridPoint2> area);
    }
    private static final Type[] types = Type.values();

    private Type            type;
    private int             alive;
    private Set<GridPoint2> area = new HashSet<GridPoint2>();

    public PowerUp(int id, int x, int y, int alive) {
        this.alive = alive;
        this.type = types[id];
        for(int i=-EDGE_TO_CENTER; i<=EDGE_TO_CENTER; i++) {
            for(int j=-EDGE_TO_CENTER; j<=EDGE_TO_CENTER; j++) {
                area.add(new GridPoint2(x+i, y+j));
            }
        }
    }

    /**
     *
     * @return true - still exists; false - doesn't exist
     */
    public boolean tick() {
        alive--;
        return alive>0;
    }

    public boolean containsPoint(GridPoint2 gp) {
        return area.contains(gp);
    }

    public List<PixmapScreen.ColouredPoint> getPixels() {
        return type.draw(area);
    }

    public Set<GridPoint2> getPoints() {
        return area;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PowerUp && ((PowerUp)o).area.iterator().next().equals(area.iterator().next());
    }
}
