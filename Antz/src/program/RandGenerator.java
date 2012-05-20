package program;

/**
 * Implements a pseudo-RNG to customer specification.
 * @author JOH
 * @version 0.2
 *
 */
public class RandGenerator {

	private static long seed = 0;		//	The sequence of random number seeds
	
	/**
	 * Constructor.
	 * 
	 * @param newSeed the initial seed for the RNG
	 */
	public static void setSeed(long newSeed)
	{
		RandGenerator.seed = newSeed;
		//	Add the first three values
		for (int i = 1; i < 4; i++)
		{
			RandGenerator.seed = RandGenerator.seed * 22695477 + 1;
		}
	}
	
	/**
	 * Gets the value of the seed.
	 * @return
	 */
	public static long getSeed()
	{
		return seed;
	}
	
	/**
	 * Returns a new pseudo-random integer between 0 and n-1 inclusive.
	 * @param n the range of the number to generate
	 * @return the random number
	 */
	public static int randomInt(int n)
	{
		seed = seed * 22695477 + 1;

		long x = seed / 65536 % 16384;

		//	Account for Java modulus behavior 
		if (x % n >= 0)
		{
			return (int)x % n;
		}
		return (int) (x % n) + (n-1);
	}
}
