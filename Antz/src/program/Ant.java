package program;

import ai.StateMachine;
import enums.E_Color;
import enums.E_Direction;

/**
 * Represents an individual Ant in the game world.
 * 
 * This is a stub.
 * 
 * @author JOH
 * @version 0.1
 */


public class Ant {

	private static int nextID = 0;		//	We want to track individual ants...	
	private int id;						//	so we automatically assign them an ID
	private E_Color color;				//	RED or BLACK
	private StateMachine stateMachine;	//	Reference to the state machine this ant shares with others of the same color; possibly not required
	private int currentState;			//	The current state ant is in within the StateMachine
	private int resting;				//	Track of how long we have to rest after a move
	private E_Direction direction;		//	The direction this ant is facing
	private boolean hasFood;			//	True if ant is carrying a food particle
	
	/**
	 * Constructor.
	 * @param color	the color of this ant
	 * @param stateMachine a reference to the state machine shared by all ants of this color
	 */
	public Ant(E_Color color, StateMachine stateMachine) {
		this.id = nextID;
		nextID ++;
		this.color = color;
		this.stateMachine = stateMachine;
		this.currentState = 0;
		hasFood = false;
		resting = 0;
		//	TODO : What direction should we start in?
	}

	/**
	 * Gets the color of this ant.
	 * @return the color of this ant
	 */
	public E_Color getColor() {
		return color;
	}

	/**
	 * Sets the color of this ant.
	 * @param color the color to set
	 */
	public void setColor(E_Color color) {
		this.color = color;
	}

	/**
	 * Gets the current state of this ant.
	 * @return the state
	 */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * Sets the current state of this ant
	 * @param currentState the state to set
	 */
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	/**
	 * Gets how long ant is resting for.
	 * @return how many more turns the ant will rest
	 */
	public int getResting() {
		return resting;
	}

	/**
	 * Sets how long the ant will have to rest.
	 * @param resting the number of turns
	 */
	public void setResting(int resting) {
		this.resting = resting;
	}

	/**
	 * Gets the direction this ant is facing.
	 * @return the direction
	 */
	public E_Direction getDirection() {
		return direction;
	}

	/**
	 * Sets the direction this ant is facing.
	 * @param direction the direction
	 */
	public void setDirection(E_Direction direction) {
		this.direction = direction;
	}

	/**
	 * Gets if this ant is carrying food.
	 * @return true if carrying food, false otherwise
	 */
	public boolean isHasFood() {
		return hasFood;
	}

	/**
	 * Gives/takes a food particle from this ant.
	 * @param hasFood true to give food, false to take food
	 */
	public void setHasFood(boolean hasFood) {
		this.hasFood = hasFood;
	}

	/**
	 * Gets the ID of this ant.
	 * @return the ID
	 */
	public int getId() {
		return id;
	}
		 
	
}
