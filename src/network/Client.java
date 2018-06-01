package network;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;
import gui.GUI;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements ICommand {
    private IGameState g;
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;


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

    @Override
    public void onCommand(Command c) {
        send(c);
    }

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
