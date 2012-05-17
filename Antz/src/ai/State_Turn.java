package ai;

import program.Ant;
import program.Main;
import world.Cell;
import enums.E_Direction;
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
	public void step(Ant ant, Cell cell) {
		int i;
		switch (leftOrRight) {
			case LEFT:
				i = ant.getDirection().ordinal() - 1;
				if (i < 0)
					i = 5;
				ant.setDirection(E_Direction.class.getEnumConstants()[i]);
				break;
			case RIGHT:
				i = ant.getDirection().ordinal() + 1;
				if (i > 5)
					i = 0;
				ant.setDirection(E_Direction.class.getEnumConstants()[i]);
				break;			
			default:
				//	Shouldn't get here!
				Main.error("Unknown sense direction!");
				break;
		}
		ant.setCurrentState(state1);
	}

}
