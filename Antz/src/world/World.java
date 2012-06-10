package world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import enums.E_Color;
import enums.E_Condition;
import enums.E_Direction;
import enums.E_Terrain;
import ai.StateMachine;
import program.Ant;
import program.AntLogger;
import ui.GameplayScreen;

/**
 * Represents an Ant world.
 * @author JOH
 * @version 1
 */
public class World {
	
	private Cell[][] cells;							//	The grid of cells which comprise this world
	private Cell[][] unchangedCells;
	private ArrayList<Ant> ants;					//	The ants in the world
	private StateMachine redBrain, blackBrain;		//	The two opposing player brains
	private String redName, blackName;				//  Team names
	private int redScore, blackScore;				//	Running total of scores
	private GameplayScreen screen;
	private static final int MAXTURNS = 300000;
	private int sleepAmount = 0;
	private boolean isPaused;
	private int turn;
	private AntLogger logger;
	
	/**
	 * Private constructor.
	 */
	private World(Cell[][] cells) {
		this.cells = cells;
		for (int i = 0; i < cells.length; i ++) {
			for (int j = 0 ; j < cells[0].length; j ++) {
				cells[i][j].setWorld(this);
			}
		}
		this.unchangedCells = deepCopyCells(this.cells);	
		//logger = new AntLogger(this);
	}
	
		
	/**
	 * Starts a new game
	 */
	public void beginGame() {

		ants = new ArrayList<Ant>();
		
		//reset scores for a new game:
		redScore = 0;
		blackScore = 0;
				
		//reset the map (markers, food, ants):
		cells = deepCopyCells(unchangedCells);
		
		if(redBrain != null && redName != null && blackBrain != null && blackName != null){
			//create GUI from EDT:
			Runnable createGameplayScreen= new Runnable() {
				public void run() {
					screen = new GameplayScreen(World.this);
				}
			};
			try {
				SwingUtilities.invokeAndWait(createGameplayScreen);
			} catch (Exception e) {
				e.printStackTrace();
			}	
	
		
			//set up
			setStartingAnts();

			if (logger != null) {
				logger.logTurn();
			}
				
			//run game loop
			update(); 

		}
	}
	
	/**
	 * Sets up the initial ants in the world.
	 */
	private void setStartingAnts() {
		for (int i = 0; i < cells[0].length; i ++) {
			for (int j = 0 ; j < cells.length; j ++) {
				if (cells[j][i].getTerrain() == E_Terrain.BLACK_ANTHILL) {
					Ant ant = new Ant(E_Color.BLACK, blackBrain);
					setAntAt(new Position(j, i), ant);
					ants.add(ant);
				} else if (cells[j][i].getTerrain() == E_Terrain.RED_ANTHILL) {
					Ant ant = new Ant(E_Color.RED, redBrain);
					setAntAt(new Position(j, i), ant);
					ants.add(ant);
				}
			}
		}
	}

