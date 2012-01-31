package ai;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import program.Main;

import enums.E_Instruction;

/**
 * Represents the StateMachine for Ant Brains.
 * @author JOH
 * @version 1
 *
 */
public class StateMachine {

	private ArrayList<State_Abstract> states;			//	The list of states (max 10000)
	
	/**
	 * Private Constructor. Sets up an empty state machine.
	 */
	private StateMachine() {
	}
	
	/**
	 * Factory method. Parses an ant brain text file.
	 * Using a Factory we always make sure the StateMachine is set up correctly.
	 * @param string the file to parse
	 */
	public static StateMachine newInstance(String fileName) {
		StateMachine sm = new StateMachine();
		//	Open the file stream
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			//	Read each line of the file
			String line = br.readLine();
			int currentState = 0;
			while(line != null) {
				//	Strip out any comments
				int i = line.indexOf(";");
				if (i != -1) {
					line = line.substring(0, i);
				}
				System.out.print("Reading state " + currentState + ": ");
				//	Split a single line of text into tokens separated by SPACE or TAB
				String[] tokens = line.toUpperCase().split(" |\t");
				//	Send these to method to parse and add the state object
				sm.addState(tokens);
				System.out.println("OK");
				//	Get next line
				line = br.readLine();
				currentState ++;
			}
		} catch (Exception e) {
			System.out.println("File I/O error with Ant Brain: " + fileName);
			e.printStackTrace();
			System.exit(0);
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
	 * Creates a concrete AIState object from a tokenized String input array and adds it to the end of the state machine instruction list.
	 * @param tokens the array of String tokens
	 * @return the concrete AIState
	 */
	public State_Abstract addState(String[] tokens) {
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
}
