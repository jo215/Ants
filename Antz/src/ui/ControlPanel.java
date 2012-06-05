package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import enums.E_Color;
import world.Position;
import world.World;

/**
 * THis class contains all the game screen controls.
 * @author JOH
 * @version 1
 */
@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener, ChangeListener{

	private World world;
	private JButton pauseButton, playButton, redHome, blackHome;
	private JLabel currentTurn;
	private MapPanel mapPanel;
	static final int MINSPEED = 0;
	static final int MAXSPEED = 10;
	private Point redHill, blackHill;
	
	/**
	 * Constructor.
	 * @param world the world we link to 
	 */
	public ControlPanel(World world, final MapPanel mapPanel) {
		super();
		this.world = world;
		this.mapPanel = mapPanel;
		JPanel temp = new JPanel(new GridLayout(2,1));
		add(temp);
		JPanel buttons = new JPanel();
		//temp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		temp.add(buttons);
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(this);
		buttons.add(pauseButton);
		playButton = new JButton("Play");
		playButton.addActionListener(this);
		buttons.add(playButton);
		currentTurn = new JLabel("Turn: 0");
		JPanel turn = new JPanel();
		turn.add(currentTurn);
		temp.add(turn);

		JPanel temp2 = new JPanel();
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, MINSPEED, MAXSPEED, MAXSPEED);
		speedSlider.addChangeListener(this);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		//temp2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		temp2.add(speedSlider);
		temp2.add(new JLabel("Speed"));
		add(temp2);
		
	    // make marker check box
	    JCheckBox markersCheckbox = new JCheckBox("Show markers");
	    markersCheckbox.setSelected(mapPanel.doDrawMarkers());
	    markersCheckbox.addItemListener(new ItemListener(){
	    	 public void itemStateChanged(ItemEvent e) {
	             mapPanel.toggleMarkers();
	         }
	    });
	    add(markersCheckbox);
		
	    //	Buttons to move the camera back home
	    //	Figure out where the hills are

	    int x = 0, y = 0;
	    while (!world.getAnthillAt(new Position(x,y), E_Color.BLACK))
	    {
	    	x++;
	    	if (x == world.getWidth()) {
	    		x = 0;
	    		y++;
	    	}
	    }
	    blackHill = new Point(x, y);
	    
	    x = 0;
	    y = 0;
	    while (!world.getAnthillAt(new Position(x,y), E_Color.RED))
	    {
	    	x++;
	    	if (x == world.getWidth()) {
	    		x = 0;
	    		y++;
	    	}
	    }
	    redHill = new Point(x, y);	    
	    
	    
		JPanel temp3 = new JPanel();
		redHome = new JButton("Red Home");
		redHome.setBackground(Color.RED);
		redHome.setForeground(Color.WHITE);
		redHome.addActionListener(this);
		
		blackHome = new JButton("Black Home");
		blackHome.setBackground(Color.BLACK);
		blackHome.setForeground(Color.WHITE);
		blackHome.addActionListener(this);
		
		temp3.add(redHome);
		temp3.add(blackHome);
		add(temp3);
	}

	@Override
	/**
	 * Listener for the various control buttons.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == pauseButton)
		{
			world.setPaused(true);
		}
		if (e.getSource() == playButton)
		{
			world.setPaused(false);
		}
		if (e.getSource() == redHome)
		{
			viewHill(redHill);
		}
		if (e.getSource() == blackHome)
		{
			viewHill(blackHill);
		}
			
	}

	/**
	 * Sets the viewport position to the given anthill location.
	 * @param hill the point to check
	 */
	private void viewHill(Point hill) {
		JViewport viewport = (JViewport) mapPanel.getParent();
		
		int stagger;
		if (hill.y % 2 == 1)
			stagger = (MapPanel.imageWidth / mapPanel.getZoomLevel()) / 2;
		else
			stagger = 0;
		int xPos = stagger + hill.x * (MapPanel.imageWidth / mapPanel.getZoomLevel()) ;
		int yPos = hill.y * (int)(((MapPanel.imageWidth / mapPanel.getZoomLevel())  / 1.333));
		
		viewport.setViewPosition(new Point(xPos, yPos));
	}

	/**
	 * Listener for speed slider changes.
	 * @param arg0
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider)e.getSource();
		if (slider.getValueIsAdjusting())
			world.setSleepAmount((MAXSPEED - slider.getValue()) * 10);
	}

	/**
	 * Gets the current turn.
	 * @return
	 */
	public JLabel getCurrentTurn() {
		return currentTurn;
	}

	/**
	 * Sets the current turn.
	 * @param currentTurn
	 */
	public void setCurrentTurn(JLabel currentTurn) {
		this.currentTurn = currentTurn;
	}

}
