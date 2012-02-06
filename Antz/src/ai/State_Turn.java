package ai;

import program.Ant;
import enums.E_Instruction;
import enums.E_LeftOrRight;

/**
 * Represents a TURN instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Turn extends State_Abstract {

	private E_LeftOrRight leftOrRight;		//	Direction to turn in
	private int state1;							//	State to go to
	
	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Turn(String[] tokens) {
		super(E_Instruction.TURN);
		checkCorrectNumberOfTokens(3, tokens.length);
		leftOrRight = tokenToLeftOrRight(tokens[1]);
		state1 = tokenToState(tokens[2]);
	}

	/**
	 * Turn left or right and goto state1.
	 */
	@Override
	public void step(Ant ant) {
		// TODO Auto-generated method stub

	}

}
