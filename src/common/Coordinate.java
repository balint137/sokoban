package common;

import java.io.Serializable;

import static gui.GUI.MAX_MAP_SIZE;

/**
 * Class to store 2 dimensional coordinates.
 * Checks if the coordinate values are in range, based on the game map size.
 * Implements simple setter, getter, and add methods.
 * Overrides toString method for debugging purposes.
 */
public class Coordinate implements Serializable {
    private int x, y;

    public Coordinate(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) throws IllegalArgumentException {
        if (x > -MAX_MAP_SIZE && x < MAX_MAP_SIZE) {
            this.x = x;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) throws IllegalArgumentException {
        if (y > -MAX_MAP_SIZE && y < MAX_MAP_SIZE) {
            this.y = y;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void add(Coordinate other) {
        setX(this.x + other.x);
        setY(this.y + other.y);
    }
}