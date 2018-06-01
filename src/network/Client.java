package network;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;
import gui.GUI;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * The client class.
 * ICommand is an interface to get information from the GUI. 
 *
 */

public class Client implements ICommand {
    
     /**
	 * IGameState is an interface that sends command information to the GUI.
	 * We need a client socket for the network communication. 
	 * The object in/outputstream reads/writes information between the socket and the client.
	 */
    
    private IGameState g;
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    /**
     * The Client constructor.
     * It creates a socket with a fixed 10007 port number and
     * an ip address, that is read through the GUI.
     * Then it creates the in/outpustream for the socket.
     * It also creates a GUI instance g for the communication interface with the GUI.
     * Finally it creates the receiver thread and starts it.
     * @param gui
     * @param ip
     */
    
    public Client(GUI gui, String ip) {
        g = gui;

        try {
            socket = new Socket(ip, 10007);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();
            Thread rec = new Thread(new ReceiverThread());
            rec.start();
        } catch (Exception e) {
            System.err.println("Couldn't get I/O for the connection.");
            System.out.println(e.getMessage());
        }
    }

    /**
     *  This function closes the in/outputstreams and the socket.
     */
    
    private void disconnect() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            System.err.println("Error while closing connections");
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * When the logic calls onCommand with a GameState parameter, 
     * this interface sends the new gamestate to the server.
     * 
     */

    @Override
    public void onCommand(Command c) {
        c.fromNetwork = true;
        send(c);
    }
    
    /**
     * The function that the command is sent with.
     * It writes the Command object c to the server's outputstream.
     * @param c
     */

    private void send(Command c) {
        if (out == null)
            return;
        try {
            System.out.println("Try sending command to server: " + c.command.toString());
            out.writeObject(c);
            System.out.println("Object write done");
            out.flush();
            System.out.println("Flush done");
        } catch (Exception e) {
            System.err.println("Send error.");
            System.out.println(e.getMessage());
        }
    }

     /**
     * The ReceiverThread is reading the socket's inputstream and when it 
     * receives the object it sends it forward to the GUI through the onNewGameState interface.
     *
     */
    
    class ReceiverThread implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("Waiting for gamestate from server");
                    GameState game = (GameState) in.readObject();
                    System.out.println("Gamestate received: " + game.type.toString());
                    g.onNewGameState(game);
                    System.out.println("Gamestate sent to GUI");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                disconnect();
                System.err.println("Server disconnected!");
            }
        }
    }
}
