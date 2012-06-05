package program;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ui.BracketScreen;
import ui.StartUpScreen;
import world.World;
import ai.StateMachine;

/**
 * Manages the players, games and world which comprise an Ant tournament.
 * @author JOH
 * @version 1.1
 */
public class GameManager {

	private boolean debug = false;
	
	// The set of uploaded brains - identified by their filename
	private HashMap<String, StateMachine> playerBrains;
	private HashMap<String, Integer> playerScores;
	
	private ArrayList<Object> matches;

	private World world;
	
	private int roundN; //consecutive number of the current match in a tournament
	
	/**
	 * Constructor.
	 */
	public GameManager() {
		playerBrains = new HashMap<String, StateMachine>();
		playerScores = new HashMap<String, Integer>();
		roundN = 0;
		setWorld(null);
		if (!debug)
		{
			//	Normal start
			new StartUpScreen(this);
		} else {
			//	For debugging - bypass all the choosing worlds & ants UI and just change these three lines:
			setWorld(World.generateMap());
			StateMachine blackBrain = StateMachine.newInstance("milit.ant2");
			StateMachine redBrain = StateMachine.newInstance("milit.ant3");
			world.setBlackBrain(blackBrain.getName(), blackBrain);
			world.setRedBrain(redBrain.getName(), redBrain);
			world.beginGame();
		}
	}
	
	/**
	 * Recursively creates a hierarchy of pairs 
	 * out of a given array list
	 * @param pairs list to combine into pairs
	 * @return multi-nested array list of pairs
	 */
	private ArrayList<Object> combineIntoPairs(ArrayList<Object> pairs){
		//if already only one pair, return as is
		if (pairs.size() == 2){
			//in case it's first recursion, and input is
			//in the form of ["String1", "String2"],
			//get them wrapped in arrays
			if(pairs.get(0).getClass() == "".getClass()){
				ArrayList<Object> arrayedPair = new ArrayList<Object>();
				for (int i=0; i<2; i++){
					ArrayList<Object >singularPair = new ArrayList<Object>();
					singularPair.add(pairs.get(i));
					arrayedPair.add(singularPair);
				}
				return arrayedPair;
			}
			return pairs;
		} else {
			ArrayList<Object> newPairs = new ArrayList<Object>(); //less pairs
			//while at least 2 elements
			while(pairs.size()>1){
				ArrayList<Object >pair = new ArrayList<Object>();
				//pick two elements at random to form a new pair, 
				//remove them from the list of available elements
				for (int i = 0; i<2; i++){
					Object pick = pairs.remove((int)(Math.random()*pairs.size()));
					//if a string, enclose in ArrayList:
					if(pick.getClass() == "".getClass()){
						ArrayList<Object >singularPair = new ArrayList<Object>();
						singularPair.add(pick);
						pair.add(singularPair);
					//else add as is:	
					} else {
						pair.add(pick);
					}
				}	
				//add the new pair
				newPairs.add(pair);
			}
			//if still one element left (odd number of elements),
			//add it as a pair of its own
			if(pairs.size()==1){
				Object lastOne = pairs.get(0);
				//if a string, enclose in ArrayList
				if(lastOne.getClass() == "".getClass()){
					ArrayList<Object >singularPair = new ArrayList<Object>();
					singularPair.add(lastOne);
					newPairs.add(singularPair);
				//else add as is:
				} else {
					newPairs.add(lastOne);
				}
			}
			//recursive call on a new collection of pairs:
			return combineIntoPairs(newPairs);
		}
	}
	
	/**
	 * Works out the correct matching of brains for a tournament to be held.
	 */
	public void assignMatches(){
		ArrayList<Object> players = new ArrayList<Object>();//players to assign
		players.addAll(playerBrains.keySet()); //add all team names
		matches = combineIntoPairs(players);
	}
	
	/**
	 * Simulation of the match: select the winner at random
	 * @param str1 name of first player
	 * @param str2 name of second player
	 * @return the "winner"
	 */
	@SuppressWarnings("unused")
	private Object pretendMatch(String str1, String str2){
		if(Math.random()>0.5){
			System.out.println("==="+str1+" vs. "+str2+ "; winner:" + str1);
			return str1;
		} else {
			System.out.println("==="+str1+" vs. "+str2+ "; winner:" + str2);
			return str2;
		}
	}
	
