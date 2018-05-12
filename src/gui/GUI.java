package gui;

import common.*;
import common.GameState.FieldType;
import logic.Logic;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
    private boolean animationInProgress;
    private boolean gameInProgress;
    private int imageSize;
    private Map<FieldType, BufferedImage> fieldToImage;
    private FieldType[][] fields;
    private ArrayList<DynamicFieldAnimation> dynamicFieldAnimations;
    private ICommand logic;
    private String connectionStatus;
    private Integer numberOfMoves;
    private DrawPanel drawPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel settingsLabel;
    private KeyboardSetting player1KeyboardSetting;
    private KeyboardSetting player2KeyboardSetting;

    public GUI() throws IOException {
        super("Sokoban");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 640);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);

        fieldToImage = new EnumMap<>(FieldType.class);
        fields = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        dynamicFieldAnimations = new ArrayList<>();
        animationInProgress = false;
        gameInProgress = false;

        connectionStatus = "-";
        numberOfMoves = 0;

        player1KeyboardSetting = KeyboardSetting.WASD;
        player2KeyboardSetting = KeyboardSetting.ARROWS;

        BuildMenu();
        ReadResourceImages();

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        drawPanel.setSize(640, 640);
        drawPanel.setVisible(false);

        statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.PAGE_END);
        statusPanel.setSize(new Dimension(getWidth(), 30));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        settingsLabel = new JLabel();
        settingsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(settingsLabel);

        updateStatusBar();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                final GUI g = new GUI();
                Timer timer = new Timer(10, e -> g.refreshAnimations());
                timer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void ReadResourceImages() throws IOException {
        final BufferedImage crateImage = ImageIO.read(new File("resources/crate.png"));
        final BufferedImage groundImage = ImageIO.read(new File("resources/ground.png"));
        final BufferedImage playerImage = ImageIO.read(new File("resources/player.png"));
        final BufferedImage player2Image = ImageIO.read(new File("resources/player2.png"));
        final BufferedImage targetImage = ImageIO.read(new File("resources/target.png"));
        final BufferedImage wallImage = ImageIO.read(new File("resources/wall.png"));

        fieldToImage.put(FieldType.CRATE, crateImage);
        fieldToImage.put(FieldType.GROUND, groundImage);
        fieldToImage.put(FieldType.PLAYER1, playerImage);
        fieldToImage.put(FieldType.PLAYER2, player2Image);
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
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_multi")) {
                this.logic = new Logic(this, path, true);
                drawPanel.setVisible(true);
                gameInProgress = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local");
        menuItem.addActionListener(e -> {
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_single")) {
                this.logic = new Logic(this, path, false);
                drawPanel.setVisible(true);
                gameInProgress = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local multiplayer");
        menuItem.addActionListener(e -> {
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_multi")) {
                this.logic = new Logic(this, path, false);
                drawPanel.setVisible(true);
                gameInProgress = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu("Keyboard settings");
        menuItem = new JMenuItem("Player 1");
        menuItem.addActionListener(e -> {
            Object selected = JOptionPane.showInputDialog(this, "Player 1 (local)",
                    "Player 1 keyboard settings", JOptionPane.DEFAULT_OPTION, null, KeyboardSetting.values(), player1KeyboardSetting);
            if (selected != null) {
                KeyboardSetting s = KeyboardSetting.valueOf(selected.toString());
                if (s != player2KeyboardSetting) {
                    player1KeyboardSetting = s;
                    updateStatusBar();
                } else {
                    JOptionPane.showMessageDialog(this, "Already selected for Player 2", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Player 2");
        menuItem.addActionListener(e -> {
            Object selected = JOptionPane.showInputDialog(this, "Player 2 (local multiplayer)",
                    "Player 2 keyboard settings", JOptionPane.DEFAULT_OPTION, null, KeyboardSetting.values(), player2KeyboardSetting);
            if (selected != null) {
                KeyboardSetting s = KeyboardSetting.valueOf(selected.toString());
                if (s != player1KeyboardSetting) {
                    player2KeyboardSetting = s;
                    updateStatusBar();
                } else {
                    JOptionPane.showMessageDialog(this, "Already selected for Player 1", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(e -> System.exit(0));
        menuBar.add(menuItem);

        setJMenuBar(menuBar);
    }

    private String openMapDialog() {
        final JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));

        fileChooser.setDialogTitle("Select a map");

        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Sokoban map txt", "txt");
        fileChooser.addChoosableFileFilter(filter);

        int ret = fileChooser.showOpenDialog(this);

        if (ret == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return "";
        }
    }

    private void updateStatusBar() {
        statusLabel.setText("Status: " + connectionStatus + ", moves: " + numberOfMoves + ", time:");
        settingsLabel.setText("Player 1: " + player1KeyboardSetting.toString() + ", Player 2: " + player2KeyboardSetting.toString());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameInProgress) {
            logic.onCommand(new Command(Command.CommandType.KEY_PRESSED, e));
        } else {
            JOptionPane.showMessageDialog(this, "Please start a new game!",
                    "End of game", JOptionPane.WARNING_MESSAGE);
        }
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
                animationInProgress = true;
                break;
            case MOVEMENTS:
                numberOfMoves = g.numberOfMovements;
                updateStatusBar();
                break;
            case PHASE_UPDATE:
                switch (g.phase) {
                    case WIN:
                        gameInProgress = false;
                        JOptionPane.showMessageDialog(this, "Congratulations, you won!",
                                "Victory", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case LOSE:
                        gameInProgress = false;
                        JOptionPane.showMessageDialog(this, "You lost!",
                                "Game over", JOptionPane.WARNING_MESSAGE);
                        break;
                }
                break;
        }
    }

    private void refreshAnimations() {
        if (animationInProgress) {
            animationInProgress = false;
            for (DynamicFieldAnimation f : dynamicFieldAnimations) {
                if (f.dx != 0) {
                    animationInProgress = true;
                    f.dx = f.dx > 0 ? f.dx - 8 : f.dx + 8;
                } else if (f.dy != 0) {
                    animationInProgress = true;
                    f.dy = f.dy > 0 ? f.dy - 8 : f.dy + 8;
                }
            }
            repaint();

            //amikor utoljara fut le
            if (!animationInProgress) {
                logic.onCommand(new Command(Command.CommandType.ANIMATION_DONE));
            }
        }
    }

    public enum KeyboardSetting {WASD, IJKL, ARROWS}

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
