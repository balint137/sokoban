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
	
	GUI() {
		super("Sokoban");
		setSize(700, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		
		//create menu
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
		
		//load pictures
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
		Icon player = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(wallImage, 0, 0, null);
		Icon wall = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(crateImage, 0, 0, null);
		Icon crate = new ImageIcon(combined);
		
		combined = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		graphics = combined.getGraphics();
		graphics.drawImage(groundImage, 0, 0, null);
		graphics.drawImage(targetImage, 0, 0, null);
		Icon target = new ImageIcon(combined);
		
		Icon ground = new ImageIcon(groundImage);

		
        String[] columnNames = {"1", "2", "3", "4", "5"};
        Object[][] data = {
            {wall, wall, wall, wall, wall},
            {wall, ground, wall, crate, wall},
            {wall, ground, ground, ground, wall},
            {wall, target, crate, player, wall},
            {wall, wall, wall, wall, wall},
        };
		
		JTable table = new JTable(data, columnNames) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		};
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(64);
		columnModel.getColumn(1).setPreferredWidth(64);		
		columnModel.getColumn(2).setPreferredWidth(64);
		columnModel.getColumn(3).setPreferredWidth(64);
		columnModel.getColumn(4).setPreferredWidth(64);
		table.setRowHeight(64);
		table.setIntercellSpacing(new Dimension(0, 0));
		
		JPanel tablePanel = new JPanel();
		tablePanel.add(table);
		tablePanel.setBounds(100, 100, 350, 350);
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
