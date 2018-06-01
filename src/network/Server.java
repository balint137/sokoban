package network;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;
import logic.Logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server class.
 * IGameState is an interface to get information from the logic.
 *
 */

public class Server implements IGameState {
    /**
	 * ICommand is an interface that sends command information to the logic.
	 * We need a server and a client socket for the network communication. 
	 * The object in/outputstream reads/writes information between the socket and the server.
	 */
    private ICommand l;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    
    /**
     * The server constructor.
     * It creates a server socket on port nr. 10007 then waits for the client to connect.
     * Then it creates the in/outpustream for the client socket.
     * It also creates a logic instance l for the communication interface with the logic.
     * Finally it creates the receiver thread and starts it.
     * @param logic
     */

    public Server(Logic logic) {
        l = logic;

        try {
            serverSocket = new ServerSocket(10007);
            try {
                System.out.println("Waiting for Client");
                clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
            } catch (Exception e) {
                System.err.println("Accept failed.");
                disconnect();
                return;
            }

            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                out.flush();
                System.out.println("I/O streams OK.");
            } catch (Exception e) {
                System.err.println("Error while getting streams.");
                disconnect();
                return;
            }

            Thread rec = new Thread(new ReceiverThread());
            rec.start();
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10007.");
        }
    }

    /**
     * This function closes the in/outputstreams and sockets.
     */
    
    void disconnect() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (clientSocket != null)
                clientSocket.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch (Exception e) {
            System.out.println("Error closing server");
            System.out.println(e.getMessage());
        }
    }

    /**
     * When the logic calls onNewGameState with a GameState parameter, 
     * this interface sends the new gamestate to the client.
     * 
     */
    
    @Override
    public void onNewGameState(GameState g) {
        send(g);
    }

    /**
     * The function that the gamestate is sent with.
     * It writes the GameState object g to the client's outputstream.
     * @param g
     */
    
    
    private void send(GameState g) {
        if (out == null)
            return;
        try {
            System.out.println("Sending gamestate to client: " + g.type.toString());
            out.reset();
            out.writeObject(g);
            System.out.println("Object write done.");
            out.flush();
            System.out.println("Send done.");
        } catch (Exception e) {
            System.err.println("Send error.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * The ReceiverThread is reading the clientSocket's inputstream and when it 
     * receives the object it sends it forward to the logic through the onCommand interface.
     *
     */
    
    class ReceiverThread implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("Waiting for command...");
                    Command cmd = (Command) in.readObject();
                    System.out.println("Command received: " + cmd.command.toString());
                    l.onCommand(cmd);
                    System.out.println("Command sent to logic.");
                }
            } catch (Exception e) {
                System.out.println("Receiver exception");
                System.out.println(e.getMessage());
            } finally {
                disconnect();
                System.err.println("Client disconnected!");
            }
        }
    }
}