	/**
	 * Runs a loop of the game.
	 */
	private void update() {

		for (turn = 1; turn <= MAXTURNS; turn++) {
												
			while(isPaused) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			for (Ant ant : ants) {
				if (ant.isAlive()) {
					if (ant.getResting() > 0) {
						ant.setResting(ant.getResting() - 1);
					} else {
						Position p = findAnt(ant.getId());
						Cell cell = cells[p.x][p.y];
						ant.getStateMachine().step(ant, cell);
					}
				}
			}
			calcScores();
			
			//update GUI in EDT
			Runnable updateDisplay = new Runnable() {
				public void run() { screen.update(); }
			};
			SwingUtilities.invokeLater(updateDisplay);
			//screen.update();					
						
			//	dump turn info
			if (logger != null) {
				//	Choose which turns to log here
				if (turn < 10000)
					logger.logTurn();
			}

			//	Variable speed
			try {
				Thread.sleep(sleepAmount);
			} catch (InterruptedException e) {
				// Surely not a problem...
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * Close the gameplay screen.
	 */
	public void closeScreen(){
		screen.dispose();
	}
	
	/**
	 * Calculates the current score (1 for each food particle at home anthill).
	 */
	private void calcScores() {
		blackScore = 0;
		redScore = 0;
		for (int i = 0; i < cells.length; i ++) {
			for (int j = 0 ; j < cells[0].length; j ++) {
				if (cells[i][j].getTerrain() == E_Terrain.BLACK_ANTHILL) {
					blackScore += cells[i][j].getFoodAmount();
				} else if (cells[i][j].getTerrain() == E_Terrain.RED_ANTHILL) {
					redScore += cells[i][j].getFoodAmount();
				}
			}
		}
	}
	
	/**
	 * Checks if ant is surrounded and must therefore be killed.
	 * @param p the position to check
	 */
	public void checkForSurroundedAnts(Position p)
	{
		checkForSurroundedAntAt(p);
		for (int d = 0; d < 6; d++) {
			checkForSurroundedAntAt(adjacentCell(p, E_Direction.values()[d]).getPosition());
		}
	}

	/**
	 * Checks an individual hex for a surrounded ant.
	 * @param p the position to check
	 */
	private void checkForSurroundedAntAt(Position p) {
		if (someAntIsAt(p)) {
			Ant a = antAt(p);
			if (adjacentAnts(p, a.getColor().otherColor()) >= 5) {
				killAntAt(p);
				setFoodAt(p, foodAt(p) + 3);
				if (a.hasFood()) {
					setFoodAt(p, foodAt(p) + 1);
				}
			}
		}
	}

	/**
	 * Returns the number of adjacent ants to a given position
	 * @param p the position to check
	 * @param otherColor the enemy color
	 * @return
	 */
	private int adjacentAnts(Position p, E_Color otherColor) {
		int n = 0;
		for (int d = 0; d < 6 ; d++) {
			if (someAntIsAt(adjacentCell(p, E_Direction.values()[d]).getPosition()) && antAt(adjacentCell(p, E_Direction.values()[d]).getPosition()).getColor() == otherColor)
			{
				n++;
			}
		}
		return n;
	}


	/**
	 * Returns a specific cell.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the cell
	 */
	public Cell getCellAt(Position pos) {
		return cells[pos.x][pos.y];
	}
	
	/**
	 * Returns the width of the world.
	 * @return the width
	 */
	public int getWidth() {
		return cells.length;
	}
	
	/**
	 * Returns the height of the world.
	 * @return the height
	 */
	public int getHeight() {
		return cells[0].length;
	}
	
	/**
	 * Checks if any ant is at a particular location.
	 * @param x the x-coordinate
	 * @param y the y coordinate
	 * @return true if ant at location, false otherwise
	 */
	public boolean someAntIsAt(Position pos) {
		return cells[pos.x][pos.y].isAnt();
	}
	
	/**
	 * Gets the ant at a particular location.
	 * @param x the x-coordinate
	 * @param y the y coordinate
	 * @return the ant , or null if none
	 */
	public Ant antAt(Position pos) {
		return cells[pos.x][pos.y].getAnt();
	}
	
	/**
	 * Sets the ant at a particular location.
	 * @param x the x-coordinate
	 * @param y the y coordinate
	 * @param ant the ant
	 */
	public void setAntAt(Position pos, Ant ant) {
		cells[pos.x][pos.y].setAnt(ant);
	}
	
	/**
	 * Clears the ant at a particular location.
	 * @param x the x-coordinate
	 * @param y the y coordinate
	 */
	public void clearAntAt(Position pos) {
		cells[pos.x][pos.y].setAnt(null);
	}
	
	/**
	 * Returns true if the given ant (by id) is alive.
	 * @param id the ant's id
	 * @return true if alive, false otherwise
	 */
	public boolean antIsAlive(int id) {
		for (Ant ant : ants)
			if (ant.getId() == id && ant.isAlive())
				return true;
		return false;
	}
	
	/**
	 * Returns the x/y coordinates of an Ant.
	 * @param id the ant to find
	 * @return the position
	 */
	public Position findAnt(int id) {
        for (int x = 0; x < cells.length; x++)
            for (int y = 0; y < cells[x].length; y++)
            	if (cells[x][y].getAnt() != null && cells[x][y].getAnt().getId() == id)
            		return cells[x][y].getPosition();
        return null;
	}
	
	/**
	 * Kills the Ant at the given position, creating 3 food particles.
	 * @param p the position
	 */
	public void killAntAt(Position p)
	{
		cells[p.x][p.y].killAnt();
	}
	
	/**
	 * Returns how much food is at a certain position.
	 * @param p the position
	 * @return the amount of food
	 */
	public int foodAt(Position p) {
		return cells[p.x][p.y].getFoodAmount();
	}
	
	/**
	 * Sets the amount of food at a certain location.
	 * @param p the position
	 * @param amount the amount of food to set
	 */
	public void setFoodAt(Position p, int amount) {
		cells[p.x][p.y].setFoodAmount(amount);
	}
	
	/**
	 * Returns true if an anthill of the given color is at the given location.
	 * @param p the position
	 * @param c the color
	 * @return true if an anthill is present, false otherwise
	 */
	public boolean getAnthillAt(Position p, E_Color c) {
		if (c == E_Color.BLACK)
			return cells[p.x][p.y].getTerrain() == E_Terrain.BLACK_ANTHILL;
		return cells[p.x][p.y].getTerrain() == E_Terrain.RED_ANTHILL;
	}
	
	/**
	 * Checks whether a given condition holds at a given position.
	 * @param p the position
	 * @param cond the condition
	 * @param c the color of the ant doing the sensing
	 * @return true if cond holds at p, false otherwise
	 */
	public boolean cellMatches(Position p, E_Condition cond, E_Color c) {
		switch (cond)
		{
			case FRIEND:
				return someAntIsAt(p) && antAt(p).getColor() == c;
			case FOE:
				return someAntIsAt(p) && antAt(p).getColor() != c;
			case FRIENDWITHFOOD:
				return someAntIsAt(p) && antAt(p).getColor() == c && antAt(p).hasFood();
			case FOEWITHFOOD:
				return someAntIsAt(p) && antAt(p).getColor() != c && antAt(p).hasFood();
			case FOOD:
				return foodAt(p) > 0;
			case ROCK:
				return cells[p.x][p.y].getTerrain() == E_Terrain.ROCKY;
			case MARKER0:
				return cells[p.x][p.y].checkMarker(c, 0);
			case MARKER1:
				return cells[p.x][p.y].checkMarker(c, 1);
			case MARKER2:
				return cells[p.x][p.y].checkMarker(c, 2);
			case MARKER3:
				return cells[p.x][p.y].checkMarker(c, 3);
			case MARKER4:
				return cells[p.x][p.y].checkMarker(c, 4);
			case MARKER5:
				return cells[p.x][p.y].checkMarker(c, 5);
			case FOEMARKER:
				return cells[p.x][p.y].checkAnyMarker(c.otherColor());
			case HOME:
				return getAnthillAt(p, c);
			case FOEHOME:
				return getAnthillAt(p, c.otherColor());
				
		}
		return false;
	}
	
	/**
	 * Returns the adjacent cell in the given direction.
	 * @param p the current position
	 * @param dir the direction
	 * @return the position of the adjacent cell in the given direction, or null if off the map
	 */
	public Cell adjacentCell(Position p, E_Direction dir) {
		Position adjacent = null;
		switch (dir) {
			case EAST:
				adjacent = new Position(p.x + 1, p.y);
				break;
			case SOUTHEAST:
				if (p.y % 2 == 0)	//	Even
					adjacent = new Position(p.x, p.y +1);
				else				//	Odd
					adjacent = new Position(p.x + 1 , p.y + 1);
				break;
			case SOUTHWEST:
				if (p.y % 2 == 0)	//	Even
					adjacent = new Position(p.x - 1, p.y + 1);
				else				//	Odd
					adjacent = new Position(p.x, p.y + 1);
				break;
			case WEST:
				adjacent = new Position(p.x - 1, p.y);
				break;
			case NORTHWEST:
				if (p.y % 2 == 0)	//	Even
					adjacent = new Position(p.x - 1, p.y - 1);
				else				//	Odd
					adjacent = new Position(p.x, p.y - 1);
				break;
			case NORTHEAST:
				if (p.y % 2 == 0)	//	Even
					adjacent = new Position(p.x, p.y - 1);
				else				//	Odd
					adjacent = new Position(p.x + 1, p.y - 1);
				break;
		}
		//	Check calculated position is not off the map
		if (adjacent.x < 0 || adjacent.x > cells.length || adjacent.y < 0 || adjacent.y > cells[0].length)
			return null;
		return cells[adjacent.x][adjacent.y];
	}
	
	/**
	 * Generate a random map.
	 * @return the World object
	 */
	public static World generateMap() {
		int x = 150;
		int y = 150;
		Cell[][] cells = new Cell[x][y];
		
		// Make a rocky border, other cells clear
		for (int i = 0; i < y; i++){
			for (int j = 0; j < x; j++){
				if ((i == 0) || (i == y-1) || (j == 0) || (j == x-1)){
					cells[j][i] = new Cell(E_Terrain.ROCKY, 0, new Position(j, i));
				} else {
					cells[j][i] = new Cell(E_Terrain.CLEAR, 0, new Position(j, i));
				}
			}
		}
		
		// create all possible origin points for 18x18 regions that the elements
		// will be placed in
		ArrayList<Position> origins = new ArrayList<Position>();
		for (int i = 0; i <= y - 18; i += 2){ // only even rows to preserve element's shape
			for (int j = 0; j <= x - 18; j++){
				origins.add(new Position(j, i));
			}
			
		}
		
		// Place elements randomly
		Position origin;
		
		// Red Anthill
		origin = origins.get((int)(Math.random()*origins.size())); // pick origin point
		origins = clearRegion(origins, origin);  //remove relevant points from available origin points
		placeAnthill(cells, E_Terrain.RED_ANTHILL, origin);
		
		// Black Anthill
		origin = origins.get((int)(Math.random()*origins.size())); //pick origin point
		origins = clearRegion(origins, origin);  //remove relevant points from available origin points
		placeAnthill(cells, E_Terrain.BLACK_ANTHILL, origin);
		
		// Food
		for (int i = 0; i < 11; i++){
			origin = origins.get((int)(Math.random()*origins.size())); //pick origin point
			origins = clearRegion(origins, origin);  //remove relevant points from available origin points
			placeFoodBlob(cells, origin);
		}
		
		// Rocks
		for (int i = 0; i < 14; i++){
			origin = origins.get((int)(Math.random()*origins.size())); //pick origin point
			origins = clearRegion(origins, origin);  //remove relevant points from available origin points
			placeRock(cells, origin);
		}
		
		// Save map into a text file (for debugging purposes)
		saveWorld(cells);

		return new World(cells);
	}
	
	
	/**
	 * From arraylist of positions removes positions
	 * with x and y between x1-18, x1+18, y1-18 and
	 * y1+18, where x1,y1 are coordinates of the 
	 * provided position
	 * 
	 * @param positions arraylist of positions
	 * @param origin provided position with x1, y1
	 * @return newPositions positions after the removes
	 */
	private static ArrayList<Position> clearRegion(ArrayList<Position> positions, Position origin){		
		Iterator<Position> itr = positions.iterator();
		Position pos;
		while (itr.hasNext()) {
			pos = itr.next();
			if((origin.x - 18 < pos.x) && (pos.x < origin.x + 18) && 
					(origin.y - 18 < pos.y) && (pos.y < origin.y + 18)) {
				itr.remove();
			}
		}
		
		return positions;
	}
	
	
	/**
	 * Places a rock in a given region of the map
	 * 
	 * @param cells the map to place the rock on
	 * @param origin upper left corner of the 18x18 region of the map
	 * that the rock would be placed in
	 */
	private static void placeRock(Cell[][] cells, Position origin){
		ArrayList<Position> rock = new ArrayList<Position>();
		// Select rock shape at random 
		int rockType = (int)(7*Math.random());
		if (rockType == 0){
			rock.addAll(Arrays.asList(new Position(6, 4), new Position(7, 4),
					new Position(8, 4), new Position(9, 4),
					new Position(10, 4), new Position(11, 4),
					new Position(5, 5), new Position(6, 5),
					new Position(7, 5), new Position(8, 5),
					new Position(9, 5), new Position(10, 5),
					new Position(11, 5),
					new Position(5, 6), new Position(6, 6),
					new Position(11, 6), new Position(12, 6),
					new Position(4, 7), new Position(5, 7),
					new Position(11, 7), new Position(12, 7),
					new Position(4, 8), new Position(5, 8),
					new Position(12, 8), new Position(13, 8),
					new Position(3,9), new Position(4,9),
					new Position(12,9), new Position(13,9),
					new Position(4,10), new Position(5,10),
					new Position(12,10), new Position(13,10),
					new Position(4,11), new Position(5,11),
					new Position(11,11), new Position(12,11),
					new Position(5,12), new Position(6,12),
					new Position(11,12), new Position(12,12),
					new Position(5,13), new Position(6,13),
					new Position(7,13), new Position(8,13),
					new Position(9,13), new Position(10,13), 
					new Position(11,13),
					new Position(6,14), new Position(7,14),
					new Position(8,14), new Position(9,14),
					new Position(10,14), new Position(11,14)
			));
		} else if (rockType == 1){
			for (int i = 8; i<10; i++){
				for (int j = 2; j<16; j++){						
					rock.add(new Position(j, i));
				}
			}
		} else if (rockType == 2){
			rock.addAll(Arrays.asList(new Position(10,3), new Position(11,3),
					new Position(10,4), new Position(11,4),
					new Position(12,4),
					new Position(9,5), new Position(10,5),
					new Position(11,5), new Position(12,5),
					new Position(9,6), new Position(10,6),
					new Position(12,6), new Position(13,6),
					new Position(8,7), new Position(9,7),
					new Position(12,7), new Position(13,7),
					new Position(8,8), new Position(9,8),
					new Position(13,8), new Position(14,8),
					new Position(2,9), new Position(7,9),
					new Position(8,9), new Position(13,9),
					new Position(14,9),
					new Position(2,10), new Position(3,10),
					new Position(7,10), new Position(8,10),
					new Position(14,10), new Position(15,10),
					new Position(2,11), new Position(3,11),
					new Position(6,11), new Position(7,11),
					new Position(14,11), new Position(15,11),
					new Position(3,12), new Position(4,12),
					new Position(6,12), new Position(7,12),
					new Position(15,12),
					new Position(3 ,13), new Position(4,13), 
					new Position(5,13), new Position(6,13),
					new Position(4,14), new Position(5,14),
					new Position(6,14),
					new Position(4,15), new Position(5,15)
			));
			
		
		} else if (rockType == 3){
			rock.addAll(Arrays.asList(new Position(12,3),
					new Position(12,4), new Position(13,4),
					new Position(11,5), new Position(12,5),
					new Position(11,6), new Position(12,6),
					new Position(10,7), new Position(11,7),
					new Position(10,8),	new Position(11,8),
					new Position(6,9), new Position(7,9),
					new Position(8,9), new Position(9,9),
					new Position(10,9),
					new Position(6,10), new Position(7,10),
					new Position(8,10), new Position(9,10),
					new Position(10,10),
					new Position(5,11), new Position(6,11),
					new Position(5,12), new Position(6,12),
					new Position(4,13), new Position(5,13),
					new Position(4,14), new Position(5,14),
					new Position(3,15), new Position(4,15)
			));
		} else if (rockType == 4){
			rock.addAll(Arrays.asList(new Position(5,3),
					new Position(6,3), new Position(7,3),
					new Position(8,3), new Position(9,3),
					new Position(5,4), new Position(6,4),
					new Position(7,4), new Position(8,4),
					new Position(9,4),
					new Position(4,5), new Position(5,5),
					new Position(4,6), new Position(5,6),
					new Position(3,7), new Position(4,7),
					new Position(3,8), new Position(4,8),
					new Position(2,9), new Position(3,9),
					new Position(3,10), new Position(4,10),
					new Position(3,11), new Position(4,11),
					new Position(4,12), new Position(5,12),
					new Position(4,13), new Position(5,13),
					new Position(5,14), new Position(6,14),
					new Position(7,14), new Position(8,14),
					new Position(9,14),
					new Position(5,15), new Position(6,15),
					new Position(7,15), new Position(8,15),
					new Position(9,15)
			));
		} else if (rockType == 5) {
			rock.addAll(Arrays.asList(new Position(9,3),
					new Position(10,3),
					new Position(9,4), new Position(10,4),
					new Position(11,4), new Position(12,4),
					new Position(13,4),
					new Position(9,5), new Position(10,5),
					new Position(11,5), new Position(12,5),
					new Position(13,5),
					new Position(10,6), new Position(11,6),
					new Position(12,6), new Position(13,6),
					new Position(2,7), new Position(3,7),
					new Position(9,7), new Position(10,7),
					new Position(11,7),
					new Position(2,8), new Position(3,8),
					new Position(4,8), new Position(10,8), 
					new Position(11,8),
					new Position(2,9), new Position(3,9),
					new Position(4,9), new Position(5,9),
					new Position(11,9), new Position(12,9),
					new Position(4,10), new Position(5,10),
					new Position(6,10), new Position(11,10),
					new Position(12,10), new Position(13,10),
					new Position(14,10), new Position(15,10),
					new Position(4,11), new Position(5,11),
					new Position(9,11), new Position(10,11),
					new Position(11,11), new Position(12,11),
					new Position(13,11), new Position(14,11),
					new Position(15,11),
					new Position(5,12), new Position(6,12),
					new Position(9,12), new Position(10,12),
					new Position(11,12), new Position(14,12),
					new Position(15,12),
					new Position(4,13), new Position(5,13), 
					new Position(6,13), new Position(7,13), 
					new Position(8,13), new Position(9,13), 
					new Position(10,13), 
					new Position(5,14), new Position(6,14), 
					new Position(7,14), new Position(8,14), 
					new Position(9,14), 
					new Position(7,15), new Position(8,15)
			));
		} else {
			rock.addAll(Arrays.asList(new Position(4,3),
					new Position(5,3), new Position(11,3),
					new Position(12,3),
					new Position(4,4), new Position(5,4),
					new Position(6,4), new Position(9,4),
					new Position(10,4), new Position(11,4),
					new Position(12,4), new Position(13,4),
					new Position(4,5), new Position(5,5),
					new Position(6,5), new Position(7,5),
					new Position(8,5), new Position(9,5),
					new Position(10,5), new Position(11,5),
					new Position(12,5),
					new Position(6,6), new Position(7,6),
					new Position(8,6), new Position(9,6),
					new Position(10,6), new Position(12,6),
					new Position(13,6),
					new Position(6,7), new Position(7,7),
					new Position(11,7), new Position(12,7),
					new Position(13,7),
					new Position(6,8), new Position(7,8),
					new Position(12,8), new Position(13,8),
					new Position(5,9), new Position(6,9),
					new Position(7,9),
					new Position(4,10), new Position(5,10),
					new Position(6,10), new Position(7,10),
					new Position(8,10), new Position(9,10),
					new Position(3,11), new Position(4,11),
					new Position(5,11), new Position(7,11),
					new Position(8,11), new Position(9,11),
					new Position(4,12), new Position(5,12),
					new Position(8,12), new Position(9,12),
					new Position(10,12), new Position(11,12),
					new Position(4,13), new Position(5,13),
					new Position(9,13), new Position(10,13),
					new Position(11,13),
					new Position(4,14), new Position(5,14),
					new Position(6,14), new Position(10,14),
					new Position(11,14),
					new Position(4,15), new Position(5,15)
			));
		}
		
		// Place the rock
		for (Position pos : rock){
			cells[pos.x + origin.x][pos.y + origin.y] = new Cell(E_Terrain.ROCKY, 
					0, new Position(pos.x + origin.x, pos.y + origin.y));
		}
		
	}

	/**
	 * Places a food blob (5x5 rectangle) in a given
	 * region of the map
	 * 
	 * @param cells map to place the food blob on
	 * @param origin upper left corner of the 18x18 region of the map
	 * that the food blob would be placed in
	 */
	private static void placeFoodBlob(Cell[][] cells, Position origin){
		Position[] foodBlob = new Position[25];
		int ind = 0;
		for (int i = 6; i < 11; i++){
			for (int j = 6; j < 11; j++){
				foodBlob[ind] = new Position(j, i);
				ind++;
			}
		}
		
		// Place the food
		for (Position pos : foodBlob){
			cells[pos.x + origin.x][pos.y + origin.y] = new Cell(E_Terrain.CLEAR, 
					5, new Position(pos.x + origin.x, pos.y + origin.y));
		}
		
	}
	
	/**
	 * Places an anthill of a given type 
	 * in a region of the map
	 * 
	 * @param cells map to place the anthill on
	 * @param anthillType type of anthill to place
	 * @param origin upper left corner of the 18x18 region of the map
	 * that the anthill would be placed in
	 */
	private static void placeAnthill(Cell[][] cells, E_Terrain anthillType, Position origin){
		Position[] anthill = {new Position(5, 2), new Position(6, 2), 
				new Position(7, 2), new Position(7, 2), new Position(8, 2),
				new Position(9, 2), new Position(10, 2), new Position(11, 2),
				new Position(4, 3), new Position(5, 3), new Position(6, 3), 
				new Position(7, 3), new Position(8, 3), new Position(9, 3), 
				new Position(10, 3), new Position(11, 3), new Position(4, 4), 
				new Position(5, 4), new Position(6, 4), new Position(7, 4), 
				new Position(8, 4), new Position(9, 4), new Position(10, 4), 
				new Position(11, 4),new Position(12, 4), new Position(3, 5), 
				new Position(4, 5), new Position(5, 5), new Position(6, 5), 
				new Position(7, 5),new Position(8, 5), new Position(9, 5), 
				new Position(10, 5),new Position(11, 5), new Position(12, 5),
				new Position(3, 6), new Position(4, 6), new Position(5, 6), 
				new Position(6, 6), new Position(7, 6), new Position(8, 6), 
				new Position(9, 6), new Position(10, 6), new Position(11, 6),
				new Position(12, 6), new Position(13, 6), new Position(2, 7), 
				new Position(3, 7), new Position(4, 7), new Position(5, 7), 
				new Position(6, 7), new Position(7, 7), new Position(8, 7), 
				new Position(9, 7), new Position(10, 7), new Position(11, 7),
				new Position(12, 7), new Position(13, 7), new Position(2, 8), 
				new Position(3, 8), new Position(4, 8), new Position(5, 8), 
				new Position(6, 8), new Position(7, 8), new Position(8, 8), 
				new Position(9, 8), new Position(10, 8), new Position(11, 8),
				new Position(12, 8), new Position(13, 8), new Position(14, 8),
				new Position(2, 9), 
				new Position(3, 9), new Position(4, 9), new Position(5, 9), 
				new Position(6, 9), new Position(7, 9), new Position(8, 9), 
				new Position(9, 9), new Position(10, 9), new Position(11, 9),
				new Position(12, 9), new Position(13, 9),
				new Position(3, 10), new Position(4, 10), new Position(5, 10), 
				new Position(6, 10), new Position(7, 10), new Position(8, 10), 
				new Position(9, 10), new Position(10, 10), new Position(11, 10),
				new Position(12, 10), new Position(13, 10),
				new Position(3, 11), 
				new Position(4, 11), new Position(5, 11), new Position(6, 11), 
				new Position(7, 11),new Position(8, 11), new Position(9, 11), 
				new Position(10, 11),new Position(11, 11), new Position(12, 11),
				new Position(4, 12), 
				new Position(5, 12), new Position(6, 12), new Position(7, 12), 
				new Position(8, 12), new Position(9, 12), new Position(10, 12), 
				new Position(11, 12),new Position(12, 12),
				new Position(4, 13), new Position(5, 13), new Position(6, 13), 
				new Position(7, 13), new Position(8, 13), new Position(9, 13), 
				new Position(10, 13), new Position(11, 13),
				new Position(5, 14), new Position(6, 14), 
				new Position(7, 14), new Position(7, 14), new Position(8, 14),
				new Position(9, 14), new Position(10, 14), new Position(11, 14)};
	
		// Place the anthill
		for (Position pos : anthill){
			cells[pos.x + origin.x][pos.y + origin.y] = 
					new Cell(anthillType, 0, new Position(pos.x + origin.x, pos.y + origin.y));
		}
		
		
	}
	
	
	/**
	 * Saves the map to the text file.
	 * 
	 * @param cells 2d array of cells representing the map
	 */
	private static void saveWorld(Cell[][] cells){
		try {
			File mapfile = new File("automap.txt");
			BufferedWriter mapout = new BufferedWriter(new FileWriter(mapfile));
			mapout.write(Integer.toString(cells[0].length));
			mapout.newLine();
			mapout.write(Integer.toString(cells.length));
			mapout.newLine();
			for (int i = 0; i < cells.length; i++){
				// Indent every second line
				if (i % 2 == 1){
					mapout.write(" ");
				}
				for (int j = 0; j < cells[0].length; j++){
					switch(cells[j][i].getTerrain()){
						case ROCKY:
							mapout.write("#");
							break;
						case RED_ANTHILL:
							mapout.write("+");
							break;
						case BLACK_ANTHILL:
							mapout.write("-");
							break;
						case CLEAR:
							if (cells[j][i].getFoodAmount() == 0){
								mapout.write(".");
							} else {
								mapout.write(Integer.toString(cells[j][i].getFoodAmount()));
							}
							break;
					}
					mapout.write(" "); //spaces between cells
				}
				mapout.newLine();
			}
			
			mapout.close();
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Copies a 2d array of cells 
	 * @param original original array
	 * @return copy of the original array
	 */
	private static Cell[][] deepCopyCells(Cell[][] original){
		Cell[][] copy = new Cell[original.length][original[0].length];
		for (int i = 0; i < original.length; i ++) {
			for (int j = 0; j < original[0].length; j ++) {
				copy[i][j] = Cell.copy(original[i][j]);
			}
		}
		return copy;
	}
	
	/**
	 * Parses a world file to World object.
	 * @param absolutePath the path to the World file
	 * @return the World object or null if a problem was encountered
	 */
	public static World parseWorld(String absolutePath) {
		//	Open the file stream
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath)));
			//	Read each line of the file
			//	First two lines are dimensions of map
			String line = br.readLine();
			int x = Integer.parseInt(line);
			line = br.readLine();
			int y = Integer.parseInt(line);
			//	Setup grid of map cells - note x, y not row, column - is this OK?
			Cell[][] cells = new Cell[x][y];
	
			//	Now read y lines of length x
			for (int i = 0; i < y; i++) {
				line = br.readLine();
				line = line.replace(" ", "");
				if (line.length() != x)
					throw new IllegalArgumentException();
				
				for (int j = 0; j < x; j ++) {
					switch (line.charAt(j)){
						case '.':			//	Clear cell
							cells[j][i] = new Cell(E_Terrain.CLEAR, 0, new Position(j, i));
							break;
						case '#':			//	Rocky cell
							cells[j][i] = new Cell(E_Terrain.ROCKY, 0, new Position(j, i));
							break;
						case '+':			//	Red anthill
							cells[j][i] = new Cell(E_Terrain.RED_ANTHILL, 0, new Position(j, i));
							break;	
						case '-':			//	Black anthill
							cells[j][i] = new Cell(E_Terrain.BLACK_ANTHILL, 0, new Position(j, i));
							break;
						default:			//	Clear cell with food
							int food = Character.getNumericValue(line.charAt(j));
							if (food < 1 || food > 9)
								throw new IllegalArgumentException();
							cells[j][i] = new Cell(E_Terrain.CLEAR, food, new Position(j, i));
							break;
					}
				}
			}
				
			// check if borders are rocky
			for (int i = 0; i < y; i++){
				for (int j = 0; j < x; j++){
					if ((i == 0) || (i == y-1) || (j == 0) || (j == x-1)){
						if (cells[j][i].getTerrain() != E_Terrain.ROCKY){
							throw new IllegalArgumentException();
						}
					} 
				}
			}//end of rocky borders check
			
			
			return new World(cells);
			
		} catch (Exception e) {
			//	This error caught dealt with later.
			return null;
		}
	}
	
	/**
	 * Returns red team's name
	 * @return redName
	 */
	public String getRedName(){
		return redName;
	}
	
	/**
	 * Returns black team's name
	 * @return blackName
	 */
	public String getBlackName(){
		return blackName;
	}
	
	/**
	 * Swaps the two ant brains to the opposite color.
	 */
	public void swapBrains() {
		StateMachine swap = redBrain;
		String swapString = redName;
		redBrain = blackBrain;
		blackBrain = swap;
		redName = blackName;
		blackName = swapString;
	}

	/**
	 * Returns the red players brain.
	 * @return
	 */
	public StateMachine getRedBrain() {
		return redBrain;
	}

	/**
	 * Sets the red player's brain.
	 * @param redName name of the red team
	 * @param redBrain
	 */
	public void setRedBrain(String redName, StateMachine redBrain) {
		this.redName = redName;
		this.redBrain = redBrain;
	}
	

	/**
	 * Returns the black player's brain.
	 */
	public StateMachine getBlackBrain() {
		return blackBrain;
	}

	/**
	 * Sets the black player's brain.
	 * @param blackBrain
	 * @param blackName name of the black team
	 */
	public void setBlackBrain(String blackName, StateMachine blackBrain) {
		this.blackBrain = blackBrain;
		this.blackName = blackName;
	}

	/**
	 * Returns the red score.
	 * @return the score
	 */
	public int getRedScore() {
		return redScore;
	}

	/**
	 * Sets the red score.
	 * @param redScore
	 */
	public void setRedScore(int redScore) {
		this.redScore = redScore;
	}

	/**
	 * Gets the black score.
	 * @return the score
	 */
	public int getBlackScore() {
		return blackScore;
	}

	/**
	 * Sets the black score.
	 * @param blackScore
	 */
	public void setBlackScore(int blackScore) {
		this.blackScore = blackScore;
	}

	/**
	 * Checks if game paused.
	 * @return true if paused
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Sets the game's pause status.
	 * @param isPaused
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * Gets the amount the game sleeps for between each turn.
	 * @return the amount
	 */
	public int getSleepAmount() {
		return sleepAmount;
	}

	/**
	 * Sets the amount the game sleeps for between turns.
	 * @param sleepAmount
	 */
	public void setSleepAmount(int sleepAmount) {
		this.sleepAmount = sleepAmount;
	}

	/**
	 * Gets the current turn.
	 * @return the turn
	 */
	public int getTurn() {
		return turn;
	}
}
