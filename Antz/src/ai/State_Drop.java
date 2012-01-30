package ai;

import enums.E_Instruction;

/**
 * Represents a DROP instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Drop extends State_Abstract {

	private int state1;							//	State to goto once food dropped
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Drop(String[] tokens) {
		super(E_Instruction.DROP);
		checkCorrectNumberOfTokens(2, tokens.length);
		state1 = tokenToState(tokens[1]);
	}

	/**
	 * Drop food in current cell and goto state1;
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

}
