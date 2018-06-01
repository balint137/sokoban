
package logic;

import common.*;
import common.GameState.FieldType;
import gui.GUI;
import network.Server;

import java.io.*;
import java.util.*;

import java.nio.charset.Charset;

import static gui.GUI.MAX_MAP_SIZE;
/**
 * 
 * @author Baksa Domonkos
 * The class for the game logic
 * ICommand is an interface to get information from the GUI. 
 */
public class Logic implements ICommand {
    private IGameState g;
    private IGameState s;
    private boolean network;
    private boolean animationInProgress;

    private FieldType[][] mapStatic;
    private ArrayList<DynamicField> crates;
    private ArrayList<DynamicField> players;
    private ArrayList<DynamicField> mapDynamic;
    private ArrayList<Command> newCommands;
    private ArrayList<Command> commandsToExecute;

    private int numberOfSteps;
    private long startTime;
    private long finishTime;

    private String highscores;
    private int minSteps;
    private String name1;
    private String name2;
/**
 * Constructor for the game logic. It loads the layout of the map 
 * from a .txt file and sends it to all connected GUIs.
 * @param gui Instance of the GUI on the same machine.
 * @param mapFilePath Path to the file containing the map
 * @param network Network or local game
 * @param name1	Name of the first player
 * @param name2 Name of the second player
 * @param startTime Time at the start of the game used for the highscore
 */
    public Logic(GUI gui, String mapFilePath, boolean network, String name1, String name2, long startTime){

        newCommands = new ArrayList<>();
        commandsToExecute = new ArrayList<>();

        animationInProgress = false;

        g = gui;
        this.network = network;
        if(network) {
        	s = new Server(this);
        }

        this.name1 = name1;
        this.name2 = name2;

        mapStatic = new FieldType[MAX_MAP_SIZE][MAX_MAP_SIZE];
        crates = new ArrayList<>();
        players = new ArrayList<>();
        mapDynamic = new ArrayList<>();

        numberOfSteps = 0;
        this.startTime = startTime;

        try {
			loadMap(mapFilePath);
			g.onNewGameState(new GameState(GameState.GameStateType.STATIC_FIELDS, mapStatic));
			g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
			if(network) {
				s.onNewGameState(new GameState(GameState.GameStateType.STATIC_FIELDS, mapStatic));
				s.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
			}
			mapDynamic.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
/**
 * Function to call through the implemented interface
 */
    @Override
    public void onCommand(Command c) {
    	newCommands.add(c);
    	executeCommands();
    }
/**
 * Reads .txt file, iterates through the characters in it, and assigns different
 * map blocks to each. Static blocks are stored in an array (array indices are coordinates),
 * dynamic blocks are stored in an ArrayList.
 * @param filename Path to the file
 * @throws IOException
 */
    private void loadMap(String filename) throws IOException {
    	for (FieldType[] col : this.mapStatic) {
            Arrays.fill(col, FieldType.GROUND);
        }

        File map = new File(filename);
        Charset encoding = Charset.defaultCharset();
        try (InputStream in = new FileInputStream(map);
        		Reader reader = new InputStreamReader(in, encoding);
        		BufferedReader br = new BufferedReader(reader)) {
        	int r, i = 0;
        	while((r = reader.read()) != -1 && i<MAX_MAP_SIZE*MAX_MAP_SIZE) {
        		switch ((char)r) {
        		case '-':
        			mapStatic[i%MAX_MAP_SIZE][i/MAX_MAP_SIZE] = FieldType.GROUND;
        			break;
        		case '#':
        			mapStatic[i%MAX_MAP_SIZE][i/MAX_MAP_SIZE] = FieldType.WALL;
        			break;
        		case 'X':
        			mapStatic[i%MAX_MAP_SIZE][i/MAX_MAP_SIZE] = FieldType.TARGET;
        			break;
        		case '0':
        			crates.add(new DynamicField(FieldType.CRATE, new Coordinate(i%MAX_MAP_SIZE, i/MAX_MAP_SIZE)));
        			break;
        		case '1':
        			players.add(new DynamicField(FieldType.PLAYER1, new Coordinate(i%MAX_MAP_SIZE, i/MAX_MAP_SIZE)));
        			break;
        		case '2':
        			players.add(new DynamicField(FieldType.PLAYER2, new Coordinate(i%MAX_MAP_SIZE, i/MAX_MAP_SIZE)));
        			break;
        		case '\n':
        			i--;
        			break;
    			default:
        			break;
        		}
        		i++;
        	}
        	highscores = br.readLine();
        	g.onNewGameState(new GameState(GameState.GameStateType.HIGHSCORES, highscores));
        	mapDynamic.addAll(players);
        	mapDynamic.addAll(crates);
        }
    }
/**
 * Function to call when a new command arrives. Iterates through the ArrayList
 * of commands (ideally storing only one element), and acts accordingly.
 * If the command is KEY_PRESSED, tries to move the appropriate player,
 * if it is ANIMATION_DONE, resolves events connected to movement (checks win-lose conditions, clears variables used for movement)
 */
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
                    if (checkForVictory()) {
                        finishTime = System.currentTimeMillis() - startTime;
                        g.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.WIN));
                        if (network) {
                            s.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.WIN));
                        }
                    }
                    if (checkForLoss()) {
                        g.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.LOSE));
                        if (network) {
                            s.onNewGameState(new GameState(GameState.GameStateType.PHASE_UPDATE, GameState.GamePhase.LOSE));
                        }
                    }
                }
    			break;
    		}
    	}
    	commandsToExecute.clear();
    }
