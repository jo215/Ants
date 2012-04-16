package world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import enums.E_Color;
import enums.E_Condition;
import enums.E_Direction;
import enums.E_Terrain;

import ai.StateMachine;

import program.Ant;
/**
 * Represents an Ant world.
 * @author JOH
 * @version 0.2
 */
public class World {
	
	private Cell[][] cells;							//	The grid of cells which comprise this world
	private ArrayList<Ant> ants;					//	The ants in the world
	private StateMachine redBrain, blackBrain;		//	The two opposing player brains
	
	/**
	 * Private constructor.
	 */
	private World(Cell[][] cells) {
		this.cells = cells;
		ants = new ArrayList<>();
		redBrain = null;
		blackBrain = null;
		for (int i = 0; i < cells.length; i ++) {
			for (int j= 0 ; j < cells[0].length; j ++) {
				cells[i][j].setWorld(this);
			}
		}
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
            	if (cells[x][y].getAnt().getId() == id)
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
	 * @return the World object
	 */
	public static World generateMap() {
		int x = 144;
		int y = 144;
		Cell[][] cells = new Cell[x][y];
		
		// Make a rocky boarder, other cells clear
		for (int i = 0; i < y; i++){
			for (int j = 0; j < x; j++){
				if ((i == 0) || (i == y-1) || (j == 0) || (j == x-1)){
					cells[j][i] = new Cell(E_Terrain.ROCKY, 0, new Position(j, i));
				} else {
					cells[j][i] = new Cell(E_Terrain.CLEAR, 0, new Position(j, i));
				}
			}
		}
		
		// Map is divided into 18x18 regions, 64 regions altogether
		ArrayList<Integer> regions = new ArrayList<>();
		for (int i = 0; i < 64; i++){
			regions.add(i);
		}
		
		// Place elements randomly, one element per region
		int region;
		
		// Red Anthill
		region = regions.remove((int)(Math.random()*regions.size()));
		//System.out.println(region);
		placeAnthill(cells, E_Terrain.RED_ANTHILL, region);
		
		// Black Anthill
		region = regions.remove((int)(Math.random()*regions.size()));
		//System.out.println(region);
		placeAnthill(cells, E_Terrain.BLACK_ANTHILL, region);
		
		// Food
		for (int i = 0; i < 11; i++){
			region = regions.remove((int)(Math.random()*regions.size()));
			placeFoodBlob(cells, region);
		}
		
		// Rocks
		for (int i = 0; i < 14; i++){
			region = regions.remove((int)(Math.random()*regions.size()));
			placeRock(cells, region);
		}
		
		// Save map into a text file (for debugging purposes)
		saveWorld(cells);

		return new World(cells);
	}
	
	
	/**
	 * Places a rock in a given region of the map
	 * 
	 * @param cells the map to place the rock on
	 * @param region the region of the map
	 */
	private static void placeRock(Cell[][] cells, int region){
		ArrayList<Position> rock = new ArrayList<>();
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
		int xShift = (region%8)*18;
		int yShift = (region/8)*18;
		for (Position pos : rock){
			cells[pos.x+xShift][pos.y+yShift] = new Cell(E_Terrain.ROCKY, 
					0, new Position(pos.x+xShift, pos.y+yShift));
		}
		
	}

	/**
	 * Places a food blob (5x5 rectangle) in a given
	 * region of the map
	 * 
	 * @param cells map to place the food blob on
	 * @param region the region of the map
	 */
	private static void placeFoodBlob(Cell[][] cells, int region){
		Position[] foodBlob = new Position[25];
		int ind = 0;
		for (int i = 6; i < 11; i++){
			for (int j = 6; j < 11; j++){
				foodBlob[ind] = new Position(j, i);
				ind++;
			}
		}
		
		// Place the food
		int xShift = (region%8)*18;
		int yShift = (region/8)*18;
		for (Position pos : foodBlob){
			cells[pos.x+xShift][pos.y+yShift] = new Cell(E_Terrain.CLEAR, 
					5, new Position(pos.x+xShift, pos.y+yShift));
		}
		
	}
	
	/**
	 * Places an anthill of a given type 
	 * in a region of the map
	 * 
	 * @param cells map to place the anthill on
	 * @param anthillType type of anthill to place
	 * @param region region of the map to place the anthill on
	 */
	private static void placeAnthill(Cell[][] cells, E_Terrain anthillType, int region){
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
		int xShift = (region%8)*18;
		int yShift = (region/8)*18;
		for (Position pos : anthill){
			cells[pos.x+xShift][pos.y+yShift] = 
					new Cell(anthillType, 0, new Position(pos.x+xShift, pos.y+yShift));
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
			//	Problem with input - handle elsewhere
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

}
