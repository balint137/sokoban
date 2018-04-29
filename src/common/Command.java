package common;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class Command implements Serializable {
    public CommandType command;
    public String mapFilePath;
    public KeyEvent lastKeyPressed;

    public Command(CommandType command, KeyEvent lastKeyPressed) {
        this.command = command;
        this.lastKeyPressed = lastKeyPressed;
    }

    public enum CommandType {
        OPEN_MAP_FILE, NEW_GAME, KEY_PRESSED
    }
}
