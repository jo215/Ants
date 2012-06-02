package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 * Displays the current state of the tournament
 * @author kris
 * @version 1
 */
@SuppressWarnings("serial")
public class TournamentBracket extends JLabel{
	
	//dimensions of the basic bracket
	private final int BRACKET_W = 200;
	private final int BRACKET_H = 80;
	
	private final int TEXT_SIZE = 15;
	private Font font = new Font("Tahoma", Font.BOLD, TEXT_SIZE);
	
	private int nPlayers;
	private int nRows;
	private ArrayList<Object> matches;
	
	/**
	 * Constructor
	 * @param matches hierarchy of players to play against each other
	 * @param nPlayers total number of players
	 */
	public TournamentBracket(ArrayList<Object> matches, int nPlayers){
		this.matches = matches;
		this.nPlayers = nPlayers;
		this.nRows = calculateRows();
		setPreferredSize(new Dimension(
				(int)(2*Math.pow(2,nRows-1)*BRACKET_W),
				(nRows+1)*BRACKET_H+TEXT_SIZE+5));
	}
	
	/**
	 * Determine the number of horizontal rows in a bracket
	 * @return number of rows
	 */
	private int calculateRows(){
		int rows = 0;
		while(Math.pow(2,rows) < nPlayers){
			rows++;
		}
		return rows;
	}
	
	/**
	 * Recursively draws brackets of the tournament
	 * @param g2d 2D graphics
	 * @param pairs brackets to draw
	 * @param origin point to start drawing the bracket
	 * @param expWidth exponent of the width of the horizontal line
	 */
	private void drawBracket(Graphics2D g2d, 
			Object pairs, Point2D.Float origin, int expWidth){
		
		//should always be an array
		@SuppressWarnings("unchecked")
		ArrayList<Object> arrayPairs = (ArrayList<Object>) pairs;
		
		//end of the vertical line
		Point2D.Float endPoint = new Point2D.Float(origin.x, origin.y + BRACKET_H);
		//draw vertical line
		g2d.draw(new Line2D.Float(origin, endPoint));
		
		//if only one element, must be in the form of ["String]
		if(arrayPairs.size()==1){
			g2d.setColor(Color.YELLOW);
			g2d.drawString((String)arrayPairs.get(0), endPoint.x-10, endPoint.y+15);
			g2d.setColor(Color.BLACK);
			
		//otherwise, it's a pair	
		} else{
			int width = (int) (BRACKET_W*Math.pow(2, expWidth));
			g2d.draw(new Line2D.Float(endPoint.x - width/2, endPoint.y,
					endPoint.x + width/2, endPoint.y));
			//draw left bracket
			drawBracket(g2d, arrayPairs.get(0), 
					new Point2D.Float(endPoint.x - width/2, endPoint.y),
					expWidth-1);
			//draw right bracket
			drawBracket(g2d, arrayPairs.get(1), 
					new Point2D.Float(endPoint.x + width/2, endPoint.y),
					expWidth-1);
			
		}
	}
	
	
	public void update(ArrayList<Object> newMatches){
		matches = newMatches;
		this.repaint();
	}
	
	/**
	 * Does the drawing
	 */
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		//green background
		g2d.setColor(new Color(0.0f, 0.7f, 0.1f));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		drawBracket(g2d, matches, new Point2D.Float(getWidth()/2, 0), nRows-1);
	}

}
