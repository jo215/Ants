package program;

import java.util.HashMap;

import ui.UserInterface;
import world.World;
import ai.StateMachine;

/**
 * Manages the players, games and world which comprise an Ant tournament.
 * @author JOH
 * @version 0.1
 */
public class GameManager {

	// The set of uploaded brains - identified by their filename
	private HashMap<String, StateMachine> playerBrains;
	private HashMap<String, Integer> playerScores;

	private World world;
	
	/**
	 * Constructor.
	 */
	public GameManager() {
		playerBrains = new HashMap<>();
		playerScores = new HashMap<>();
		setWorld(null);
		
		UserInterface ui = new UserInterface(this);
	}
	
	/**
	 * Works out the correct matching of brains for a tournament to be held.
	 */
	public void assignMatches()
	{
		
	}
	
	/**
	 * Adds a new ant brain.
	 * @param name the name of the brain / player or team
	 * @param brain the StateMachine containing the brain
	 * @return true if succeeded, false if a brain with the same name is already loaded.
	 */
	public boolean addBrain(String name, StateMachine brain) {
		if (playerBrains.containsKey(name))
			return false;
		playerBrains.put(name, brain);
		playerScores.put(name, 0);
		return true;
	}
	
	/**
	 * Returns a brain from the list.
	 * @param name the name of the brain to return
	 * @return the brain if successful, null otherwise
	 */
	public StateMachine getBrain(String name) {
		if(playerBrains.containsKey(name))
			return playerBrains.get(name);
		return null;
	}
	
	/**
	 * Returns an ant brain's overall score.
	 * @param name the name of the brain to score
	 * @return the score if name valid, -1 otherwise
	 */
	public int getScore(String name) {
		if (playerScores.containsKey(name))
			return playerScores.get(name);
		return -1;
	}
	
	/**
	 * Adds some number to a brain's score.
	 * @param name the name of the brain to affect
	 * @param scoreToAdd the number to add to current score
	 * @return true if succeeded, false otherwise
	 */
	public boolean addToScore(String name, int scoreToAdd) {
		if (!playerScores.containsKey(name))
			return false;
		playerScores.put(name, playerScores.get(name) + scoreToAdd);
		return true;
	}
	
	/**
	 * Returns the total number of brains currently under management.
	 * @return the number of brains
	 */
	public int getTotalPlayers()
	{
		return playerBrains.size(); 	
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
