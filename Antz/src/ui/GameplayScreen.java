package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import program.GameManager;
import world.World;


public class GameplayScreen extends JFrame {


	private MapPanel mapPanel;
	private ScorePanel scorePanel;
	private ControlPanel controlPanel;
	
	private final World world;
	
	/**
	 * Constructor.
	 * @param manager the manager running the current game.
	 */
	public GameplayScreen(final World world)
	{
		super("Antz");
		System.out.println("GameplayScreen Constructor");
		this.world = world;
	    Container pane = this.getContentPane();
	    JPanel outerPanel = new JPanel(new BorderLayout());
	    pane.add(outerPanel);
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    outerPanel.add("Center", mainPanel);
	    
	    //	Add the central main map panel, within a JScrollPane
		mapPanel = new MapPanel(this, world);
		JScrollPane scrollPane = new JScrollPane(mapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.setDoubleBuffered(true);
		scrollPane.addMouseWheelListener(new MapScrollListener(scrollPane));
		mainPanel.add("Center", scrollPane);
		//	Add the scoring information to NORTH
		scorePanel = new ScorePanel(world);
	    mainPanel.add("North", scorePanel);
	    //	Add the controls to SOUTH
	    controlPanel = new ControlPanel(world);
	    mainPanel.add("South", controlPanel);
	    mainPanel.add("East", new JPanel());
	    mainPanel.add("West", new JPanel());
	    
	    //  final initialization
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	    this.setResizable(true);
	    this.pack();
	    this.setVisible(true);  
	}
	
	/**
	 * Called when we need to update the world display.
	 */
	public void update()
	{
		mapPanel.repaint();
		scorePanel.redScore.setText("Score: " + world.getRedScore());
		scorePanel.blackScore.setText("Score: " + world.getBlackScore());
		scorePanel.scoreRatio.repaint();
		controlPanel.getCurrentTurn().setText("Turn: " + world.getTurn());
	}
	
	/**
	 * Scroll Pane listener (for mouse wheel zoom);
	 * @author JOH
	 * @version 0.1
	 */
	private class MapScrollListener implements MouseWheelListener {
		
		private JScrollPane scrollPane;
		
		/**
		 * Constructor.
		 * @param scrollPane
		 */
		public MapScrollListener(JScrollPane scrollPane) {
			this.scrollPane = scrollPane;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// Save the previous coordinates
			int oldZoom = mapPanel.getZoomLevel();
			Rectangle oldView = scrollPane.getViewport().getViewRect();

			// resize the panel for the new zoom
			int wheel = e.getWheelRotation();
			mapPanel.setZoomLevel(mapPanel.getZoomLevel() + wheel);
			if (mapPanel.getZoomLevel() < 1) {
				mapPanel.setZoomLevel(1);
			} else if (mapPanel.getZoomLevel() > 2) {
				if (wheel > 0)
					mapPanel.setZoomLevel(4);
				else
					mapPanel.setZoomLevel(2);
			}
			// calculate the new view position - this isn't perfect
			mapPanel.setPreferredSize(new Dimension(world.getWidth() * MapPanel.imageWidth / mapPanel.getZoomLevel(), (int)(world.getHeight() * MapPanel.imageHeight / mapPanel.getZoomLevel() / 1.333)));
			int newZoom = mapPanel.getZoomLevel();
			Point newViewPos = new Point();
			newViewPos.x = Math.max(0, (oldView.x + oldView.width / 2) * oldZoom / newZoom - oldView.width / 2);
			newViewPos.y = Math.max(0, (oldView.y + oldView.height / 2) * oldZoom / newZoom - oldView.height / 2);
			scrollPane.getViewport().setViewPosition(newViewPos);
			//	Redraw the panel
			mapPanel.revalidate();	
		}
	}
}
