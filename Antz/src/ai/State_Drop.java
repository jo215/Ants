package ai;

import program.Ant;
import world.Cell;
import enums.E_Instruction;

/**
 * Represents a DROP instruction in the AI state machine.
 * @author JOH
 * @version 1
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
	public void step(Ant ant, Cell cell) {
		//	If ant has food, drop it and and to this cell's food amount.
		if (ant.hasFood()) {
			ant.setHasFood(false);
			cell.setFoodAmount(cell.getFoodAmount() + 1);
		}
		//	Goto state1
		ant.setCurrentState(state1);
	}
}