	/**
	 * Recursively plays out all the matches
	 * @param matchesToPlay matches to complete
	 * @return returns the winner of given matches
	 */
	public Object playMatches(ArrayList<Object> matchesToPlay){
		int i = matches.indexOf(matchesToPlay);
		if(matchesToPlay.size()==1){
			return matchesToPlay;
		} else {
			//break up the pair into parts
			@SuppressWarnings("unchecked")
			ArrayList<Object> part1 = (ArrayList<Object>)matchesToPlay.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<Object> part2 = (ArrayList<Object>)matchesToPlay.get(1);
			
			//if it is the basic pair (i.e. [[player1], [player2]]),
			//play the match between them
			if (part1.get(0).getClass() == "".getClass() &&
					 part2.get(0).getClass() == "".getClass()){
				ArrayList<Object> resultArray = new ArrayList<Object>();

				resultArray.add(tournMatch((String)part1.get(0), 
						(String)part2.get(0)));
				//replace the original pair with the winner
				if(i != -1){
					matches.set(i, resultArray);
				} else if (matches == matchesToPlay){
				 	matches = resultArray;
				}
				return playMatches(resultArray);
			} else {
				//explore one of the branches
				ArrayList<Object> resultArray = new ArrayList<Object>();
				
				//deal with only one branch at a time (hacky?..)
				if(part1.size()>1){
					resultArray.add(playMatches(part1));
					resultArray.add(part2);	
				} else {
					resultArray.add(part1);
					resultArray.add(playMatches(part2));
				}				

				//replace the original array with the reduced one
				if(i != -1){
					matches.set(i, resultArray);
				} else if (matches == matchesToPlay){
					matches = resultArray;
				}
				return playMatches(resultArray);
			}
		}
	}

	/**
	 * Sets the world and brains to play an example singular match
	 * (for debugging/testing)
	 */
	public void playDummyMatch(){	
		setWorld(World.parseWorld("sample0.world"));
		addBrain("crapBrain.txt", StateMachine.newInstance("crapBrain.txt"));
		addBrain("exampleBrain.txt", StateMachine.newInstance("exampleBrain.txt"));
		playSingularMatch("crapBrain.txt", "exampleBrain.txt");
	}
	
