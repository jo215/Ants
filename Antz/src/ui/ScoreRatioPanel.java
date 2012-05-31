package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import world.World;

/**
 * Score ratio sub panel.
 * @author JOH
 * @version 1
 */
public class ScoreRatioPanel extends JPanel {

	private World world;

	/**
	 * Constructor.
	 */
	public ScoreRatioPanel(World world) {
		super();
		this.world = world;
		setPreferredSize(new Dimension(800, 20));
	}
	
	/**
	 * Redraws the ratio bar.
	 */
	public void paintComponent(Graphics g)
	{
		//	Cast as G2D
		Graphics2D g2d = (Graphics2D) g;
		
		//	clear screen
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//	Work out the ratio
		int foodCollected = world.getBlackScore() + world.getRedScore();
		if (foodCollected == 0)
			return;
		int ratio = (int)(((float)world.getRedScore() / (float)foodCollected) * 80);
		g2d.setColor(Color.RED);
		g2d.fillRect(0, 0, ratio * 8, this.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.fillRect(ratio * 8 + 1, 0, this.getWidth(), this.getHeight());
	}
}
