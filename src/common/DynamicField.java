package common;

import java.io.Serializable;

/**
 * This class stores the actual coordinates, and the necessary movement delta coordinates of a dynamic field.
 */
public class DynamicField implements Serializable {
    public GameState.FieldType type;
    public Coordinate actual;
    public Coordinate delta;

    /**
     * Used when no movement is necessary. Sets the actual coordinates, clears the delta.
     *
     * @param type   type of field
     * @param actual current position of the field
     */
    public DynamicField(GameState.FieldType type, Coordinate actual) {
        this.type = type;
        this.actual = actual;
        this.delta = new Coordinate(0, 0);
    }

    /**
     * Used when movement is necessary. Sets the actual coordinates and the delta too.
     *
     * @param type   type of field
     * @param actual current position of the field
     * @param delta  necessary movement of the field
     */

    public DynamicField(GameState.FieldType type, Coordinate actual, Coordinate delta) {
        this.type = type;
        this.actual = actual;
        this.delta = delta;
    }
}
