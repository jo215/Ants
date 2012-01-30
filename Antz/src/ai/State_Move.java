package ai;

import enums.E_Instruction;

/**
 * Represents a MOVE instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Move extends State_Abstract {

	private int state1;							//	State to go to if move forward OK
	private int state2;							//	State to go to if move forward blocked
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Move(String[] tokens) {
		super(E_Instruction.MOVE);
		super.checkCorrectNumberOfTokens(3, tokens.length);
		state1 = super.tokenToState(tokens[1]);
		state2 = super.tokenToState(tokens[2]);
	}

	/**
	 * Move forward and goto state1; goto state2 if the cell ahead is blocked.
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

}
