package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import program.GameManager;

/**
 * The start up screen of the game.
 * @author kris
 * @version 1
 */
@SuppressWarnings("serial")
public class StartUpScreen extends JFrame {

	private static ImageIcon titleImage;
	private GameManager gm;
	
	/**
	 * Constructor
	 */
	public StartUpScreen(GameManager gm){
		super("Ants");
		this.gm = gm;
		
		// load title image
		try {
			titleImage = new ImageIcon("Images/dummy_title.gif");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't load image.");
		}
		
		Container pane = this.getContentPane();
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(titleImage.getIconWidth(), titleImage.getIconHeight()+70));
		panel.add(new JLabel(titleImage), BorderLayout.NORTH);
		//buttons
		JButton matchButton = new JButton(new AbstractAction("Standard Match"){
			public void actionPerformed(ActionEvent e){
				startMatch();
			}
		});
		panel.add(matchButton, BorderLayout.WEST);
		JButton tournamentButton = new JButton(new AbstractAction("Tournament Mode"){
			public void actionPerformed(ActionEvent e) {
				startTournament();
			}
			
		});
		panel.add(tournamentButton, BorderLayout.EAST);
		JButton creditsButton = new JButton(new AbstractAction("Credits"){
			public void actionPerformed(ActionEvent e){
				viewCredits();
			}
		});
		panel.add(creditsButton, BorderLayout.SOUTH);
		
		
		pane.add(panel);
		
	    //final initialization
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	    this.pack();
	    this.setLocationRelativeTo(null);
	    this.setVisible(true);  
		
	}

	/**
	 * Starts the tournament.
	 */
	private void startTournament() {
		System.out.println("startTournament");
		new TournamentScreen(gm);
	}

	/**
	 * Starts a match.
	 */
	private void startMatch() {
		System.out.println("startMatch");
		new MatchScreen(gm);
		
	}

	/**
	 * Views the game credits.
	 */
	private void viewCredits() {
		System.out.println("credits");
		new CreditsScreen();
	}
}
