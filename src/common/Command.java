package common;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class Command implements Serializable {
    public CommandType command;
    public KeyEvent lastKeyPressed;

    public Command(CommandType command, KeyEvent lastKeyPressed) {
        this.command = command;
        this.lastKeyPressed = lastKeyPressed;
    }

    public Command(CommandType command) {
        this.command = command;
    }

    public enum CommandType {
        KEY_PRESSED, ANIMATION_DONE
    }
}
