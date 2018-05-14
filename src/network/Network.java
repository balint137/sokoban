package network;

import java.util.Scanner;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Command;
import common.GameState;
import common.ICommand;
import common.IGameState;

public class Network implements ICommand, IGameState {
    
    public class client {
	
	    int number,temp;
		Scanner sc = new Scanner(System.in);
		Socket s = new Socket("127.0.0.1",9085);
		Scanner sc1 = new Scanner(s.getInputStream());
		System.out.println("Enter any text");
		number = sc.nextInt();
		PrintStream p =new PrintStream(s.getOutputStream());
		p.println(number);
		temp = sc1.nextInt();
		System.out.println(temp);
		
	}
    
    public class server {

		int number,temp;
		ServerSocket s1 = new ServerSocket(9085);
		Socket ss = s1.accept();
		Scanner sc = new Scanner(ss.getInputStream());
		number = sc.nextInt();
		
		temp = number + 3;
		
		PrintStream p = new PrintStream(ss.getOutputStream());
		p.println(temp);
		
	}
    
    @Override
    public void onNewGameState(GameState g) {
    }

    @Override
    public void onCommand(Command c) {
    }
}
