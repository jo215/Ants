package ai;

import enums.E_Condition;
import enums.E_Instruction;
import enums.E_SenseDirection;

/**
 * Represents a SENSE instruction in the AI state machine. 
 * @author JOH
 *
 */
public class State_Sense extends State_Abstract {
	
	private E_SenseDirection senseDir;		//	The direction to sense in
	private int state1;							//	State to go to if condition is true
	private int state2;							//	State to go to if condition is false
	private E_Condition condition;			//	The condition to evaluate

	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Sense(String[] tokens) {
		super(E_Instruction.SENSE);
		checkCorrectNumberOfTokens(5, tokens.length);
		senseDir = tokenToSenseDirection(tokens[1]);
		state1 = tokenToState(tokens[2]);
		state2 = tokenToState(tokens[3]);
		condition = tokenToCondition(tokens[4]);
	}

	/**
	 * Goto state1 if condition holds in senseDir; goto state2 otherwise.
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}
}
