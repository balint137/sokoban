package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import gui.GUI;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;
import logic.Logic;

public class Server implements IGameState  {

	private ICommand g;
	private Command Cmd;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;

   	public Server (Logic logic){

		g = logic;

		disconnect();
		try {
			serverSocket = new ServerSocket(10007);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 10007.");
		}


		private class ReceiverThread implements Runnable {

			public void run() {
				try {
					System.out.println("Waiting for Client");
					clientSocket = serverSocket.accept();
					System.out.println("Client connected.");
				} catch (IOException e) {
					System.err.println("Accept failed.");
					disconnect();
					return;
				}

				try {
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					in = new ObjectInputStream(clientSocket.getInputStream());
					out.flush();
				} catch (IOException e) {
					System.err.println("Error while getting streams.");
					disconnect();
					return;
				}

				try {
					while (true) {
						Cmd = (Command) in.readObject();
						g.onCommand(Cmd);
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					System.err.println("Client disconnected!");
				} finally {
					disconnect();
				}
			}
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
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	@Override
	public void onNewGameState(GameState g) {
		send(g);
	}


	private void send(GameState Gs){
		if (out == null)
			return;
		//System.out.println("Sending point: " + p + " to Client");
		try {
			out.writeObject(Gs);
			out.flush();
		} catch (IOException ex) {
			System.err.println("Send error.");
		}
	}
}
