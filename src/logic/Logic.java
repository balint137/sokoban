package logic;

import common.*;
import common.GameState.FieldType;
import gui.GUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static gui.GUI.MAX_MAP_SIZE;

public class Logic implements ICommand {
    private IGameState g;
    private boolean animationInProgress;

    private Coordinate player;

    private ArrayList<DynamicField> dynamicFields;

    public Logic(IGameState g) {
        this.g = g;

        player = new Coordinate(3, 4);
        animationInProgress = false;
        dynamicFields = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        GUI g = new GUI();
        Logic l = new Logic(g);
        g.setLogic(l);

        //---------------------------------------------------------------------
        //teszt palya
        //ez a kod csak teszelesre valo

        //statikus mezok letrehozasa
        FieldType[][] testStatic = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];

        for (FieldType[] col : testStatic) {
            Arrays.fill(col, FieldType.GROUND);
        }

        testStatic[1][0] = FieldType.WALL;
        testStatic[2][0] = FieldType.WALL;
        testStatic[3][0] = FieldType.WALL;
        testStatic[4][0] = FieldType.WALL;
        testStatic[5][0] = FieldType.WALL;
        testStatic[6][0] = FieldType.WALL;
        testStatic[7][0] = FieldType.WALL;
        testStatic[8][0] = FieldType.WALL;

        testStatic[1][9] = FieldType.WALL;
        testStatic[2][9] = FieldType.WALL;
        testStatic[3][9] = FieldType.WALL;
        testStatic[4][9] = FieldType.WALL;
        testStatic[5][9] = FieldType.WALL;
        testStatic[6][9] = FieldType.WALL;
        testStatic[7][9] = FieldType.WALL;

        testStatic[1][1] = FieldType.WALL;
        testStatic[1][2] = FieldType.WALL;
        testStatic[1][3] = FieldType.WALL;
        testStatic[1][4] = FieldType.WALL;
        testStatic[1][5] = FieldType.WALL;
        testStatic[1][6] = FieldType.WALL;
        testStatic[1][7] = FieldType.WALL;
        testStatic[1][8] = FieldType.WALL;

        testStatic[8][1] = FieldType.WALL;
        testStatic[8][2] = FieldType.WALL;
        testStatic[8][3] = FieldType.WALL;
        testStatic[8][4] = FieldType.WALL;
        testStatic[8][5] = FieldType.WALL;
        testStatic[7][5] = FieldType.WALL;
        testStatic[7][6] = FieldType.WALL;
        testStatic[7][7] = FieldType.WALL;
        testStatic[7][8] = FieldType.WALL;

        testStatic[4][3] = FieldType.WALL;
        testStatic[4][4] = FieldType.WALL;
        testStatic[4][5] = FieldType.WALL;


        testStatic[2][3] = FieldType.TARGET;
        testStatic[3][4] = FieldType.TARGET;
        testStatic[3][5] = FieldType.TARGET;

        g.onNewGameState(new GameState(GameState.GameStateType.STATIC_FIELDS, testStatic, null, 0, 0));

        //dinamikus mezok letrehozasa
        ArrayList<DynamicField> testDynamic = new ArrayList<>();

        testDynamic.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 5)));
        testDynamic.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 6)));
        testDynamic.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 7)));
        testDynamic.add(new DynamicField(FieldType.PLAYER1, new Coordinate(3, 4)));

        g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, null, testDynamic, 0, 0));
        //---------------------------------------------------------------------
    }

    @Override
    public void onCommand(Command c) {
        switch (c.command) {
            case NEW_GAME:
                break;
            case OPEN_MAP_FILE:
                break;
            case KEY_PRESSED:
                if (!animationInProgress) {
                    Coordinate delta;
                    switch (c.lastKeyPressed.getKeyChar()) {
                        case 'w':
                            delta = new Coordinate(0, -1);
                            break;
                        case 'a':
                            delta = new Coordinate(-1, 0);
                            break;
                        case 's':
                            delta = new Coordinate(0, 1);
                            break;
                        case 'd':
                            delta = new Coordinate(1, 0);
                            break;
                        default:
                            delta = new Coordinate(0, 0);
                            break;
                    }

                    dynamicFields.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 5)));
                    dynamicFields.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 6)));
                    dynamicFields.add(new DynamicField(FieldType.CRATE, new Coordinate(5, 7)));
                    dynamicFields.add(new DynamicField(FieldType.PLAYER1, player, delta));

                    new Thread(() -> {
                        try {
                            animationInProgress = true;
                            g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, null, dynamicFields, 0, 0));
                        } finally {
                            dynamicFields.clear();
                            player.add(delta);
                            animationInProgress = false;
                        }
                    }).start();
                }
                break;
        }
    }
}
