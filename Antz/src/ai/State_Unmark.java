package ai;

import program.Ant;
import world.Cell;
import enums.E_Instruction;

/**
 * Represents an UNMARK instruction in the AI state machine.
 * @author JOH
 * @version 1
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
	 * Clear marker in current cell and goto state1.
	 */
	@Override
	public void step(Ant ant, Cell cell) {
		if (cell.checkMarker(ant.getColor(), marker)) {
			cell.clearMarker(ant.getColor(), marker);
		}
		ant.setCurrentState(state1);
	}
}
