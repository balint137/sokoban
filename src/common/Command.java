package common;

import java.io.Serializable;

/**
 * This class is used for notifying the game logic about events happened on the GUI.
 * The event can be a movement caused by a keypress, or an animation finished notification.
 */
public class Command implements Serializable {
    public CommandType command;
    public Move movePlayer1;
    public Move movePlayer2;

    /**
     * Used only for animation done command.
     * @param command command type
     */
    public Command(CommandType command) {
        this.command = command;
    }

    /**
     * Used only for player movement command. If only one player is moved, the other field is null.
     * @param command command type
     * @param movePlayer1 player 1 movement
     * @param movePlayer2 player 2 movement
     */
    public Command(CommandType command, Move movePlayer1, Move movePlayer2) {
        this.command = command;
        this.movePlayer1 = movePlayer1;
        this.movePlayer2 = movePlayer2;
    }

    /**
     * The possible command types.
     */
    public enum CommandType {KEY_PRESSED, ANIMATION_DONE}

    /**
     * The possible movement directions.
     */
    public enum Move {UP, DOWN, LEFT, RIGHT}
}
