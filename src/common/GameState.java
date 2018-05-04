package common;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    public GameStateType type;
    public FieldType[][] staticFields; //1. index: oszlop, 2. index: sor
    public ArrayList<DynamicField> dynamicFields;
    public int elapsedTime;
    public int numberOfMovements;
    public GamePhase phase;

    public GameState(GameStateType type, GamePhase phase, FieldType[][] staticFields, ArrayList<DynamicField> dynamicFields, int elapsedTime, int numberOfMovements) {
        this.type = type;
        this.staticFields = staticFields;
        this.dynamicFields = dynamicFields;
        this.elapsedTime = elapsedTime;
        this.numberOfMovements = numberOfMovements;
        this.phase = phase;
    }

    public enum FieldType {GROUND, WALL, PLAYER1, PLAYER2, CRATE, TARGET}

    public enum GameStateType {STATIC_FIELDS, DYNAMIC_FIELDS, TIME, MOVEMENTS, PHASE_UPDATE}
    
    public enum GamePhase {GAME, WIN, LOSE}
}
