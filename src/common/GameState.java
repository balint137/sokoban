package common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is used for notifying the GUI about changes in the map, player positions, game phase, etc.
 * The static (ground, wall, target) and the movable (player, crate) fields are stored separately.
 * The number of movements is the summary of the two players' moves.
 * The phase indicates victory or defeat when the game is finished.
 */
public class GameState implements Serializable {
    public GameStateType type;
    public FieldType[][] staticFields;
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
