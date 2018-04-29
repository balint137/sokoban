package gui;

import common.DynamicField;
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

    public static final int MAX_MAP_SIZE = 10;

    private GameState.FieldType[][] fields = new GameState.FieldType[10][10];

    private final BufferedImage crateImage;
    private final BufferedImage groundImage;
    private final BufferedImage playerImage;
    private final BufferedImage targetImage;
    private final BufferedImage wallImage;

    private final int imageSize;

    private ArrayList<DynamicFieldAnimation> dynamicFieldAnimations;

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
        System.out.println(e.toString());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //draw static
            for (int col = 0; col < fields.length; col++) {
                for (int row = 0; row < fields[col].length; row++) {
                    g.drawImage(fieldToImage.get(GameState.FieldType.GROUND), col * imageSize, row * imageSize,
                            null);
                    g.drawImage(fieldToImage.get(fields[col][row]), col * imageSize, row * imageSize,
                            null);
                }
            }

            //draw dynamic
            for (DynamicFieldAnimation f : dynamicFieldAnimations) {
                g.drawImage(fieldToImage.get(f.field.type), f.field.to.getX() * imageSize - f.dx,
                        f.field.to.getY() * imageSize - f.dy, null);
            }
        }
    }

    class DynamicFieldAnimation {
        public DynamicField field;
        public int dx, dy;

        public DynamicFieldAnimation(DynamicField field) {
            this.field = field;
            dx = (field.to.getX() - field.from.getX()) * imageSize;
            dy = (field.to.getY() - field.from.getY()) * imageSize;
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
        repaint();

        dynamicFieldAnimations = new ArrayList<>();

        for (DynamicField f : g.dynamicFields) {
            dynamicFieldAnimations.add(new DynamicFieldAnimation(f));
        }

        while (!dynamicFieldAnimations.isEmpty()) {
            ListIterator<DynamicFieldAnimation> itr = dynamicFieldAnimations.listIterator();
            while (itr.hasNext()) {
                DynamicFieldAnimation f = itr.next();
                if (f.dx != 0) {
                    f.dx = f.dx > 0 ? f.dx - 1 : f.dx + 1;
                } else if (f.dy != 0) {
                    f.dy = f.dy > 0 ? f.dy - 1 : f.dy + 1;
                } else {
                    fields[f.field.to.getX()][f.field.to.getY()] = f.field.type;
                    itr.remove();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }
    }
}
