package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import program.GameManager;
import world.World;
import ai.StateMachine;

	/**
	 * The graphical user interface used for the main menu. 
	 * @author JOH
	 * @version 0.1
	 */
	public class UserInterface extends JFrame{
	
		//	Reference to the Game manager which stores all the brains, etc.
		private GameManager manager;
		
		private JButton startGameButton;
		private JLabel loadedMapLabel;
		private JList<String> loadedBrainsList;
		private DefaultListModel<String> listModel;
		private final JFileChooser fc;
		
		/**
		 * Constructor.
		 */
		public UserInterface(GameManager manager) {
			super("Antz");
			fc = new JFileChooser();
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.manager = manager;
			//  initialize GUI using a BorderLayout
		    Container pane = this.getContentPane();
		    JPanel mainPanel = new JPanel(new BorderLayout());
		    pane.add(mainPanel);	
		    mainPanel.add("Center", new JPanel());
		    mainPanel.add("North", new JPanel());
		    mainPanel.add("South", new JPanel());
		    mainPanel.add("East", infoPanel());
		    mainPanel.add("West", menuPanel());
		    //  final initialization
		    this.setLocationRelativeTo(null);	//	This centralizes the window
		    this.setResizable(false);	//	The layout manager makes things look rubbish if resized
		    this.pack();
		    this.setVisible(true);  
		}
	
		/**
		 * The panel which shows the currently loaded map and brains.
		 * @return the panel
		 */
		private JPanel infoPanel() {
			JPanel outerPanel = new JPanel();
			
			JPanel innerPanel = new JPanel();
			innerPanel.setLayout(new GridLayout(0, 1));

			loadedMapLabel = new JLabel("No map loaded");
			loadedMapLabel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
			innerPanel.add(loadedMapLabel);
			outerPanel.add(innerPanel);
			
			listModel = new DefaultListModel<String>();
			loadedBrainsList = new JList<String>(listModel);
			loadedBrainsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			loadedBrainsList.setLayoutOrientation(JList.VERTICAL);
			listModel.addElement("No brains loaded");
			JScrollPane listScroll = new JScrollPane(loadedBrainsList);
			listScroll.setPreferredSize(new Dimension(200, 200));
			outerPanel.add(listScroll);
			return outerPanel;
		}
	
		/**
		 * The main menu buttons displayed when program first loaded.
		 * @return the JPanel enclosing the menu buttons.
		 */
		@SuppressWarnings("serial")
		private JPanel menuPanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));
			
			startGameButton = new JButton(new AbstractAction("Start Game") {
				public void actionPerformed(ActionEvent arg0) {
					startGame();
				}
			});
			//	You can't start a game until a map is loaded and number of players > 1
			startGameButton.setEnabled(false);
			panel.add(startGameButton);
			
			panel.add(new JButton(new AbstractAction("Upload Map") {
				public void actionPerformed(ActionEvent arg0) {
					loadMap();
				}
			}));
			
			panel.add(new JButton(new AbstractAction("Upload Ant Brain") {
				public void actionPerformed(ActionEvent arg0) {
					loadBrain();
				}
			}));
			
			panel.add(new JButton(new AbstractAction("View Credits") {
				public void actionPerformed(ActionEvent arg0) {
					viewCredits();
				}
			}));
			return panel;
		}
		
		private void viewCredits() {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * Shows the file dialog where the user can attempt to load an ant brain.
		 */
		private void loadBrain() {
			int r = fc.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile(); 
				manager.addBrain(file.getName(), StateMachine.newInstance(file.getAbsolutePath()));
				listModel.removeElement("No brains loaded");
				listModel.addElement(file.getName());
			}
			//	Check if we can start a game
			if (manager.getTotalPlayers() > 1 && manager.getWorld() != null) {
				startGameButton.setEnabled(true);
			}
		}
		
		/**
		 * Shows the file dialog where the user can attempt to load an ant world. 
		 */
		private void loadMap() {
			int r = fc.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile(); 
				manager.setWorld(World.parseWorld(file.getAbsolutePath()));
			}
			//	Check if we can start a game
			if (manager.getTotalPlayers() > 1 && manager.getWorld() != null) {
				startGameButton.setEnabled(true);
			}
		}
		
		/**
		 * Starts a game
		 */
		private void startGame() {
			manager.assignMatches();
		}
		
		///
		///	Getters & Setters
		///
	
		public JLabel getLoadedMapLabel() {
			return loadedMapLabel;
		}
	
		public void setLoadedMapLabel(JLabel loadedMapLabel) {
			this.loadedMapLabel = loadedMapLabel;
		}
	
		public JList<String> getLoadedBrainsList() {
			return loadedBrainsList;
		}
	
		public void setLoadedBrainsList(JList<String> loadedBrainsList) {
			this.loadedBrainsList = loadedBrainsList;
		}
	}