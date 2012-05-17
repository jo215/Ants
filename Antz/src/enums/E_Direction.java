package enums;

import java.util.EnumSet;

/**
 * Enumerates allowable directions on the map.
 * @author JOH
 * @version 1
 *
 */
public enum E_Direction {
	
	EAST,
	SOUTHEAST,
	SOUTHWEST,
	WEST,
	NORTHWEST,
	NORTHEAST;
	
	/**
	 * Returns an EnumSet of all valid enums.
	 * @return the EnumSet
	 */
	public static EnumSet<E_Direction> valid() {
		return EnumSet.allOf(E_Direction.class);
	}
}