	/**
	 * Sets the world and brains to play an example tournament
	 * (for debugging/testing)
	 */
	public void playDummyTourn(){	
		setWorld(World.parseWorld("tinyworld.txt"));
		int numberPlayers = 3;
		for (int i = 1; i <= numberPlayers; i++){
			addBrain("ex"+i, StateMachine.newInstance("exampleBrain.txt"));
		}
		assignMatches();
		
		Object winner = playMatches(matches);

		JOptionPane.showMessageDialog(null, 
				winner + " won the tournament!",
				"And the winner is..", 
				JOptionPane.PLAIN_MESSAGE);
		
		//reset round #
		roundN = 0;
		//reset all brains and scores
		resetBrains();
		//reset the world
		world = null;
	}
	
	
	/**
	 * Organizes and plays a whole tournament
	 */
	public void playTournament(){
		//if there is a world and at least two brains
		if((world != null) && (playerBrains.size() > 1)){
			//pair up the players:
			assignMatches();
			//play all matches:
			@SuppressWarnings("unchecked")
			final String winner = ((ArrayList<String>) playMatches(matches)).get(0);
			//announce the winner:
			Runnable showMessage = new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, 
							winner + " won the tournament!",
							"And the winner is..", 
							JOptionPane.PLAIN_MESSAGE);
				}
			};
			SwingUtilities.invokeLater(showMessage);
			//reset round #
			roundN = 0;
			//reset all brains and scores
			resetBrains();
			//reset the world
			world = null;
		}
	}

	/**
	 * Plays a match as part of a tournament
	 * @param redName one player
	 * @param blackName second player
	 * @return winner
	 */
	public Object tournMatch(String redName, String blackName){
		if((playerBrains.containsKey(redName))&&(playerBrains.containsKey(blackName))){
			
			roundN++;
			
			// show the tournament bracket:
			new BracketScreen(matches, playerBrains.size(), 
							roundN, redName, blackName);

			//while there is no winner, keep playing a pair of games
			while(getScore(redName) == getScore(blackName)){
				
				world.setRedBrain(redName, this.getBrain(redName));
				world.setBlackBrain(blackName, this.getBrain(blackName));
				world.beginGame();
				showResults();
				world.closeScreen();
	
				world.swapBrains();
				world.beginGame();
				showResults();
				world.closeScreen();
			}
			
			//announce the winner
			if(getScore(redName)>getScore(blackName)){
				showMatchWinner(redName);
				
				//reset scores
				playerScores.put(redName, 0);
				playerScores.put(blackName, 0);		
				
				return redName;
			} else {
				showMatchWinner(blackName);
				
				//reset scores
				playerScores.put(redName, 0);
				playerScores.put(blackName, 0);		
				
				return blackName;
			}
		}
		return null;
	}
	
	/**
	 * Shows results of a match.
	 * @param teamName name of a match winner
	 */
	private void showMatchWinner(String teamName) {
		JOptionPane.showMessageDialog(null, 
				"Match winner is " + teamName + "!",
				"Match Results", 
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Plays a singular match (not as part of a tournament)
	 * @param red string-key of the red team brain
	 * @param black string-key of the black team brain
	 */
	public void playSingularMatch(String redName, String blackName){

		if((playerBrains.containsKey(redName))&&(playerBrains.containsKey(blackName))){			
			
			while(getScore(redName) == getScore(blackName)){
				
				world.setRedBrain(redName, this.getBrain(redName));
				world.setBlackBrain(blackName, this.getBrain(blackName));
				world.beginGame();
				showResults();
				world.closeScreen();
	
				world.swapBrains();
				world.beginGame();
				showResults();
				world.closeScreen();
			}
			
			//announce the winner
			if(getScore(redName)>getScore(blackName)){
				showMatchWinner(redName);
			} else {
				showMatchWinner(blackName);
			}

		// singular match - need to reset brains and scores
		this.resetBrains();	
		}	
	}
	
	/**
	 * Shows results of a single game and updates
	 * the scores
	 */
	private void showResults(){
		String winnerMessage;
		if (world.getBlackScore() > world.getRedScore()){
			addScore(world.getBlackName(), 1);
			winnerMessage = world.getBlackName() + " won this game!\n";
		} else if (world.getBlackScore() < world.getRedScore()){
			winnerMessage = world.getRedName() + " won!\n";
			addScore(world.getRedName(), 1);
		} else {
			winnerMessage = "It's a draw!\n";
		}
		JOptionPane.showMessageDialog(null, 
			winnerMessage,
			"Game Results", 
			JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Sets the world's map to an auto-generated map
	 */
	public void setGeneratedWorld(){
		setWorld(World.generateMap());
	}
	
	/**
	 * Adds a new ant brain.
	 * @param name the name of the brain / player or team
	 * @param brain the StateMachine containing the brain
	 * @return true if succeeded, false if a brain with the same name is already loaded.
	 */
	public boolean addBrain(String name, StateMachine brain) {
		if (brain == null)
			return false;
		if (playerBrains.containsKey(name))
			return false;
		playerBrains.put(name, brain);
		playerScores.put(name, 0);
		return true;
	}
	
	/**
	 * Deletes all of the uploaded brains
	 * and their scores.
	 */
	public void resetBrains(){
		playerBrains = new HashMap<String, StateMachine>();
		playerScores = new HashMap<String, Integer>();
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
	 * Adds 1 to a player's score.
	 * @param name the player to score
	 * @param score the score to add
	 */
	public void addScore(String name, int score) {
		if (playerScores.containsKey(name)){
			int oldScore = playerScores.get(name);
			playerScores.put(name, oldScore + score);
		}	
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

	/**
	 * Returns the current world.
	 * @return
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets the current world.
	 * @param world
	 * @return
	 */
	public boolean setWorld(World world) {
		if (world == null)
			return false;
		this.world = world;
		return true;
	}
}
