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
    
    private Timer timer;
    private TimerTask wakeUp;
    private long delay;

    public Logic() {
        
        newCommands = new ArrayList<>();
        commandsToExecute = new ArrayList<>();
        
        animationInProgress = false;
        
        mapStatic = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        crates = new ArrayList<>();
        players = new ArrayList<>();
        mapDynamic = new ArrayList<>();
    }
    
    public void setGui(IGameState g) {
        this.g = g;
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

        	//System.out.println(c.lastKeyPressed.getKeyChar());
    		switch (c.command) {
    		case NEW_GAME:
    			break;
    		case OPEN_MAP_FILE:
    			break;
    		case KEY_PRESSED:
    			switch (c.lastKeyPressed.getKeyChar()) {
    			case 'w':
    				moveUp(0);
    				break;
    			case 'a':
    				moveLeft(0);
    				break;
    			case 's':
    				moveDown(0);
    				break;
    			case 'd':
    				moveRight(0);
    				break;
    			}
    		}
    	}

    	commandsToExecute.clear();
    	mapDynamic.clear();
    	mapDynamic.addAll(players);
    	mapDynamic.addAll(crates);
    	new Thread(() -> {
    		try {
    			 animationInProgress = true;
    			 g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, null, mapDynamic, 0, 0));
    		 } finally {
    		 	mapDynamic.clear();
    		 	animationInProgress = false;
    		 }
    		 }).start();
    	resolveDeltas();
    }
    private void moveUp(int playerIndex) {
    	switch(blockType(players.get(playerIndex).actual, new Coordinate(0, -1))) {
    	case WALL:
    		return;
    	case GROUND:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, -1)));
    		return;
    	case TARGET:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, -1)));
    		return;
    	case CRATE:
    		int crateIndex;
    		switch(blockType(players.get(playerIndex).actual, new Coordinate(0, -2))) {
    		case GROUND:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, -1)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(0, -1));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (0, -1)));
    			return;
    		case TARGET:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, -1)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(0, -1));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (0, -1)));
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
    		return;
    	case PLAYER1:
    		return;
    	case PLAYER2:
    		return;
    	}
    }

    private void moveDown(int playerIndex) {
    	switch(blockType(players.get(playerIndex).actual, new Coordinate(0, 1))) {
    	case WALL:
    		return;
    	case GROUND:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, 1)));
    		return;
    	case TARGET:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, 1)));
    		return;
    	case CRATE:
    		int crateIndex;
    		switch(blockType(players.get(playerIndex).actual, new Coordinate(0, 2))) {
    		case GROUND:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, 1)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(0, 1));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (0, 1)));
    			return;
    		case TARGET:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (0, 1)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(0, 1));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (0, 1)));
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
    		return;
    	case PLAYER1:
    		return;
    	case PLAYER2:
    		return;
    	}
    }
    
    private void moveLeft(int playerIndex) {
    	switch(blockType(players.get(playerIndex).actual, new Coordinate(-1, 0))) {
    	case WALL:
    		return;
    	case GROUND:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (-1, 0)));
    		return;
    	case TARGET:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (-1, 0)));
    		return;
    	case CRATE:
    		int crateIndex;
    		switch(blockType(players.get(playerIndex).actual, new Coordinate(-2, 0))) {
    		case GROUND:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (-1, 0)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(-1, 0));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (-1, 0)));
    			return;
    		case TARGET:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (-1, 0)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(-1, 0));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (-1, 0)));
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
    		return;
    	case PLAYER1:
    		return;
    	case PLAYER2:
    		return;
    	}
    }
    private void moveRight(int playerIndex) {
    	switch(blockType(players.get(playerIndex).actual, new Coordinate(1, 0))) {
    	case GROUND:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (1, 0)));
    		return;
    	case TARGET:
    		players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (1, 0)));
    		return;
    	case CRATE:
    		int crateIndex;
    		switch(blockType(players.get(playerIndex).actual, new Coordinate(2, 0))) {
    		case GROUND:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (1, 0)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(1, 0));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (1, 0)));
    			return;
    		case TARGET:
    			players.set(playerIndex, new DynamicField(FieldType.PLAYER1, players.get(playerIndex).actual, new Coordinate (1, 0)));
    			crateIndex = findCrateIndex(players.get(playerIndex).actual, new Coordinate(1, 0));
    			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, new Coordinate (1, 0)));
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
    
}
