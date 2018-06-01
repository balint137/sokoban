package common;

import java.io.Serializable;

public class Command implements Serializable {
    public CommandType command;
    public Move movePlayer1;
    public Move movePlayer2;
    public boolean fromNetwork = false;

    public Command(CommandType command) {
        this.command = command;
    }

    public Command(CommandType command, Move movePlayer1, Move movePlayer2) {
        this.command = command;
        this.movePlayer1 = movePlayer1;
        this.movePlayer2 = movePlayer2;
    }

    public enum CommandType {KEY_PRESSED, ANIMATION_DONE}

    public enum Move {UP, DOWN, LEFT, RIGHT}
}
