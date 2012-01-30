package ai;

import enums.E_Instruction;

/**
 * Represents a FLIP instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Flip extends State_Abstract {

	private int p;							//	Choose random number x between 0 and p-1
	private int state1;						//	Go to state1 if x == 0
	private int state2;						//	Go to state2 if x != 0
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Flip(String[] tokens) {
		super(E_Instruction.FLIP);
		checkCorrectNumberOfTokens(4, tokens.length);
		p = tokenToInt(tokens[1]);
		state1 = tokenToState(tokens[2]);
		state2 = tokenToState(tokens[3]);
	}

	/**
	 * Choose a random number x from 0 to p-1; goto state1 if x=0 and state2 otherwise.
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

}
