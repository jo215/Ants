package ai;

import program.Ant;
import enums.E_Instruction;

/**
 * Represents a MARK instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Mark extends State_Abstract {

	private int marker;					//	The chemical marker to place (0-5)
	private int state1;					//	The state to go to
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Mark(String[] tokens) {
		super(E_Instruction.MARK);
		checkCorrectNumberOfTokens(3, tokens.length);
		marker = tokenToMarker(tokens[1]);
		state1 = tokenToState(tokens[2]);
	}

	/**
	 * Set mark marker in current cell and goto state1
	 */
	@Override
	public void step(Ant ant) {
		// TODO Auto-generated method stub

	}

}
