package common;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class Command implements Serializable {
    public CommandType command;
    public int lastKeyPressed;
    public KeyboardSetting player1;
    public KeyboardSetting player2;
    public boolean fromNetwork = false;

    public Command(CommandType command, int lastKeyPressed) {
        this.command = command;
        this.lastKeyPressed = lastKeyPressed;
    }

    public Command(CommandType command) {
        this.command = command;
    }

    public Command(CommandType command, KeyboardSetting player1, KeyboardSetting player2) {
        this.command = command;
        this.player1 = player1;
        this.player2 = player2;
    }


    public enum CommandType {KEY_PRESSED, ANIMATION_DONE, KEY_MAP}

    public enum KeyboardSetting {WASD, IJKL, ARROWS}
}
