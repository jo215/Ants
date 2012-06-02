package enums;

import java.util.EnumSet;

/**
 * Enumerates #sense_dir# for the Sense instruction of the Ant-Brain state machine.
 * @author JOH
 * @version 1
 */
public enum E_SenseDirection {

	HERE,
	AHEAD,
	LEFTAHEAD,
	RIGHTAHEAD;
	
	/**
	 * Returns an EnumSet of all valid enums.
	 * @return the EnumSet
	 */
	public static EnumSet<E_SenseDirection> valid() {
		return EnumSet.allOf(E_SenseDirection.class);
	}
	
}
