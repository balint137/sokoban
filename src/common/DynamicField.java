package common;

public class DynamicField {
    public GameState.FieldType type;
    public Coordinate from;
    public Coordinate to;

    public DynamicField(GameState.FieldType type, Coordinate from, Coordinate to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }
}
