package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private static final long serialVersionUID = 7803853539866953138L;
	
	private JTable table;
	private JPanel tablePanel;
	private Icon player;
	private Icon wall;
	private Icon crate;
	private Icon target;
	private Icon ground;
	
	GUI() {
		super("Sokoban");
		setSize(1000, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("Start");

		JMenuItem menuItem = new JMenuItem("Client");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Server");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(menuItem);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuBar.add(menuItem);

		setJMenuBar(menuBar);
		
		BufferedImage groundImage = null;
		try {
			groundImage = ImageIO.read(new File("resources/ground.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage playerImage = null;
		try {
			playerImage = ImageIO.read(new File("resources/player.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage wallImage = null;
		try {
			wallImage = ImageIO.read(new File("resources/wall.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage crateImage = null;
		try {
			crateImage = ImageIO.read(new File("resources/crate.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage targetImage = null;
		try {
			targetImage = ImageIO.read(new File("resources/target.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(playerImage, 0, 0, null);
		player = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(wallImage, 0, 0, null);
		wall = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(crateImage, 0, 0, null);
		crate = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(targetImage, 0, 0, null);
		target = new ImageIcon(combined);
		
		ground = new ImageIcon(groundImage);
		
		table = new JTable(10, 10) {
			private static final long serialVersionUID = -4882552328598802471L;

			@Override
			public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		};
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setRowHeight(64);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(64);
		}
		
		tablePanel = new JPanel();
		tablePanel.add(table);
		tablePanel.setBounds(50, 50, 700, 700);
		add(tablePanel);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		GUI g = new GUI();
		GameState gameState = new GameState();
		g.onNewGameState(gameState);
	}

	@Override
	public void onNewGameState(GameState g) {
		for (int row = 0; row < table.getRowCount(); row++) {
			for (int col = 0; col < table.getColumnCount(); col++) {
				switch (g.fields[row][col]) {
				case PLAYER1: table.setValueAt(player, row, col); break;
				case PLAYER2: table.setValueAt(player, row, col); break;
				case WALL: table. setValueAt(wall, row, col); break;
				case CRATE: table.setValueAt(crate, row, col); break;
				case TARGET: table.setValueAt(target, row, col); break;
				case GROUND: table.setValueAt(ground, row, col); break;
				}
			}
		}
	}
}
