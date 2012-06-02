package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A window to displays the bracket screen to show the state of 
 * the tournament.
 * @author kris
 * @version 1
 */
@SuppressWarnings("serial")
public class BracketScreen extends JDialog {

	private TournamentBracket tournamentBracket;

	private Font font = new Font("Tahoma", Font.BOLD, 30);
	
	/**
	 * Constructor.
	 * @param matches the list of matches
	 * @param nPlayers the number of players
	 * @param round the current round
	 * @param player1 player1's name
	 * @param player2 player2's name
	 */
	public BracketScreen(ArrayList<Object> matches, int nPlayers, int round, String player1, String player2){
		super();
		this.setModal(true); //to wait until the user presses begin

		tournamentBracket = new TournamentBracket(matches, nPlayers);
		
		//arrange components
		Container pane = this.getContentPane();
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		
		//text part
		JPanel textPanel = new JPanel(new GridLayout(2,1));
		JLabel roundLabel = new JLabel("Round " + round, JLabel.CENTER);
		roundLabel.setFont(font);
		textPanel.add(roundLabel);
		// P1 VS P2 part
		JLabel playersLabel = new JLabel(player1 + " VS " + player2, JLabel.CENTER);
		playersLabel.setFont(font);
		textPanel.add(playersLabel);
		mainPanel.add(textPanel, BorderLayout.NORTH);
		
		// Bracket part
		JScrollPane bracketScroll = new JScrollPane(tournamentBracket, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bracketScroll.setPreferredSize(new Dimension(800, 500));
		mainPanel.add(bracketScroll, BorderLayout.CENTER);
		
		// Begin button

		JButton beginButton = new JButton(new AbstractAction("Begin"){
			public void actionPerformed(ActionEvent e) {
				dispose(); //just need to close the window
			}
			
		});

		beginButton.setPreferredSize(new Dimension(150, 40));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(beginButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
				
		pane.add(mainPanel);
		
		//this.setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//for now only
	    this.pack();
	    this.setLocationRelativeTo(null);	
	    this.setVisible(true); 
	}


}

