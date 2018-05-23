package common;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    public GameStateType type;
    public FieldType[][] staticFields; //1. index: oszlop, 2. index: sor
    public ArrayList<DynamicField> dynamicFields;
    public int numberOfMovements;
    public GamePhase phase;
    public String highscores;

    public GameState(GameStateType type, FieldType[][] staticFields) {
        this.type = type;
        this.staticFields = staticFields;
    }

    public GameState(GameStateType type, ArrayList<DynamicField> dynamicFields) {
        this.type = type;
        this.dynamicFields = dynamicFields;
    }

    public GameState(GameStateType type, int numberOfMovements) {
        this.type = type;
        this.numberOfMovements = numberOfMovements;
    }

    public GameState(GameStateType type, GamePhase phase) {
        this.type = type;
        this.phase = phase;
    }

    public GameState(GameStateType type, String highscores) {
        this.type = type;
        this.highscores = highscores;
    }

    public enum FieldType {GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET}

    public enum GameStateType {STATIC_FIELDS, DYNAMIC_FIELDS, MOVEMENTS, PHASE_UPDATE, HIGHSCORES}

    public enum GamePhase {WIN, LOSE}
}
