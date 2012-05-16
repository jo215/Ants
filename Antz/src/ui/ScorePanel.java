package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import world.World;

public class ScorePanel extends JPanel {


	protected JLabel redScore, blackScore;
	protected JPanel scoreRatio;
	
	/**
	 * Constructor.
	 * @param screen
	 * @param world
	 */
	public ScorePanel(World world) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		///	Top panel is the player names and scores display
		JPanel topPanel = new JPanel();
		add(topPanel);
		
		//	outer Red panel
		JPanel redPanel = new JPanel(new GridLayout(1, 2));
		redPanel.setPreferredSize(new Dimension(400, 30));
		topPanel.add(redPanel);
		//	Red name label
		ImageIcon red = new ImageIcon("images/redant_E_tiny.gif");
		JLabel redBrain = new JLabel(world.getRedBrain().getName(), red, JLabel.TRAILING);
		redBrain.setForeground(Color.RED);
		redBrain.setHorizontalAlignment(SwingConstants.CENTER);
		redBrain.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		redPanel.add(redBrain);
		//	Red score label
		redScore = new JLabel("Score: " + world.getRedScore());
		redScore.setForeground(Color.RED);
		redScore.setHorizontalAlignment(SwingConstants.CENTER);
		redScore.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		redPanel.add(redScore);
		
		//	Spacer
		topPanel.add(new JPanel());
		
		//	outer Black panel
		JPanel blackPanel = new JPanel(new GridLayout(1, 2));
		blackPanel.setPreferredSize(new Dimension(400, 30));
		topPanel.add(blackPanel);
		//	Black name label
		ImageIcon black = new ImageIcon("images/blackant_E_tiny.gif");
		JLabel blackBrain = new JLabel(world.getBlackBrain().getName(), black, JLabel.TRAILING);
		blackBrain.setForeground(Color.black);
		blackBrain.setHorizontalAlignment(SwingConstants.CENTER);
		blackBrain.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		blackPanel.add(blackBrain);
		//	black score label
		blackScore = new JLabel("Score: " + world.getBlackScore());
		blackScore.setForeground(Color.black);
		blackScore.setHorizontalAlignment(SwingConstants.CENTER);
		blackScore.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		blackPanel.add(blackScore);
		
		//	Bottom panel is score ratio bar
		JPanel bottomPanel = new JPanel();
		add(bottomPanel);
		scoreRatio = new ScoreRatioPanel(world);
		bottomPanel.add(scoreRatio);
	}
}
