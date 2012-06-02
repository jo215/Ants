package ai;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import program.Ant;
import program.Main;
import world.Cell;

import enums.E_Instruction;

/**
 * Represents the StateMachine for Ant Brains.
 * @author JOH
 * @version 1
 */
public class StateMachine {

	private ArrayList<State_Abstract> states;			//	The list of states (max 10000)
	private String name;								//	The filename
	
	/**
	 * Private Constructor. Sets up an empty state machine.
	 */
	private StateMachine() {
		states = new ArrayList<>();
	}
	
	/**
	 * Given an ant, gets and executes the next instruction. 
	 * @param ant
	 */
	public void step(Ant ant, Cell cell) {
		states.get(ant.getCurrentState()).step(ant, cell);
	}
	
	/**
	 * Factory method. Parses an ant brain text file.
	 * Using a Factory we always make sure the StateMachine is set up correctly, else return null;
	 * @param string the file to parse
	 */
	public static StateMachine newInstance(String fileName) {
		StateMachine sm = new StateMachine();
		sm.setName(fileName);
		@SuppressWarnings("unused")
		int currentState = 0;
		//	Open the file stream
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			//	Read each line of the file
			String line = br.readLine();
			while(line != null) {
				if (!line.startsWith("#") && !line.equals("")) {
					line.trim();
					//	Strip out any end-of-line comments
					int i = line.indexOf(";");
					if (i != -1) {
						line = line.substring(0, i);
					}
					//	Split a single line of text into tokens separated by SPACE or TAB
					String[] tokens = line.toUpperCase().split(" |\t");
					//	Send these to method to parse and add the state object
					sm.states.add(sm.createState(tokens));
					currentState ++;
					//	Get next line
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			//	Problem reading file - handle elsewhere
			return null;
		}
		//	StateMachine is fully populated & validated
		return sm;
	}

	/**
	 * Returns the entire list of states for this machine.
	 * @return
	 */
	public ArrayList<State_Abstract> getStates() {
		return states;
	}
	
	/**
	 * Returns a single state.
	 * @param state the index of the state to return
	 * @return
	 */
	public State_Abstract getState(int state) {
		return states.get(state);
	}

	/**
	 * Creates a concrete AIState object from a tokenized String input array.
	 * @param tokens the array of String tokens
	 * @return the concrete AIState
	 */
	private State_Abstract createState(String[] tokens) {
		//	tokens[0] is the instruction for this state
		if (!E_Instruction.valid().contains(E_Instruction.valueOf(tokens[0]))) {
			Main.error("Error : unknown instruction: " + tokens[0]); 
		}
		//	Based on this we create the correct state
		switch (E_Instruction.valueOf(tokens[0])) {
			case SENSE:
				return new State_Sense(tokens);
			case MARK:
				return new State_Mark(tokens);
			case UNMARK:
				return new State_Unmark(tokens);
			case PICKUP:
				return new State_Pickup(tokens);
			case DROP:
				return new State_Drop(tokens);
			case TURN:
				return new State_Turn(tokens);
			case MOVE:
				return new State_Move(tokens);
			case FLIP:
				return new State_Flip(tokens);	
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
