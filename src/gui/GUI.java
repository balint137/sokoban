package gui;

import common.*;
import common.Command.Move;
import common.GameState.FieldType;
import logic.Logic;
import network.Client;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Graphical interface implementation
 * <p>
 * The GUI class communicates with Logic or Client, using the ICommand interface.
 * This class will listen for key presses, and notify the game logic.
 *
 * @author Balint Dobszay
 */
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
    private JLabel timeLabel;
    private JLabel settingsLabel;
    private Map<KeyboardSetting, Map<Integer, Move>> keyboardMaps;
    private KeyboardSetting player1KeyboardSetting;
    private KeyboardSetting player2KeyboardSetting;
    private boolean player1Enabled;
    private boolean player2Enabled;
    private String player1Name;
    private String player2Name;
    private long startTime;
    private String highscores;

    /**
     * Constructor of GUI. Creates and displays the game window.
     * Initializes game progress variables, calls the methods to build the game environment.
     *
     * @throws IOException if error when reading resource images
     */
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
        startTime = 0;

        player1Name = "";
        player2Name = "";
        player1Enabled = false;
        player2Enabled = false;
        highscores = "";

        player1KeyboardSetting = KeyboardSetting.WASD;
        player2KeyboardSetting = KeyboardSetting.ARROWS;
        buildKeyboardMaps();

        BuildMenu();
        ReadResourceImages();
        BuildDrawPanel();
        BuildStatusbar();

        updateStatusBar();
        updateTime();

        setVisible(true);
    }

    /**
     * Entry point of the application.
     * Instantiates the GUI class, and starts timer for refreshing the window for animations.
     *
     * @param args not used
     */
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

    /**
     * Creates elements for the status bar at the bottom of the window.
     */
    private void BuildStatusbar() {
        statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.PAGE_END);
        statusPanel.setSize(new Dimension(getWidth(), 30));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        settingsLabel = new JLabel();
        settingsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(settingsLabel);
    }

    /**
     * Creates the main draw area of the game.
     */
    private void BuildDrawPanel() {
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        drawPanel.setSize(640, 640);
        drawPanel.setVisible(false);
    }

    /**
     * Reads the picture files that are the elements of a game map.
     * Creates key-value pairs between field type and image name.
     *
     * @throws IOException if error when reading resource images
     */
    private void ReadResourceImages() throws IOException {
        final BufferedImage crateImage = ImageIO.read(getClass().getResourceAsStream("/crate.png"));
        final BufferedImage groundImage = ImageIO.read(getClass().getResourceAsStream("/ground.png"));
        final BufferedImage playerImage = ImageIO.read(getClass().getResourceAsStream("/player.png"));
        final BufferedImage player2Image = ImageIO.read(getClass().getResourceAsStream("/player2.png"));
        final BufferedImage targetImage = ImageIO.read(getClass().getResourceAsStream("/target.png"));
        final BufferedImage wallImage = ImageIO.read(getClass().getResourceAsStream("/wall.png"));

        fieldToImage.put(FieldType.CRATE, crateImage);
        fieldToImage.put(FieldType.GROUND, groundImage);
        fieldToImage.put(FieldType.PLAYER1, playerImage);
        fieldToImage.put(FieldType.PLAYER2, player2Image);
        fieldToImage.put(FieldType.TARGET, targetImage);
        fieldToImage.put(FieldType.WALL, wallImage);

        imageSize = groundImage.getWidth();
    }

    /**
     * Builds the structure of the menu bar at the top of the game window.
     * The Logic or Client classes are instantiated here, if a new game is started.
     */
    private void BuildMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Start");

        JMenuItem menuItem = new JMenuItem("Client");
        menuItem.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog("Please provide the server IP address");
            logic = new Client(this, ip);
            connectionStatus = "Game in progress";
            updateStatusBar();
            player1Enabled = false;
            player2Enabled = true;
            drawPanel.setVisible(true);
            gameInProgress = true;
            startTime = System.currentTimeMillis();
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Server");
        menuItem.addActionListener(e -> {
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_multi")) {
                connectionStatus = "Waiting for client";
                updateStatusBar();
                if (player1Name.isEmpty() || player2Name.isEmpty()) {
                    player1Name = JOptionPane.showInputDialog("Player 1 name:");
                    player2Name = JOptionPane.showInputDialog("Player 2 name:");
                }
                logic = new Logic(this, path, true, player1Name, player2Name, startTime);
                connectionStatus = "Game in progress";
                updateStatusBar();
                player1Enabled = true;
                player2Enabled = false;
                drawPanel.setVisible(true);
                gameInProgress = true;
                startTime = System.currentTimeMillis();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local");
        menuItem.addActionListener(e -> {
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_single")) {
                if (player1Name.isEmpty()) {
                    player1Name = JOptionPane.showInputDialog("Player 1 name:");
                }
                logic = new Logic(this, path, false, player1Name, player2Name, startTime);
                connectionStatus = "Game in progress";
                updateStatusBar();
                player1Enabled = true;
                player2Enabled = false;
                drawPanel.setVisible(true);
                gameInProgress = true;
                startTime = System.currentTimeMillis();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Local multiplayer");
        menuItem.addActionListener(e -> {
            String path = openMapDialog();
            if (!path.isEmpty() && path.contains("_multi")) {
                if (player1Name.isEmpty() || player2Name.isEmpty()) {
                    player1Name = JOptionPane.showInputDialog("Player 1 name:");
                    player2Name = JOptionPane.showInputDialog("Player 2 name:");
                }
                logic = new Logic(this, path, false, player1Name, player2Name, startTime);
                connectionStatus = "Game in progress";
                updateStatusBar();
                player1Enabled = true;
                player2Enabled = true;
                drawPanel.setVisible(true);
                gameInProgress = true;
                startTime = System.currentTimeMillis();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid map", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu("Options");
        menuItem = new JMenuItem("Player 1 keyboard settings");
        menuItem.addActionListener(e -> {
            Object selected = JOptionPane.showInputDialog(this, "Player 1 (local)",
                    "Player 1 keyboard settings", JOptionPane.PLAIN_MESSAGE, null, KeyboardSetting.values(), player1KeyboardSetting);
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

        menuItem = new JMenuItem("Player 2 keyboard settings");
        menuItem.addActionListener(e -> {
            Object selected = JOptionPane.showInputDialog(this, "Player 2 (local multiplayer)",
                    "Player 2 keyboard settings", JOptionPane.PLAIN_MESSAGE, null, KeyboardSetting.values(), player2KeyboardSetting);
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

        menuItem = new JMenuItem("Highscores");
        menuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, highscores));
        menu.add(menuItem);

        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    /**
     * Opens a file chooser window, used for selecting a game map.
     *
     * @return Path of selected file if OK is pressed, empty string if cancel is pressed.
     */
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

    /**
     * Updates the game status, number of moves, and keyboard settings info in the status bar.
     */
    private void updateStatusBar() {
        statusLabel.setText("Status: " + connectionStatus + ", moves: " + numberOfMoves);
        settingsLabel.setText("Player 1: " + player1KeyboardSetting.toString() + ", Player 2: " + player2KeyboardSetting.toString());
        repaint();
    }

    /**
     * Updates the elapsed time info in the status bar.
     */
    private void updateTime() {
        long elapsed = System.currentTimeMillis() - startTime;
        long second = (elapsed / 1000) % 60;
        long minute = (elapsed / (1000 * 60)) % 60;
        String time;

        if (startTime == 0) {
            time = "--:--";
        } else {
            time = String.format("%02d:%02d", minute, second);
        }

        timeLabel.setText("Elapsed time: " + time);
    }

    /**
     * Creates key-value pair relation between pressed key and caused player movement.
     */
    private void buildKeyboardMaps() {
        Map<Integer, Move> keymapWASD = new HashMap<>();
        keymapWASD.put(KeyEvent.VK_W, Move.UP);
        keymapWASD.put(KeyEvent.VK_A, Move.LEFT);
        keymapWASD.put(KeyEvent.VK_S, Move.DOWN);
        keymapWASD.put(KeyEvent.VK_D, Move.RIGHT);

        Map<Integer, Move> keymapIJKL = new HashMap<>();
        keymapIJKL.put(KeyEvent.VK_I, Move.UP);
        keymapIJKL.put(KeyEvent.VK_J, Move.LEFT);
        keymapIJKL.put(KeyEvent.VK_K, Move.DOWN);
        keymapIJKL.put(KeyEvent.VK_L, Move.RIGHT);

        Map<Integer, Move> keymapARROWS = new HashMap<>();
        keymapARROWS.put(KeyEvent.VK_UP, Move.UP);
        keymapARROWS.put(KeyEvent.VK_LEFT, Move.LEFT);
        keymapARROWS.put(KeyEvent.VK_DOWN, Move.DOWN);
        keymapARROWS.put(KeyEvent.VK_RIGHT, Move.RIGHT);

        keyboardMaps = new EnumMap<>(KeyboardSetting.class);
        keyboardMaps.put(KeyboardSetting.WASD, keymapWASD);
        keyboardMaps.put(KeyboardSetting.IJKL, keymapIJKL);
        keyboardMaps.put(KeyboardSetting.ARROWS, keymapARROWS);
    }

    /**
     * Not used, needed because of the KeyListener interface.
     * @param e pressed key event
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * If a keypress is catched, this method will be called.
     * It selects the necessary player movement based on the pressed key, and sends it to the game logic.
     * Displays error message if there is no game in progress.
     * @param e pressed key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameInProgress) {
            logic.onCommand(new Command(Command.CommandType.KEY_PRESSED,
                    player1Enabled ? keyboardMaps.get(player1KeyboardSetting).get(e.getKeyCode()) : null,
                    player2Enabled ? keyboardMaps.get(player2KeyboardSetting).get(e.getKeyCode()) : null));
        } else {
            JOptionPane.showMessageDialog(this, "Please start a new game!",
                    "End of game", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Not used, needed because of the KeyListener interface.
     * @param e pressed key event
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * This is the communication point with the game logic.
     * If there is a happening in the game, the logic will notify the GUI using this method.
     * The GUI will draw the new fields or update the parameters based on the received new gamestate.
     * @param g new gamestate
     */
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
                        connectionStatus = "Game finished";
                        updateStatusBar();
                        JOptionPane.showMessageDialog(this, "Congratulations, you won!",
                                "Victory", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case LOSE:
                        gameInProgress = false;
                        connectionStatus = "Game finished";
                        updateStatusBar();
                        JOptionPane.showMessageDialog(this, "You lost!",
                                "Game over", JOptionPane.WARNING_MESSAGE);
                        break;
                }
                break;
            case HIGHSCORES:
                highscores = g.highscores;
                break;
        }
    }

    /**
     * This method is called by the timer in the main method, every 10 msec.
     * If there is an animation in progress, it will calculate the necessary movement of the fields and repaint.
     * When the movement animation is done, it will notify the game logic using the ICommand interface.
     * This method also updates the elapsed time label in the status bar.
     */
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

            //when running last time
            if (!animationInProgress) {
                logic.onCommand(new Command(Command.CommandType.ANIMATION_DONE));
            }
        }

        if (gameInProgress) {
            updateTime();
        }
    }

    /**
     * The possible combinations of player control keys.
     */
    public enum KeyboardSetting {WASD, IJKL, ARROWS}

    /**
     * The main graphics area of the game, it will run when repaint is called.
     * Draws the static fields at their fixed place,
     * draws the dynamic fields using the delta values caused by a running animation.
     */
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

    /**
     * This class is similar to the DynamicField.
     * It is needed to convert the actual and delta coordinates stored in
     * the DynamicField to pixels, because it is needed for the animations.
     */
    class DynamicFieldAnimation implements Serializable {
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
