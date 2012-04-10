package ai;

import program.Ant;
import program.Main;
import world.Cell;
import enums.E_Condition;
import enums.E_Instruction;
import enums.E_LeftOrRight;
import enums.E_SenseDirection;

/**
 * Represents a single state in an Ant-Brain StateMachine.
 * This class should be extended with a concrete implementation.
 * @author JOH
 * @version 0.2
 */

public abstract class State_Abstract {

	private E_Instruction instruction;
	
	/**
	 * Constructor.
	 * @param instruction this state's instruction
	 */
	public State_Abstract(E_Instruction instruction) {
		this.instruction = instruction;
	}
	
	/**
	 * Returns the instruction stored at this state.
	 * @return the instruction
	 */
	public E_Instruction getInstruction() {
		return instruction;
	}
	
	/**
	 * Implement this method in each inheriting state to run state logic.
	 */
	public abstract void step(Ant ant, Cell cell);
	{

	}
	
	//
	//	Helper methods ::-
	//
	
	/**
	 * Checks we have the right number of tokens for a given instruction + arguments.
	 * Helper method used in constructing concrete types.
	 * @param correctNum
	 * @param actualNum
	 */
	protected void checkCorrectNumberOfTokens(int correctNum, int actualNum) {
		if (correctNum != actualNum) Main.error("Instruction with wrong number of arguments.");
	}

	/**
	 * Validates integer tokens.
	 * Helper method used in constructing concrete types.
	 * @param token the String token to check
	 * @return the integer value of the token
	 */
	protected int tokenToInt(String token) {
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			Main.error("Error: instruction with non-integer argument: " + token);
		}
		//	Shouldn't get here
		return -1;
	}
	
	/**
	 * Validates a state index.
	 * Note we only calculate if a state index is valid, not that it necessarily exists.
	 * Helper method used in constructing concrete types.
	 */
	protected int tokenToState(String token) {
		if (tokenToInt(token) < 0 || tokenToInt(token) > 9999) {
			Main.error("Error: state out of range : " + token);
		}
		return tokenToInt(token);
	}
	
	/**
	 * Validates a marker.
	 * Helper method used in constructing concrete types.
	 */
	protected int tokenToMarker(String token) {
		if (tokenToInt(token) < 0 || tokenToInt(token) > 5 ) {
			Main.error("Error: marker number out of range: " + token);
		}
		return tokenToInt(token);
	}
	
	/**
	 * Validates a sense direction. 
	 * Helper method used in constructing concrete types.
	 */
	protected E_SenseDirection tokenToSenseDirection(String token) {
		if (!E_SenseDirection.valid().contains(E_SenseDirection.valueOf(token))) {
			Main.error("Error : unknown argument for senseDir " + token); 
		}
		return E_SenseDirection.valueOf(token);
	}
	
	/**
	 * Validates a condition.
	 * Helper method used in constructing concrete types.
	 */
	protected E_Condition tokenToCondition(String token) {
		if (!E_Condition.valid().contains(E_Condition.valueOf(token))) {
			Main.error("Error : unknown argument for condition " + token); 
		}
		return E_Condition.valueOf(token);
	}
	
	/**
	 * Validates a Left or Right turn.
	 * Helper method used in constructing concrete types.
	 */
	protected E_LeftOrRight tokenToLeftOrRight(String token) {
		if (!E_LeftOrRight.valid().contains(E_LeftOrRight.valueOf(token))) {
			Main.error("Error : unknown argument for left_or_right " + token); 
		}
		return E_LeftOrRight.valueOf(token);
	}
}
