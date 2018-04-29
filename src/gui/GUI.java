package gui;

import common.GameState;
import common.IGameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.ListIterator;
import java.util.Map;

public class GUI extends JFrame implements IGameState, KeyListener {
    private static final long serialVersionUID = 7803853539866953138L;

    private GameState.FieldType[][] fields = new GameState.FieldType[10][10];

    private final BufferedImage crateImage;
    private final BufferedImage groundImage;
    private final BufferedImage playerImage;
    private final BufferedImage targetImage;
    private final BufferedImage wallImage;

    private final int imageSize;

    private ArrayList<dynamicElement> dynamicElements;

    private DrawPanel drawPanel;

    Map<GameState.FieldType, BufferedImage> fieldToImage = new EnumMap<>(GameState.FieldType.class);

    GUI() throws IOException {
        super("Sokoban");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);

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

        crateImage = ImageIO.read(new File("resources/crate.png"));
        groundImage = ImageIO.read(new File("resources/ground.png"));
        playerImage = ImageIO.read(new File("resources/player.png"));
        targetImage = ImageIO.read(new File("resources/target.png"));
        wallImage = ImageIO.read(new File("resources/wall.png"));

        fieldToImage.put(GameState.FieldType.CRATE, crateImage);
        fieldToImage.put(GameState.FieldType.GROUND, groundImage);
        fieldToImage.put(GameState.FieldType.PLAYER1, playerImage);
        fieldToImage.put(GameState.FieldType.TARGET, targetImage);
        fieldToImage.put(GameState.FieldType.WALL, wallImage);

        imageSize = groundImage.getWidth();

        drawPanel = new DrawPanel();
        getContentPane().add(BorderLayout.CENTER, drawPanel);

        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //draw static
            for (int row = 0; row < fields.length; row++) {
                for (int col = 0; col < fields[row].length; col++) {
                    g.drawImage(groundImage, col * imageSize, row * imageSize, null);
                    g.drawImage(fieldToImage.get(fields[row][col]), col * imageSize, row * imageSize, null);
                }
            }

            //draw dynamic
            for (dynamicElement e : dynamicElements) {
                g.drawImage(e.image, e.x - e.animX, e.y - e.animY, null);
            }
        }
    }

    class dynamicElement {
        public final BufferedImage image;
        public int x, y;
        public int animX, animY;

        public dynamicElement(BufferedImage image, int x, int y, int animX, int animY) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.animX = animX;
            this.animY = animY;
        }
    }


    public static void main(String[] args) throws IOException {
        GUI g = new GUI();
        GameState gameState = new GameState();
        g.onNewGameState(gameState);
    }

    @Override
    public void onNewGameState(GameState g) {
        fields = g.fields;
        dynamicElements = new ArrayList<>();

        for (GameState.dynamicField f : g.dynamicFields) {
            if (f.fromX == f.toX && f.fromY == f.toY) {
                fields[f.fromX][f.fromY] = f.type;
            } else {
                dynamicElements.add(new dynamicElement(fieldToImage.get(f.type), f.toX * imageSize, f.toY * imageSize,
                        (f.toX - f.fromX) * imageSize, (f.toY - f.fromY) * imageSize));
            }
        }
        repaint();

        while (dynamicElements.isEmpty() == false) {
            ListIterator<dynamicElement> iterator = dynamicElements.listIterator();
            while (iterator.hasNext()) {
                dynamicElement element = iterator.next();
                if (element.animX > 0) {
                    element.animX--;
                } else if (element.animY > 0) {
                    element.animY--;
                } else {
                    iterator.remove();
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }
    }
}
