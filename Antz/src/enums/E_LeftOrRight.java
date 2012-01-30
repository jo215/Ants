package enums;

import java.util.EnumSet;

/**
 * Enumerates #left_or_right# for the Turn instruction in the Ant-Brain state machine.
 * @author JOH
 *
 */
public enum E_LeftOrRight {

	LEFT,
	RIGHT;
	
	/**
	 * Returns an EnumSet of all valid enums.
	 * @return the EnumSet
	 */
	public static EnumSet<E_LeftOrRight> valid() {
		return EnumSet.allOf(E_LeftOrRight.class);
	}
}
