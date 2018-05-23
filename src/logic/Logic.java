
package logic;

import common.*;
import common.GameState.FieldType;
import gui.GUI;

import java.io.*;
import java.util.*;

import java.nio.charset.Charset;

import static gui.GUI.MAX_MAP_SIZE;

public class Logic implements ICommand {
    private IGameState g;
    private boolean animationInProgress;

    private FieldType[][] mapStatic;
    private ArrayList<DynamicField> crates;
    private ArrayList<DynamicField> players;
    private ArrayList<DynamicField> mapDynamic;

    private ArrayList<Command> newCommands;
    private ArrayList<Command> commandsToExecute;
    
    private GUI.KeyboardSetting p1Keyboard;
    private GUI.KeyboardSetting p2Keyboard;
    
    private int numberOfSteps;

    public Logic(GUI gui, String mapFilePath, boolean network, GUI.KeyboardSetting player1, GUI.KeyboardSetting player2){

        newCommands = new ArrayList<>();
        commandsToExecute = new ArrayList<>();

        animationInProgress = false;

        g = gui;

        mapStatic = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        crates = new ArrayList<>();
        players = new ArrayList<>();
        mapDynamic = new ArrayList<>();

        p1Keyboard = player1;
        p2Keyboard = player2;
        
        numberOfSteps = 0;

        try {
			loadMap(mapFilePath);
			g.onNewGameState(new GameState(GameState.GameStateType.STATIC_FIELDS, mapStatic));
			g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
			mapDynamic.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void onCommand(Command c) {
    	newCommands.add(c);
    	executeCommands();
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

    private void executeCommands() {
    	commandsToExecute.addAll(newCommands);
    	newCommands.clear();
    	for(Command c : commandsToExecute) {
    		switch (c.command) {
    		case KEY_PRESSED:
    	    	if(!animationInProgress) {
	    			processKeyPress(c);
    	    	}
    			break;
    		case ANIMATION_DONE:
    			if (animationInProgress) {
        			mapDynamic.clear();
        		 	animationInProgress = false;
        	    	resolveDeltas();
        		 	if(checkForVictory()) {
        		 		g.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.WIN));
        		 	}
        		 	if(checkForLoss()) {
        		 		g.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.LOSE));
        		 	}
    			}
    			break;
    		}
    	}
    	commandsToExecute.clear();
    }

    private void processKeyPress(Command c) {
    	switch (c.lastKeyPressed.getKeyCode()) {
		case 87: //W
			if (p1Keyboard == GUI.KeyboardSetting.WASD) {
				move(0, new Coordinate(0,-1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.WASD&&players.size() == 2) {
				move(1, new Coordinate(0,-1));
			}
			break;
		case 65: //A
			if (p1Keyboard == GUI.KeyboardSetting.WASD) {
				move(0, new Coordinate(-1, 0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.WASD&&players.size() == 2) {
				move(1, new Coordinate(-1, 0));
			}
			break;
		case 83: //S
			if (p1Keyboard == GUI.KeyboardSetting.WASD) {
				move(0, new Coordinate(0,1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.WASD&&players.size() == 2) {
				move(1, new Coordinate(0,1));
			}
			break;
		case 68: //D
			if (p1Keyboard == GUI.KeyboardSetting.WASD) {
				move(0, new Coordinate(1,0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.WASD&&players.size() == 2) {
				move(1, new Coordinate(1,0));
			}
			break;
		case 73: //I
			if (p1Keyboard == GUI.KeyboardSetting.IJKL) {
				move(0, new Coordinate(0,-1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.IJKL&&players.size() == 2) {
				move(1, new Coordinate(0,-1));
			}
			break;
		case 74: //J
			if (p1Keyboard == GUI.KeyboardSetting.IJKL) {
				move(0, new Coordinate(-1, 0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.IJKL&&players.size() == 2) {
				move(1, new Coordinate(-1, 0));
			}
			break;
		case 75: //K
			if (p1Keyboard == GUI.KeyboardSetting.IJKL) {
				move(0, new Coordinate(0,1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.IJKL&&players.size() == 2) {
				move(1, new Coordinate(0,1));
			}
			break;
		case 76: //L
			if (p1Keyboard == GUI.KeyboardSetting.IJKL) {
				move(0, new Coordinate(1,0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.IJKL&&players.size() == 2) {
				move(1, new Coordinate(1,0));
			}
			break;
		case 38: //UP
			if (p1Keyboard == GUI.KeyboardSetting.ARROWS) {
				move(0, new Coordinate(0,-1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.ARROWS&&players.size() == 2) {
				move(1, new Coordinate(0,-1));
			}
			break;
		case 37: //LEFT
			if (p1Keyboard == GUI.KeyboardSetting.ARROWS) {
				move(0, new Coordinate(-1, 0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.ARROWS&&players.size() == 2) {
				move(1, new Coordinate(-1, 0));
			}
			break;
		case 40: //DOWN
			if (p1Keyboard == GUI.KeyboardSetting.ARROWS) {
				move(0, new Coordinate(0,1));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.ARROWS&&players.size() == 2) {
				move(1, new Coordinate(0,1));
			}
			break;
		case 39: //RIGHT
			if (p1Keyboard == GUI.KeyboardSetting.ARROWS) {
				move(0, new Coordinate(1,0));
			}
			else if (p2Keyboard == GUI.KeyboardSetting.ARROWS&&players.size() == 2) {
				move(1, new Coordinate(1,0));
			}
			break;
		default:
			break;
    	}
    }

    private void move(int playerIndex, Coordinate dir) {
    	switch(blockType(players.get(playerIndex).actual, dir)) {
    	case GROUND:
    		players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
    		numberOfSteps++;
    		break;
    	case TARGET:
    		players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
    		numberOfSteps++;
    		break;
    	case CRATE:
    		moveCrate(playerIndex, dir);
    		break;
    	case PLAYER1:
    		break;
    	case PLAYER2:
    		break;
    	case WALL:
    		break;
    	}
    	mapDynamic.addAll(players);
    	mapDynamic.addAll(crates);
		animationInProgress = true;
		g.onNewGameState(new GameState(GameState.GameStateType.MOVEMENTS, numberOfSteps));
		g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
    }

    private void moveCrate(int playerIndex, Coordinate dir) {
		int crateIndex;
		switch(blockType(players.get(playerIndex).actual, new Coordinate(dir.getX()*2, dir.getY()*2))) {
		case GROUND:
			players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
			crateIndex = findCrateIndex(players.get(playerIndex).actual, dir);
			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, dir));
			numberOfSteps++;
			return;
		case TARGET:
			players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
			crateIndex = findCrateIndex(players.get(playerIndex).actual, dir);
			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, dir));
			numberOfSteps++;
			return;
		case CRATE:
    		return;
		case PLAYER1:
    		return;
    	case PLAYER2:
    		return;
    	case WALL:
    		return;
		}
    }

    private FieldType blockType (Coordinate c, Coordinate difference) {
    	for (DynamicField mapElement : crates) {
    		if (mapElement.actual.getX() == (c.getX() + difference.getX()) && mapElement.actual.getY() == (c.getY() + difference.getY())) {
    			return mapElement.type;
    		}
    	}
    	for (DynamicField mapElement : players) {
    		if (mapElement.actual.getX() == (c.getX() + difference.getX()) && mapElement.actual.getY() == (c.getY() + difference.getY())) {
    			return mapElement.type;
    		}
    	}
    	return mapStatic[c.getX()+difference.getX()][c.getY()+difference.getY()];
    }

    private int findCrateIndex (Coordinate c, Coordinate difference) {
    	int i = 0;
    	for (DynamicField crate : crates) {
    		if(crate.actual.getX() == (c.getX() + difference.getX()) && crate.actual.getY() == (c.getY() + difference.getY())) {
    			return i;
    		}
    		i++;
    	}
    	return -1;
    }

    private void resolveDeltas() {
    	for (DynamicField df : players) {
    		df.actual.add(df.delta);
    		df.delta = new Coordinate (0,0);
    	}
    	for (DynamicField df : crates) {
    		df.actual.add(df.delta);
    		df.delta = new Coordinate (0,0);
    	}
    }

    private boolean checkForVictory() {
    	for (DynamicField crate : crates) {
    		if(mapStatic[crate.actual.getX()][crate.actual.getY()] != FieldType.TARGET) {
    			return false;
    		}
    	}
    	return true;
    }

    private boolean checkForLoss() {
    	for (DynamicField crate : crates) {
    		FieldType above, below, right, left;
    		boolean aboveFree, belowFree, rightFree, leftFree;
    		above = blockType (crate.actual, new Coordinate(0, -1));
    		below = blockType (crate.actual, new Coordinate(0, 1));
    		right = blockType (crate.actual, new Coordinate(1, 0));
    		left = blockType (crate.actual, new Coordinate(-1, 0));
    		aboveFree = (above != FieldType.WALL && above != FieldType.CRATE);
    		belowFree = (below != FieldType.WALL && below != FieldType.CRATE);
    		rightFree = (right != FieldType.WALL && right != FieldType.CRATE);
    		leftFree = (left != FieldType.WALL && left != FieldType.CRATE);

    		boolean stuck = !((aboveFree&&belowFree)||(rightFree&&leftFree))&&(mapStatic[crate.actual.getX()][crate.actual.getY()] != FieldType.TARGET);
    		if (stuck) {
    			return true;
    		}
    	}
    	return false;
    }

}
