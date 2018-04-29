package common;

public class DynamicField {
    public GameState.FieldType type;
    public Coordinate actual;
    public Coordinate delta;

    public DynamicField(GameState.FieldType type, Coordinate actual) {
        this.type = type;
        this.actual = actual;
        this.delta = new Coordinate(0, 0);
    }

    public DynamicField(GameState.FieldType type, Coordinate actual, Coordinate delta) {
        this.type = type;
        this.actual = actual;
        this.delta = delta;
    }
}
