package ai;

import program.Ant;
import program.Main;
import world.Cell;
import world.Position;
import enums.E_Condition;
import enums.E_Direction;
import enums.E_Instruction;
import enums.E_SenseDirection;

/**
 * Represents a SENSE instruction in the AI state machine. 
 * @author JOH
 * @version 0.1
 */
public class State_Sense extends State_Abstract {
	
	private E_SenseDirection senseDir;			//	The direction to sense in
	private int state1;							//	State to go to if condition is true
	private int state2;							//	State to go to if condition is false
	private E_Condition condition;				//	The condition to evaluate

	/**
	 * Constructor.
	 * @param tokens the tokenized String containing one full instruction
	 */
	public State_Sense(String[] tokens) {
		super(E_Instruction.SENSE);
		if (tokens.length == 5 || tokens.length == 6) {
			//	Variable number of tokens!
			senseDir = tokenToSenseDirection(tokens[1]);
			state1 = tokenToState(tokens[2]);
			state2 = tokenToState(tokens[3]);
			condition = tokenToCondition(tokens[4]);
			if (tokens.length == 6) {
				//	Marker numbers - have added these to condition enums which seems to mark sense
				switch (tokens[5])
				{
				case "0": condition = E_Condition.MARKER0; break;
				case "1": condition = E_Condition.MARKER1; break;
				case "2": condition = E_Condition.MARKER2; break;
				case "3": condition = E_Condition.MARKER3; break;
				case "4": condition = E_Condition.MARKER4; break;
				case "5": condition = E_Condition.MARKER5; break;
				default: Main.error("Unknown marker number: " + tokens[5]);
				}
			}
		} else {
			//	Throw an error.
			checkCorrectNumberOfTokens(5, tokens.length);
		}
		
	}

	/**
	 * Goto state1 if condition holds in senseDir; goto state2 otherwise.
	 */
	@Override
	public void step(Ant ant, Cell cell) {
		Position toCheck;	
		int i;
		//	First get a reference to the cell position we need to check
		switch (senseDir)
		{
			case HERE: 
				toCheck = cell.getPosition();
				break;
			case AHEAD:
				toCheck = cell.getWorld().adjacentCell(cell.getPosition(), ant.getDirection()).getPosition();
				break;
			case LEFTAHEAD:
				//	This is all a bit cheaty?
				i = ant.getDirection().ordinal() - 1;
				if (i < 0)
					i = 5;
				toCheck = cell.getWorld().adjacentCell(cell.getPosition(), E_Direction.class.getEnumConstants()[i]).getPosition();
				break;
			case RIGHTAHEAD:
				//	This is all a bit cheaty too?
				i = ant.getDirection().ordinal() + 1;
				if (i > 5)
					i = 0;
				toCheck = cell.getWorld().adjacentCell(cell.getPosition(), E_Direction.class.getEnumConstants()[i]).getPosition();
				break;			
			default:
				//	Shouldn't get here!
				toCheck = null;
				Main.error("Unknown sense direction!");
				break;
		}
		
		//	Check if the position to check matches the condition
		if (cell.getWorld().cellMatches(toCheck, condition, ant.getColor()))
		{
			ant.setCurrentState(state1);
		} else {
			ant.setCurrentState(state2);
		}
	}
}
