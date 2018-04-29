package common;

import java.util.Objects;

import static gui.GUI.MAX_MAP_SIZE;

public class Coordinate {
    private int x, y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if (x >= 0 && x < MAX_MAP_SIZE) {
            this.x = x;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if (y >= 0 && y < MAX_MAP_SIZE) {
            this.y = y;
        } else {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return getX() == that.getX() &&
                getY() == that.getY();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getX(), getY());
    }

    public Coordinate(int x, int y) {
        setX(x);
        setY(y);
    }
}
