package network;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;
import gui.GUI;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;

public class SerialClient implements ICommand {
    	private IGameState g;
	private GameState GmSt;
	private Socket socket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;

		
	public class SerialClient (GUI gui, String ip) {
		
		g = gui;
		
		SerialClient(Control c) {
			super(c);
		}
		
		try {
			socket = new Socket(ip, 10007);

			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			out.flush();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection. ");
			JOptionPane.showMessageDialog(null, "Cannot connect to server!");
		}

		try {
			IGmSt = (GameState) in.readObject();
			//ctrl.clickReceived(Gms);
			g.onNewGamState(IGmSt);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.err.println("Server disconnected!");
		} finally {
			disconnect();
		}


		disconnect();
		

		
		@Override
		void disconnect() {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			} catch (IOException ex) {
				System.err.println("Error while closing conn.");
			}
		}
	}

	@Override
	public void onNewGameState(Command c) {
		    SerialClient.send(c)
	}
	
	@Override
	void send(Command comm) {
		if (out == null)
			return;
		//System.out.println("Sending point: " + p + " to Server");
		try {
			out.writeObject(comm);
			out.flush();
		} catch (IOException ex) {
			System.err.println("Send error.");
		}
	}


}
