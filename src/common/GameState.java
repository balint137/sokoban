package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static gui.GUI.MAX_MAP_SIZE;

public class GameState implements Serializable {
    private static final long serialVersionUID = 7413938669227547696L;
    public FieldType[][] staticFields; //1. index: oszlop, 2. index: sor
    public ArrayList<DynamicField> dynamicFields;

    public GameState() {
        // palya rajzolas teszt
        staticFields = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];

        for (FieldType[] row : staticFields) {
            Arrays.fill(row, FieldType.GROUND);
        }

        Arrays.fill(staticFields[1], FieldType.WALL);
        Arrays.fill(staticFields[8], FieldType.WALL);

        staticFields[2][3] = FieldType.TARGET;
        staticFields[3][4] = FieldType.TARGET;
        staticFields[4][3] = FieldType.WALL;

        dynamicFields = new ArrayList<>();

        dynamicFields.add(new DynamicField(FieldType.PLAYER1, new Coordinate(3, 4), new Coordinate(1, 0)));
        dynamicFields.add(new DynamicField(FieldType.CRATE, new Coordinate(7, 2), new Coordinate(1, 0)));

    }

    public GameState(DynamicField f) {
        dynamicFields = new ArrayList<>();
        dynamicFields.add(f);
    }

    public enum FieldType {GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET}
}
