package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import world.Position;
import world.World;
import enums.E_Terrain;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents the map panel within the GameplayScreen.
 * @author JOH
 * @version 0.1
 */
public class MapPanel extends JLabel{
	
	private GameplayScreen screen;
	private World world;
	private int zoomLevel;
	
	private static BufferedImage rocky_big, clear_big, anthill_big,
						rocky_small, clear_small, anthill_small,
						rocky_tiny, clear_tiny, anthill_tiny;
	
	public static final int imageWidth = 72;
	public static final int imageHeight = 84;
	
	public static final Font bigFont = new Font("Tahoma", Font.BOLD, 40);
	public static final Font smallFont = new Font("Tahoma", Font.PLAIN, 20);
	public static final Font tinyFont = new Font("Tahoma", Font.PLAIN, 10);

	/**
	 * Constructor
	 */
	public MapPanel(GameplayScreen screen, World world)
	{
		this.screen = screen;
		this.world = world;
		zoomLevel = 1;
		this.setPreferredSize(new Dimension(world.getWidth() * imageWidth , world.getHeight() * (imageHeight-20)));
		
		/**
		 * Drag map functionality
		 */
		MapDragListener dragListener = new MapDragListener();
		addMouseMotionListener(dragListener);
		addMouseListener(dragListener);
		
		//	Load images
		try {
			rocky_big = ImageIO.read(new File("Images/rocky_big.gif"));
			clear_big = ImageIO.read(new File("Images/clear_big.gif"));
			anthill_big = ImageIO.read(new File("Images/anthill_big.gif"));
			rocky_small = ImageIO.read(new File("Images/rocky_small.gif"));
			clear_small = ImageIO.read(new File("Images/clear_small.gif"));
			anthill_small = ImageIO.read(new File("Images/anthill_small.gif"));
			rocky_tiny = ImageIO.read(new File("Images/rocky_tiny.gif"));
			clear_tiny = ImageIO.read(new File("Images/clear_tiny.gif"));
			anthill_tiny = ImageIO.read(new File("Images/anthill_tiny.gif"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load image.");
		}
	}

	/**
	 * Draws the map.
	 */
	public void paintComponent(Graphics g)
	{
		//	Cast as G2D
		Graphics2D g2d = (Graphics2D) g;
		
		//	clear screen
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());	
		g2d.setColor(Color.YELLOW);
		//	Choose image sizes based on zoom level
		BufferedImage rocky = null, clear = null, anthill = null;
		switch (zoomLevel) {
		case 1: 
			rocky = rocky_big;
			clear = clear_big;
			anthill = anthill_big;
			g2d.setFont(bigFont);
			break;
		case 2:
			rocky = rocky_small;
			clear = clear_small;
			anthill = anthill_small;
			g2d.setFont(smallFont);
			break;
		case 4:
			rocky = rocky_tiny;
			clear = clear_tiny;
			anthill = anthill_tiny;
			g2d.setFont(tinyFont);
			break;
		}
		
		//	Draw the map
		for (int x = 0; x < world.getWidth() ; x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				//	The current cell position
				Position pos = new Position(x, y);
				//	Each odd-numbered row is staggered
				int stagger;
				if (y % 2 == 1)
					stagger = (imageWidth / zoomLevel) / 2;
				else
					stagger = 0;
				//	Window coordinates for this cell
				int xPos = stagger + x * ((imageWidth / zoomLevel));
				int yPos = y * (int)((imageHeight / zoomLevel / 1.333));
				//	Draw underlying terrain
				switch (world.getCellAt(pos).getTerrain())
				{
					case ROCKY:
						g2d.drawImage(rocky, xPos, yPos , null);
						break;
					case CLEAR:
						g2d.drawImage(clear, xPos, yPos , null);
						break;
					case BLACK_ANTHILL:
						g2d.drawImage(anthill, xPos, yPos , null);
						break;
					case RED_ANTHILL:
						g2d.drawImage(anthill, xPos, yPos, null);
						break;
				}
				//	Draw food particles if applicable
				if (world.foodAt(pos) > 0)
					g2d.drawString("" + world.foodAt(pos), xPos, yPos);
				//	Draw any ants
			}
		}
	}

	/**
	 * @return the zoomLevel
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * @param zoomLevel the zoomLevel to set
	 */
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	/**
	 * Mouse Input Adaptor Inner Class.
	 * Handles drag map functionality.
	 */
	class MapDragListener extends MouseInputAdapter
	{
		int m_XDifference, m_YDifference;
		public void mouseDragged(MouseEvent e) {
			Container c = MapPanel.this.getParent();
			if (c instanceof JViewport) {
				JViewport jv = (JViewport) c;
			    Point p = jv.getViewPosition();
			    int newX = p.x - (e.getX()-m_XDifference);
			    int newY = p.y - (e.getY()-m_YDifference);
			    int maxX = MapPanel.this.getWidth() - jv.getWidth();
			    int maxY = MapPanel.this.getHeight() - jv.getHeight();
			    if (newX < 0) newX = 0;
			    if (newX > maxX) newX = maxX;
			    if (newY < 0) newY = 0;
			    if (newY > maxY) newY = maxY;
			    jv.setViewPosition(new Point(newX, newY));
			 }
		}
		public void mouseMoved(MouseEvent e) {
			if (getMousePosition() != null) {
				int x = getMousePosition().x / zoomLevel;
				int y = getMousePosition().y / zoomLevel;
				setToolTipText("Hello " + x + "/" + y);
			}
		}
		public void mousePressed(MouseEvent e) {
		    setCursor(Cursor.getPredefinedCursor(
			Cursor.MOVE_CURSOR));
			m_XDifference = e.getX();
			m_YDifference = e.getY();
		}
		public void mouseReleased(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(
			Cursor.DEFAULT_CURSOR));
		}     
	};
}
