package gui;

import common.*;
import common.GameState.FieldType;

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
import java.util.Map;

public class GUI extends JFrame implements IGameState, KeyListener {
    public static final int MAX_MAP_SIZE = 10;
    private static final long serialVersionUID = 7803853539866953138L;
    private int imageSize;
    private Map<FieldType, BufferedImage> fieldToImage = new EnumMap<>(FieldType.class);
    private FieldType[][] fields = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
    private ArrayList<DynamicFieldAnimation> dynamicFieldAnimations;
    private ICommand logic;

    GUI(ICommand logic) throws IOException {
        super("Sokoban");
        this.logic = logic;
        setSize(1000, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);

        BuildMenu();
        ReadResourceImages();

        DrawPanel drawPanel = new DrawPanel();
        getContentPane().add(BorderLayout.CENTER, drawPanel);

        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        GUI g = new GUI(null);
        GameState gameState = new GameState();
        g.onNewGameState(gameState);
    }

    private void ReadResourceImages() throws IOException {
        final BufferedImage crateImage = ImageIO.read(new File("resources/crate.png"));
        final BufferedImage groundImage = ImageIO.read(new File("resources/ground.png"));
        final BufferedImage playerImage = ImageIO.read(new File("resources/player.png"));
        final BufferedImage targetImage = ImageIO.read(new File("resources/target.png"));
        final BufferedImage wallImage = ImageIO.read(new File("resources/wall.png"));

        fieldToImage.put(FieldType.CRATE, crateImage);
        fieldToImage.put(FieldType.GROUND, groundImage);
        fieldToImage.put(FieldType.PLAYER1, playerImage);
        fieldToImage.put(FieldType.TARGET, targetImage);
        fieldToImage.put(FieldType.WALL, wallImage);

        imageSize = groundImage.getWidth();
    }

    private void BuildMenu() {
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
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.showOpenDialog(null);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local multiplayer");
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
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        logic.onCommand(new Command(Command.CommandType.KEY_PRESSED, e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void onNewGameState(GameState g) {
        fields = g.staticFields;
        dynamicFieldAnimations = new ArrayList<>();

        for (DynamicField f : g.dynamicFields) {
            dynamicFieldAnimations.add(new DynamicFieldAnimation(f));
        }

        boolean inProgress = true;

        while (inProgress) {
            inProgress = false;
            for (DynamicFieldAnimation f : dynamicFieldAnimations) {
                if (f.dx != 0) {
                    inProgress = true;
                    f.dx = f.dx > 0 ? f.dx - 1 : f.dx + 1;
                } else if (f.dy != 0) {
                    inProgress = true;
                    f.dy = f.dy > 0 ? f.dy - 1 : f.dy + 1;
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

    class DrawPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //draw static
            for (int col = 0; col < fields.length; col++) {
                for (int row = 0; row < fields[col].length; row++) {
                    g.drawImage(fieldToImage.get(FieldType.GROUND), col * imageSize, row * imageSize,
                            null);
                    g.drawImage(fieldToImage.get(fields[col][row]), col * imageSize, row * imageSize,
                            null);
                }
            }

            //draw dynamic
            for (DynamicFieldAnimation f : dynamicFieldAnimations) {
                g.drawImage(fieldToImage.get(f.field.type),
                        (f.field.actual.getX() + f.field.delta.getX()) * imageSize - f.dx,
                        (f.field.actual.getY() + f.field.delta.getY()) * imageSize - f.dy,
                        null);
            }
        }
    }

    class DynamicFieldAnimation {
        DynamicField field;
        int dx, dy;

        DynamicFieldAnimation(DynamicField field) {
            this.field = field;
            dx = field.delta.getX() * imageSize;
            dy = field.delta.getY() * imageSize;
        }
    }
}