/**
 * Function to call if the issued command is KEY_PRESSED.
 * @param c Movement command to execute
 */
    private void processKeyPress(Command c) {
        if (c.movePlayer1 != null) {
            switch (c.movePlayer1) {
                case UP:
                    move(GameState.FieldType.PLAYER1, new Coordinate(0, -1));
                    break;
                case DOWN:
                    move(GameState.FieldType.PLAYER1, new Coordinate(0, 1));
                    break;
                case LEFT:
                    move(GameState.FieldType.PLAYER1, new Coordinate(-1, 0));
                    break;
                case RIGHT:
                    move(GameState.FieldType.PLAYER1, new Coordinate(1, 0));
                    break;
                default:
                    break;
            }
        }

        if (c.movePlayer2 != null) {
            switch (c.movePlayer2) {
                case UP:
                    move(GameState.FieldType.PLAYER2, new Coordinate(0, -1));
                    break;
                case DOWN:
                    move(GameState.FieldType.PLAYER2, new Coordinate(0, 1));
                    break;
                case LEFT:
                    move(GameState.FieldType.PLAYER2, new Coordinate(-1, 0));
                    break;
                case RIGHT:
                    move(GameState.FieldType.PLAYER2, new Coordinate(1, 0));
                    break;
                default:
                    break;
            }
        }
    }
/**
 * Function which moves the player if possible. Checks the next block in the
 * direction of the movement. If it is free, moves there. If it is a wall or a player,
 * does not move. If it is a crate, it calls moveCrate().
 * @param player Player to move
 * @param dir Direction to move
 */
    private void move(FieldType player, Coordinate dir) {
    	int playerIndex = 0;
    	for (int i=0;i<players.size(); i++) {
    		if (players.get(i).type.equals(player)) {
    			playerIndex = i;
    		}
    	}
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
		if(network) {
			s.onNewGameState(new GameState(GameState.GameStateType.MOVEMENTS, numberOfSteps));
 		}

		g.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
		if(network) {
			s.onNewGameState(new GameState(GameState.GameStateType.DYNAMIC_FIELDS, mapDynamic));
 		}
    }
/**
 * Function to call if the next block in the movement's direction is a crate.
 * Checks if the block next to the crate is free, and if so, moves both crate and player.
 * @param playerIndex Index of the player in the players ArrayList
 * @param dir Direction of movement
 */
    private void moveCrate(int playerIndex, Coordinate dir) {
		int crateIndex;
		switch(blockType(players.get(playerIndex).actual, new Coordinate(dir.getX()*2, dir.getY()*2))) {
		case GROUND:
			players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
			crateIndex = findCrateIndex(players.get(playerIndex).actual, dir);
			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, dir));
			numberOfSteps++;
			break;
		case TARGET:
			players.set(playerIndex, new DynamicField(players.get(playerIndex).type, players.get(playerIndex).actual, dir));
			crateIndex = findCrateIndex(players.get(playerIndex).actual, dir);
			crates.set(crateIndex, new DynamicField(FieldType.CRATE, crates.get(crateIndex).actual, dir));
			numberOfSteps++;
			break;
		case CRATE:
    		break;
		case PLAYER1:
    		break;
    	case PLAYER2:
    		break;
    	case WALL:
    		break;
		}
    }
/**
 * Finds the FieldType of a block at a distance from a player.
 * @param c Player's coordinates
 * @param difference X and Y distance of block from the player
 * @return Dynamic block type if there is a player or crate at that position,
 * 		   otherwise return static block type at that position.
 */
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
/**
 * Find index of a crate instance in the ArrayList containing them
 * @param c Coordinates of the player
 * @param difference  X and Y distance of crate from the player
 * @return Index of the crate at given position in the ArrayList
 */
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
/**
 * Clear deltas responsible for animations. Called, when the GUI finished animating the movement. 
 */
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
/**
 * Check win condition.
 * @return True if all crates are over a TARGET block.
 * 		   False else.
 */
    private boolean checkForVictory() {
    	for (DynamicField crate : crates) {
    		if(mapStatic[crate.actual.getX()][crate.actual.getY()] != FieldType.TARGET) {
    			return false;
    		}
    	}
    	return true;
    }
/**
 * Check lose condition.
 * @return	True if a crate is in a corner (made up of walls or crates)
 * 			False else.
 */
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
