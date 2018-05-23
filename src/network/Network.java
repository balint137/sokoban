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

public class Network implements Command, GameState {
    	private ICommand ICom;
	Public GameState GmSt;
	Public Command Cmd;
	
	public class SerialClient extends Network (GUI gui) {

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

		
	
    
   	public class SerialServer extends Network (GUI gui){

		private ServerSocket serverSocket = null;
		private Socket clientSocket = null;
		private ObjectOutputStream out = null;
		private ObjectInputStream in = null;

		private ICommand g = GUI;
		
		SerialServer(Control c) {
			super(c);
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
						ctrl.clickReceived(Cmd);
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					System.err.println("Client disconnected!");
				} finally {
					disconnect();
				}
			}
		}

		@Override
		void connect(String ip) {
			disconnect();
			try {
				serverSocket = new ServerSocket(10007);

				Thread rec = new Thread(new ReceiverThread());
				rec.start();
			} catch (IOException e) {
				System.err.println("Could not listen on port: 10007.");
			}
		}

		@Override
		void send(GameState Gs){
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

	   	@Override
	   	public void onNewGameState(GameState g) {
	   		SerialServer.send(g)
	  	}

		@Override
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
				Logger.getLogger(SerialServer.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}

		
	}
    

    @Override
    public void onCommand(Command c) {
	    SerialClient.send(c)
    }

}
