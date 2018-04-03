package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import common.GameState;
import common.IGameState;

public class GUI extends JFrame implements IGameState {

	private static final long serialVersionUID = 1L;
	
	GUI() {
		super("Sokoban");
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Start");
		JMenuItem menuItem = new JMenuItem("Client");
		menu.add(menuItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		
		JPanel inputPanel = new JPanel();
		JButton btn1 = new JButton("1");
		JButton btn2 = new JButton("2");
		JButton btn3 = new JButton("3");
		JButton btn4 = new JButton("4");
		inputPanel.setBounds(30, 30, 200, 200);
		inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
		inputPanel.setLayout(new GridLayout(2, 2, 0, 0));
		inputPanel.add(btn1);
		inputPanel.add(btn2);
		inputPanel.add(btn3);
		inputPanel.add(btn4);
		add(inputPanel);
		
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("icon.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(100, 100, Image.SCALE_FAST)));
		picLabel.setBounds(100, 300, 100, 100);
		add(picLabel);
		
		Icon icon = new ImageIcon(myPicture);
		
        String[] columnNames = {"Picture", "Description"};
        Object[][] data = {
            {icon, "1"},
            {icon, "2"},
            {icon, "3"},
        };
		
		JTable table = new JTable(data, columnNames) {
			public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		};
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(250, 100, 200, 200);
		add(scrollPane);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		GUI g = new GUI();
		System.out.println("hello");
	}

	@Override
	public void onNewGameState(GameState g) {
		// TODO Auto-generated method stub
		
	}

}
