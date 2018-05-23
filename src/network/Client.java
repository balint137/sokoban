package network;

import java.awt.Point;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.Scanner;
/*import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;*/
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

public class SerialClient implements Command, GameState {
    	private ICommand ICom;
	Public GameState GmSt;
	Public Command Cmd;
	
	public class SerialClient extends Network (GUI gui) {
		private ICommand ICom;
		Public GameState GmSt;
		Public Command Cmd;
		private Socket socket = null;
		private ObjectOutputStream out = null;
		private ObjectInputStream in = null;

		private IGameState g = gui
		
		SerialClient(Control c) {
			super(c);
		}

		private class ReceiverThread implements Runnable {

			public void run() {
				//System.out.println("Waiting for points...");
				try {
					while (true) {
						IGmSt = (GameState) in.readObject();
						//ctrl.clickReceived(Gms);
						g.onNewGamState(IGmSt);
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					System.err.println("Server disconnected!");
				} finally {
					disconnect();
				}
			}
		}

		@Override
		void connect(String ip) {
			disconnect();
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
    public void onCommand(Command c) {
	    SerialClient.send(c)
    }

}
