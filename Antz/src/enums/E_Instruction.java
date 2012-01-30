package enums;

import java.util.EnumSet;

/**
 * Enumerates the valid instructions for the Ant-Brain state machine.
 * @author JOH
 *
 */
public enum E_Instruction {

	SENSE,
	MARK,
	UNMARK,
	PICKUP,
	DROP,
	TURN,
	MOVE,
	FLIP;
	
	/**
	 * Returns an EnumSet of all valid enums.
	 * @return the EnumSet
	 */
	public static EnumSet<E_Instruction> valid() {
		return EnumSet.allOf(E_Instruction.class);
	}
}
