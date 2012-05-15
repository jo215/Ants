package world;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import enums.E_Color;
import enums.E_Condition;
import enums.E_Direction;
import enums.E_Terrain;

import ai.StateMachine;

import program.Ant;
import ui.GameplayScreen;
/**
 * Represents an Ant world.
 * @author JOH
 * @version 0.2
 */
public class World {
	
	private Cell[][] cells;							//	The grid of cells which comprise this world
	private ArrayList<Ant> ants;					//	The ants in the world
	private StateMachine redBrain, blackBrain;		//	The two opposing player brains
	private int redScore, blackScore;				//	Running total of scores
	private GameplayScreen screen;
	private static final int MAXTURNS = 300000;
	private int sleepAmount = 0;

	
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
	}
	
	/**
	 * Starts a new game
	 */
	public void beginGame(StateMachine redBrain, StateMachine blackBrain) {
		ants = new ArrayList<>();
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.screen = new GameplayScreen(this);
		setStartingAnts();
		update();
	}
	
	/**
	 * Sets up the initial ants in the world.
	 */
	private void setStartingAnts() {
		for (int i = 0; i < cells.length; i ++) {
			for (int j = 0 ; j < cells[0].length; j ++) {
				if (cells[i][j].getTerrain() == E_Terrain.BLACK_ANTHILL) {
					Ant ant = new Ant(E_Color.BLACK, blackBrain);
					setAntAt(new Position(i, j), ant);
					ants.add(ant);
				} else if (cells[i][j].getTerrain() == E_Terrain.RED_ANTHILL) {
					Ant ant = new Ant(E_Color.RED, redBrain);
					setAntAt(new Position(i, j), ant);
					ants.add(ant);
				}
			}
		}		
	}

	/**
	 * Runs a loop of the game.
	 */
	private void update() {
		for (int turn = 0; turn < MAXTURNS; turn++) {
			for (Ant ant : ants) {
				if (ant.isAlive()) {
					if (ant.getResting() > 0) {
						ant.setResting(ant.getResting() - 1);
					} else {
						Position p = this.findAnt(ant.getId());
						Cell cell = cells[p.x][p.y];
						ant.getStateMachine().step(ant, cell);
					}
				}
			}
			calcScores();
			//	Update the display
			screen.update();
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
	 * Returns a specific cell.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return
	 */
	public Cell getCellAt(Position pos) {
		return cells[pos.x][pos.y];
	}
	
	/**
	 * Returns the width of the world.
	 * @return
	 */
	public int getWidth() {
		return cells.length;
	}
	
	/**
	 * Returns the height of the world.
	 * @return
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
	 * @param id
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
	 * Some random way of generating a map
	 */
	public World generateMap() {
		//	TODO 
		return null;
	}
	
	/**
	 * Parses a world file to World object.
	 * @param absolutePath the path to the World file
	 * @return the World object or null if a problem was encountered
	 */
	public static World parseWorld(String absolutePath) {
		//	Open the file stream
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath)))) {
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
							cells[j][i] = new Cell(E_Terrain.CLEAR, Character.getNumericValue(line.charAt(j)), new Position(j, i));
							break;
					}
				}
			}
			return new World(cells);
		} catch (Exception e) {
			System.out.println("Error parsing world file.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Swaps the two ant brains to the opposite color.
	 */
	public void swapBrains() {
		StateMachine swap = redBrain;
		redBrain = blackBrain;
		blackBrain = swap;
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
	 * @param redBrain
	 */
	public void setRedBrain(StateMachine redBrain) {
		this.redBrain = redBrain;
	}

	/**
	 * Returns the black player's brain.
	 * @return
	 */
	public StateMachine getBlackBrain() {
		return blackBrain;
	}

	/**
	 * Sets the black player's brain.
	 * @param blackBrain
	 */
	public void setBlackBrain(StateMachine blackBrain) {
		this.blackBrain = blackBrain;
	}


	public GameplayScreen getScreen() {
		return screen;
	}


	public void setScreen(GameplayScreen screen) {
		this.screen = screen;
	}

	public int getRedScore() {
		return redScore;
	}

	public void setRedScore(int redScore) {
		this.redScore = redScore;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public void setBlackScore(int blackScore) {
		this.blackScore = blackScore;
	}
}
