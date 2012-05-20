package program;

import enums.E_Color;
import world.Cell;
import world.Position;
import world.World;

/**
 * This class provides end-of turn logging facilities for QA.
 * @author JOH
 * @version 1
 */
public class AntLogger {

	private World world;
	
	/**
	 * Constructor.
	 * @param world
	 */
	public AntLogger(World world) {
		this.world = world;
		System.out.println("random seed: " + RandGenerator.getSeed() + "\n");
	}
	
	/**
	 * Prints out the current state of the world.
	 */
	public void logTurn() {
		System.out.println("\nAfter round " + world.getTurn() + "...");
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				Cell cell = world.getCellAt(new Position(y, x));
				String cellContents = food(cell) + terrain(cell) +  markerInfo(cell, E_Color.RED) + markerInfo(cell, E_Color.BLACK) + antInfo(cell);
				
				System.out.println("cell (" + y +", " + x + "): " + cellContents);
			}
		}
	}

	private String antInfo(Cell cell) {
		String info = "";
		if (cell.isAnt()) {
			Ant ant = cell.getAnt();
			if (ant.getColor() == E_Color.RED) {
				info += "red ";
			} else {
				info += "black ";
			}
			int food = 0;
			if (ant.hasFood()) {
				food = 1;
			}
			info += "ant of id " + ant.getId() + ", " + ant.getDirection() +", food " + food + ", state " + ant.getCurrentState() + ", resting " + ant.getResting(); 
		}
		return info;
	}

	private String markerInfo(Cell cell, E_Color color) {
		String info = "";
		String colorName;
		if (color == E_Color.RED)
			colorName = "red";
		else
			colorName = "black";
		// 	Marker info
		if (cell.checkAnyMarker(color)) {
			info += colorName + " marks: ";
			for (int i = 0; i < 6; i ++) {
				if (cell.checkMarker(color, i)) {
					info += i;
				}
			}
			info +="; ";
		}
		return info;
	}

	private String food(Cell cell) {
		if (cell.getFoodAmount() > 0)
			return cell.getFoodAmount() + " food; ";
		return "";
	}

	private String terrain(Cell cell) {
		switch (cell.getTerrain())
		{
		case BLACK_ANTHILL:
			return "black hill; ";
		case RED_ANTHILL:
			return "red hill; ";
		case ROCKY:
			return "rock ";
		}
		return "";
	}
}
