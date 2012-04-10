package ai;

import program.Ant;
import world.Cell;
import enums.E_Instruction;

/**
 * Represents a PICKUP instruction in the AI state machine.
 * @author JOH
 *
 */
public class State_Pickup extends State_Abstract {
	
	private int state1;							//	State to go to on successful pickup
	private int state2;							//	State to go to if no food in cell

	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Pickup(String[] tokens) {
		super(E_Instruction.PICKUP);
		checkCorrectNumberOfTokens(3, tokens.length);
		state1 = tokenToState(tokens[1]);
		state2 = tokenToState(tokens[2]);
	}

	/**
	 * Pickup food from current cell and goto state1; goto state2 if there is no food in the current cell.
	 */
	@Override
	public void step(Ant ant, Cell cell) {
		if (cell.getFoodAmount() > 0 &! ant.hasFood())
		{
			//	This cell has food and the ant is not currently carrying any
			ant.setHasFood(true);
			cell.setFoodAmount(cell.getFoodAmount() - 1);
			ant.setCurrentState(state1);
		} else {
			//	Either this cell has no food or ant is already carrying some
			ant.setCurrentState(state2);
		}
	}
}
