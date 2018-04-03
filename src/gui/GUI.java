package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

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
		
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File("icon.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Icon icon = new ImageIcon(myPicture.getScaledInstance(80, 80, Image.SCALE_FAST));
		
        String[] columnNames = {"1", "2", "3"};
        Object[][] data = {
            {icon, icon, icon},
            {icon, icon, icon},
            {icon, icon, icon},
        };
		
		JTable table = new JTable(data, columnNames) {
			public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		};
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(80);
		columnModel.getColumn(1).setPreferredWidth(80);		
		columnModel.getColumn(2).setPreferredWidth(80);
		table.setRowHeight(80);
		table.setIntercellSpacing(new Dimension(0, 0));
		
		JPanel tablePanel = new JPanel();
		tablePanel.add(table);
		tablePanel.setBounds(100, 100, 300, 300);
		tablePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		add(tablePanel);
		
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
