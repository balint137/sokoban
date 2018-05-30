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

public class Client implements ICommand {
    private IGameState g;
	private GameState GmSt;
	private Socket socket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;


	public Client (GUI gui, String ip) {

		g = gui;

		class ReceiverThread implements Runnable {

			public void run() {
				//System.out.println("Waiting for points...");
				try {
					while (true) {
						GmSt = (GameState) in.readObject();
						//ctrl.clickReceived(Gms);
						g.onNewGameState(GmSt);
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					System.err.println("Server disconnected!");
				} finally {
					disconnect();
				}
			}

		}
		
		try {
			socket = new Socket(ip, 10007);

			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			out.flush();
			Thread rec = new Thread(new ReceiverThread());
			rec.start();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection. ");
			JOptionPane.showMessageDialog(null, "Cannot connect to server!");
		}
	}



	

	

		class ReceiverThread implements Runnable {

			public void run() {
				//System.out.println("Waiting for points...");
				try {
					while (true) {
						GmSt = (GameState) in.readObject();
						//ctrl.clickReceived(Gms);
						g.onNewGameState(GmSt);
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					System.err.println("Server disconnected!");
				} finally {
					disconnect();
				}
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
		} catch (IOException ex) {
			System.err.println("Error while closing conn.");
		}
	}

	@Override
	public void onCommand(Command c) {
		c.fromNetwork = true;
		    send(c);
	}

	private void send(Command comm) {
		if (out == null)
			return;
		System.out.println("Send command to server");
		try {
			System.out.println("Send command to server2");
			out.writeObject(comm);
			System.out.println("Send command to server3");
			out.flush();
		} catch (IOException ex) {
			System.err.println("Send error.");
		}
	}


}//valami
