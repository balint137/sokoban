package common;

/**
 * Interface for communications from GUI to Logic, and from GUI to Client.
 */
public interface ICommand {
    void onCommand(Command c);
}
