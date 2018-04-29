package common;

import java.io.Serializable;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;

import static gui.GUI.MAX_MAP_SIZE;

public class GameState implements Serializable {
	private static final long serialVersionUID = 7413938669227547696L;

	public enum FieldType { GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET }
	public FieldType[][] fields; //1. index: oszlop, 2. index: sor
    public ArrayList<DynamicField> dynamicFields;


	public GameState() {
		// palya rajzolas teszt
		fields = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
		
		for (FieldType[] row: fields) {
			Arrays.fill(row, FieldType.GROUND);
		}
		
		Arrays.fill(fields[1], FieldType.WALL);
		Arrays.fill(fields[8], FieldType.WALL);

		fields[2][3] = FieldType.CRATE;
		fields[3][3] = FieldType.CRATE;
		fields[4][3] = FieldType.WALL;

		dynamicFields = new ArrayList<>();

        dynamicFields.add(new DynamicField(FieldType.PLAYER1, new Coordinate(3, 4), new Coordinate(3, 5)));
        dynamicFields.add(new DynamicField(FieldType.CRATE, new Coordinate(7, 2), new Coordinate(6, 2)));

	}
}
