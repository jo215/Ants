package program;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
		setNimbusLookAndFeel();
		//	Remember to set the seed
		RandGenerator.setSeed(12345);
		GameManager gm = new GameManager();
		
		//for (int i =0; i <100; i++)
			//System.out.println("" + RandGenerator.randomInt(16384));
	}

	/**
	 * If possible, sets the Nimbus L&F.
	 */
	private static void setNimbusLookAndFeel() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
			//	We'll just go with default
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
