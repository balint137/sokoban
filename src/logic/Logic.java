package logic;

import common.*;
import common.GameState.FieldType;

import java.io.*;
import java.util.*;

import java.nio.charset.Charset;

import static gui.GUI.MAX_MAP_SIZE;

public class Logic implements ICommand {
    private IGameState g;
    private boolean animationInProgress;

    private Coordinate player;

    private FieldType[][] mapStatic;
    private ArrayList<DynamicField> crates;
    private ArrayList<DynamicField> players;
    private ArrayList<DynamicField> mapDynamic;

    private ArrayList<Command> newCommands;
    private ArrayList<Command> commandsToExecute;

    public Logic() {
        newCommands = new ArrayList<>();
        animationInProgress = false;
        mapStatic = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        crates = new ArrayList<>();
        players = new ArrayList<>();
        mapDynamic = new ArrayList<>();
    }

    public void setGui(IGameState g) {
        this.g = g;
    }

    public static void main(String[] args) throws IOException {
        l.loadMap("resources/map.txt");
        g.onNewGameState(new GameState(GameState.GameStateType.STATIC_FIELDS, l.mapStatic, null, 0, 0));

        g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, null, l.mapDynamic, 0, 0));
        l.mapDynamic.clear();
        //---------------------------------------------------------------------
    }

    @Override
    public void onCommand(Command c) {
    	newCommands.add(c);
    	System.out.println("pfff");
    }

    private void loadMap(String filename) throws IOException {
    	for (FieldType[] col : this.mapStatic) {
            Arrays.fill(col, FieldType.GROUND);
        }

        File map = new File(filename);
        Charset encoding = Charset.defaultCharset();
        try (InputStream in = new FileInputStream(map);
        		Reader reader = new InputStreamReader(in, encoding)) {
        	int r, i = 0;
        	while((r = reader.read()) != -1) {
        		switch ((char)r) {
        		case '-':
        			mapStatic[i%10][i/10] = FieldType.GROUND;
        			break;
        		case '#':
        			mapStatic[i%10][i/10] = FieldType.WALL;
        			break;
        		case 'X':
        			mapStatic[i%10][i/10] = FieldType.TARGET;
        			break;
        		case '0':
        			crates.add(new DynamicField(FieldType.CRATE, new Coordinate(i%10, i/10)));
        			break;
        		case '1':
        			players.add(new DynamicField(FieldType.PLAYER1, new Coordinate(i%10, i/10)));
        			break;
        		case '2':
        			players.add(new DynamicField(FieldType.PLAYER2, new Coordinate(i%10, i/10)));
        			break;
        		case '\n':
        			i--;
        			break;
    			default:
    				mapStatic[i/10][i%10] = FieldType.GROUND;
        			break;
        		}
        		i++;
        	}
        	mapDynamic.addAll(players);
        	mapDynamic.addAll(crates);
        }
    }
}
