package ai;

import enums.E_Instruction;

/**
 * Represents an UNMARK instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Unmark extends State_Abstract {

	private int marker;					//	The chemical marker to remove (0-5)
	private int state1;					//	The state to go to
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Unmark(String[] tokens) {
		super(E_Instruction.UNMARK);
		checkCorrectNumberOfTokens(3, tokens.length);
		marker = tokenToMarker(tokens[1]);
		state1 = tokenToState(tokens[2]);
	}

	/**
	 * Clear mark marker in current cell and goto state1.
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

}
