package enums;

import java.util.EnumSet;

/**
 * Enumerates allowable Ant colors.
 * @author JOH
 * @version 1
 *
 */
public enum E_Color {

	RED, BLACK;
	
	/**
	 * Returns an EnumSet of all valid enums.
	 * @return the EnumSet
	 */
	public static EnumSet<E_Color> valid() {
		return EnumSet.allOf(E_Color.class);
	}
	
	/**
	 * Returns the other color.
	 */
	public E_Color otherColor() {
		if (this == RED) {
			return BLACK;
		}
		return RED;
	}
}
