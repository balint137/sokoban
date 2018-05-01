package gui;

import common.*;
import common.GameState.FieldType;
import logic.Logic;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static javax.swing.SwingUtilities.isEventDispatchThread;

public class GUI extends JFrame implements IGameState, KeyListener {
    public static final int MAX_MAP_SIZE = 10;

    private int imageSize;
    private Map<FieldType, BufferedImage> fieldToImage = new EnumMap<>(FieldType.class);

    private FieldType[][] fields;
    private ArrayList<DynamicFieldAnimation> dynamicFieldAnimations;

    private ICommand logic;

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Logic l = new Logic();
                try {
                    GUI g = new GUI(l);
                    l.setGui(g);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GUI(ICommand logic) throws IOException {
        super("Sokoban");
        setSize(850, 750);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setFocusable(true);
        addKeyListener(this);

        this.logic = logic;

        fields = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        dynamicFieldAnimations = new ArrayList<>();

        BuildMenu();
        ReadResourceImages();

        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 40));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel("Status bar");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        setVisible(true);
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
        menuItem.addActionListener(e -> {
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Server");
        menuItem.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local");
        menuItem.addActionListener(e -> {
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local multiplayer");
        menuItem.addActionListener(e -> {
        });
        menu.add(menuItem);

        menuBar.add(menu);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(e -> System.exit(0));
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
        switch (g.type) {
            case STATIC_FIELDS:
                fields = g.staticFields;
                break;
            case DYNAMIC_FIELDS:
                dynamicFieldAnimations.clear();
                for (DynamicField f : g.dynamicFields) {
                    dynamicFieldAnimations.add(new DynamicFieldAnimation(f));
                }

                boolean inProgress = true;
                while (inProgress) {
                    inProgress = false;
                    for (DynamicFieldAnimation f : dynamicFieldAnimations) {
                        if (f.dx != 0) {
                            inProgress = true;
                            f.dx = f.dx > 0 ? f.dx - 4 : f.dx + 4;
                        } else if (f.dy != 0) {
                            inProgress = true;
                            f.dy = f.dy > 0 ? f.dy - 4 : f.dy + 4;
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.repaint();
                    }
                }
                break;
            case TIME:
            case MOVEMENTS:
                break;
        }
        Thread.currentThread().interrupt();
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
                g.drawImage(fieldToImage.get(f.type), f.x - f.dx, f.y - f.dy, null);
            }
        }
    }

    class DynamicFieldAnimation {
        GameState.FieldType type;
        int x, y;
        int dx, dy;

        DynamicFieldAnimation(DynamicField field) {
            type = field.type;
            x = (field.actual.getX() + field.delta.getX()) * imageSize;
            y = (field.actual.getY() + field.delta.getY()) * imageSize;
            dx = field.delta.getX() * imageSize;
            dy = field.delta.getY() * imageSize;
        }
    }
}
