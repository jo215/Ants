package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import world.World;

public class ControlPanel extends JPanel implements ActionListener, ChangeListener{

	private World world;
	private JButton pauseButton, playButton;
	private JLabel currentTurn;
	static final int MINSPEED = 0;
	static final int MAXSPEED = 10;
	
	public ControlPanel(World world) {
		super();
		this.world = world;
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
	}

	@Override
	/**
	 * Listener for the various control buttons.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == pauseButton)
			world.setPaused(true);
		if (e.getSource() == playButton)
			world.setPaused(false);
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

	public JLabel getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(JLabel currentTurn) {
		this.currentTurn = currentTurn;
	}

}
