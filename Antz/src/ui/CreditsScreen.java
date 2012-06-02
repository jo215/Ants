package ui;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Credits screen.
 * @author kris
 * @version 1
 */
@SuppressWarnings("serial")
public class CreditsScreen extends JFrame {
	
	/**
	 * Constructor.
	 */
	public CreditsScreen(){
		Container pane = this.getContentPane();
		pane.add(new JLabel("Credits go here", JLabel.CENTER));
		pane.setPreferredSize(new Dimension(100, 100));
		
	    //  final initialization
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	    this.pack();
	    this.setLocationRelativeTo(null);	
	    this.setVisible(true);  
	}
}
