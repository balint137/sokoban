package common;

import static gui.GUI.MAX_MAP_SIZE;

public class Coordinate {
    private int x, y;

    public Coordinate(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) throws IllegalArgumentException {
        if (x >= 0 && x < MAX_MAP_SIZE) {
            this.x = x;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) throws IllegalArgumentException {
        if (y >= 0 && y < MAX_MAP_SIZE) {
            this.y = y;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }
}
