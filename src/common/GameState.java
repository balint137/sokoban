package common;

import java.io.Serializable;
import java.util.Arrays;

public class GameState implements Serializable {
	private static final long serialVersionUID = 7413938669227547696L;

	public enum FieldType { GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET }
	public FieldType[][] fields;
	
	public GameState() {
		// palya rajzolas teszt
		fields = new FieldType[10][10];
		
		for (FieldType[] row: fields) {
			Arrays.fill(row, FieldType.GROUND);
		}
		
		Arrays.fill(fields[1], FieldType.WALL);
		Arrays.fill(fields[7], FieldType.WALL);
		fields[2][3] = FieldType.CRATE;
		fields[3][3] = FieldType.CRATE;
		fields[5][7] = FieldType.CRATE;
		fields[4][8] = FieldType.WALL;
		fields[4][7] = FieldType.WALL;
		fields[2][6] = FieldType.TARGET;
		fields[5][5] = FieldType.PLAYER1;
	}
}
