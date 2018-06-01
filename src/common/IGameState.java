package common;

/**
 * Interface for communications from Logic to GUI, and from Logic to Server.
 */
public interface IGameState {
    void onNewGameState(GameState g);
}
