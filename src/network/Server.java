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

public class Server implements IGameState {
    private ICommand l;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

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

    @Override
    public void onNewGameState(GameState g) {
        send(g);
    }

    private void send(GameState g) {
        if (out == null)
            return;
        try {
            System.out.println("Sending gamestate to client: " + g.type.toString());
            out.writeObject(g);
            System.out.println("Object write done.");
            out.flush();
            System.out.println("Send done.");
        } catch (Exception e) {
            System.err.println("Send error.");
            System.out.println(e.getMessage());
        }
    }

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
