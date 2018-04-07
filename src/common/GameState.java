package common;

import java.io.Serializable;

public class GameState implements Serializable {
	private static final long serialVersionUID = 7413938669227547696L;

	public enum FieldType { GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET }
	public FieldType[][] fields;
}
