package common;

import java.io.Serializable;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;

public class GameState implements Serializable {
	private static final long serialVersionUID = 7413938669227547696L;

	public enum FieldType { GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET }
	public FieldType[][] fields;

	public class dynamicField {
	    public FieldType type;
	    public int fromX, fromY;
	    public int toX, toY;

        public dynamicField(FieldType type, int x, int y) {
            this.type = type;
            this.fromX = this.toX = x;
            this.fromY = this.toY = y;
        }

        public dynamicField(FieldType type, int fromX, int fromY, int toX, int toY) {
            this.type = type;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    public ArrayList<dynamicField> dynamicFields = new ArrayList<dynamicField>();

	public GameState() {
		// palya rajzolas teszt
		fields = new FieldType[10][10];
		
		for (FieldType[] row: fields) {
			Arrays.fill(row, FieldType.GROUND);
		}
		
		Arrays.fill(fields[1], FieldType.WALL);
		Arrays.fill(fields[7], FieldType.WALL);


        dynamicFields.add(new dynamicField(FieldType.CRATE, 2, 3));
        dynamicFields.add(new dynamicField(FieldType.CRATE, 3, 3));
        dynamicFields.add(new dynamicField(FieldType.CRATE, 4, 5));
        dynamicFields.add(new dynamicField(FieldType.CRATE, 5, 6));
        dynamicFields.add(new dynamicField(FieldType.WALL, 3, 5));
        dynamicFields.add(new dynamicField(FieldType.PLAYER1, 5, 2, 6, 2));
	}
}
