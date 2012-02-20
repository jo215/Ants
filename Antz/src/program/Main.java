package program;

import java.util.ArrayList;

import ai.StateMachine;

/**
 * Test driver. 
 * @author JOH
 * @version 1
 */
public class Main {
	
	
	/**
	 * Main method just to get things up and running.
	 * @param args
	 */
	public static void main(String[] args)
	{
		StateMachine.newInstance("exampleBrain.txt");
		
		RandGenerator rng = new RandGenerator(12345);
		for (int i = 0; i < 100; i++)
		{
			System.out.println(" " + rng.randomInt(16384));
		}
	}

	
	/**
	 * Prints an error message and exits the program.
	 * @param message the message to print
	 */
	public static void error(String message) {
		System.out.println(message);
		System.exit(0);
	}
}
