package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import program.GameManager;
import world.World;


public class GameplayScreen extends JFrame {


	private MapPanel mapPanel;
	private final World world;
	
	/**
	 * Constructor.
	 * @param manager the manager running the current game.
	 */
	public GameplayScreen(final World world)
	{
		super("Antz");

		this.world = world;
		
	    Container pane = this.getContentPane();
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    pane.add(mainPanel);	
	    mainPanel.add("Center", new JPanel());
	    mainPanel.add("North", new JPanel());
	    mainPanel.add("South", new JPanel());
	    mainPanel.add("East", new JPanel());
	    mainPanel.add("West", new JPanel());
	    
		mapPanel = new MapPanel(this, world);
		final JScrollPane scrollPane = new JScrollPane(mapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.setDoubleBuffered(true);

		scrollPane.addMouseWheelListener(new MouseWheelListener() {
			/**
			 * Scroll Map functionality
			 */
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
				mapPanel.revalidate();
				
			}
		});
		mainPanel.add("Center", scrollPane);
	    
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
	}
}
