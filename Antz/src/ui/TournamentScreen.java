package ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;



import program.GameManager;
import world.World;
import ai.StateMachine;

/**
 * Screen to set up a tournament
 * @author kris
 * @version 1
 */
@SuppressWarnings("serial")
public class TournamentScreen extends JFrame{
	private GameManager gm;
	private final JFileChooser fc;
	
	Font bigFont;
	
	//buttons
	JButton browseWorldButton;
	JButton generateWorldButton;
	JButton browseBrainButton;
	JButton addBrainButton;
	JButton beginButton;
	JButton backButton;
	
	//text fields
	JTextField brainName;
	JTextField brainFile;
	JTextField worldFile;
	
	//table
	JTable table;
	JScrollPane tableScroll;
	int numRows; //initial number of rows
	
	/**
	 * Constructor
	 */
	public TournamentScreen(GameManager gm){
		super("Tournament Mode");
		this.gm = gm;
		fc = new JFileChooser();
		bigFont = new Font("Tahoma", Font.BOLD, 20);
		
		//make buttons
		backButton = new JButton(new AbstractAction("Back"){
			public void actionPerformed(ActionEvent e) {
				closeScreen();
			}
		});
		beginButton = new JButton(new AbstractAction("Begin"){
			public void actionPerformed(ActionEvent e) {
				startTournament();
			}
		});
		beginButton.setEnabled(false);
		browseWorldButton = new JButton(new AbstractAction("Browse"){
			public void actionPerformed(ActionEvent e) {
				loadMap();
			}
		});
		generateWorldButton = new JButton(new AbstractAction("Generate"){
			public void actionPerformed(ActionEvent e) {
				generateMap();
			}
		});
		browseBrainButton = new JButton(new AbstractAction("Browse"){
			public void actionPerformed(ActionEvent e) {
				browseBrain();
			}
		});
		addBrainButton = new JButton(new AbstractAction("Add"){
			public void actionPerformed(ActionEvent e) {
				addBrain();
			}
		});
		
		//make text fields
		brainName = new JTextField(20);
		brainFile = new JTextField(20);
		worldFile = new JTextField(20);
		
		worldFile.addFocusListener(new WorldFocusListener());
		brainFile.addFocusListener(new BrainFocusListener());
		
		//make table
		String[] colHeadings = {"Name","Brain"};
		numRows = 14;
		DefaultTableModel model = new DefaultTableModel(numRows, colHeadings.length) ;
		model.setColumnIdentifiers(colHeadings);
		table = new JTable(model){
			public boolean isCellEditable(int rowIndex, int colIndex) {
				  return false; //Disable the editing of any cell
			}
			
		};
		
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//to prevent users resizing columns smaller than the viewport
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(200);

		tableScroll = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setPreferredSize(new Dimension(300, 19*numRows));
		
		
		//place components of GUI
		addComponents();
		
	    //final initialization
		this.setResizable(false);
	    this.pack();
	    this.setLocationRelativeTo(null);	
	    this.setVisible(true); 
		
	}

	/**
	 * Closes the tournament screen
	 */
	private void closeScreen() {
		gm.setWorld(null);//in case the world was set
		gm.resetBrains(); //in case any brains were set
		dispose();
		
	}

	/**
	 * Checks if this brain can be added to the game manager;
	 * if possible, adds it.
	 */
	private void addBrain() {
		//determine team name
		String teamName;	
		if(brainName.getText().compareTo("") == 0){
			teamName = new File(brainFile.getText()).getName();
		} else {
			teamName = brainName.getText();
		}
		
		//if can't upload, show error
		//(either a brain with that name already exists or brainFile field is empty)
		if (!gm.addBrain(teamName, StateMachine.newInstance(brainFile.getText()))){
			JOptionPane.showMessageDialog(this, "Error!\nThis brain cannot be added.\n"+
					"Please make sure that all brains have different names.", "Error", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//add brain to the table
		if(gm.getTotalPlayers()-1 < numRows){
			// if there are enough rows:
			table.setValueAt(teamName, gm.getTotalPlayers()-1, 0);
			table.setValueAt(brainFile.getText(), gm.getTotalPlayers()-1, 1);
		} else {
			//add a new row
			((DefaultTableModel)table.getModel()).addRow(new Object[]{teamName, 
					brainFile.getText()});
		}
		
		//reset text fields
		brainFile.setText("");
		brainName.setText("");
		
		checkCanStartTournament();
	}

	/**
	 * Opens a dialog window to allow user to choose the brain file,
	 * checks its validity
	 */
	private void browseBrain() {
		int r = fc.showOpenDialog(this);
		if (r == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			
			checkBrain(file.getAbsolutePath());
		}
		
		//Check if we can start the tournament
		checkCanStartTournament();
	}

	/**
	 * Checks if the file at a specified path can be
	 * a valid ant brain; if not, 
	 * clears the corresponding text field
	 * @param absolutePath absolute path to the brain text file
	 */
	private void checkBrain(String absolutePath) {
		if (StateMachine.newInstance(absolutePath) == null){
			JOptionPane.showMessageDialog(this, "Error!\nThis Ant Brain file is invalid.\n"+
					"Please check the contents of this file or try uploading a different one.", 
					"Error", JOptionPane.ERROR_MESSAGE);
			brainFile.setText("");
		} else {
			brainFile.setText(absolutePath);
		}
	}

	/**
	 * Sets the world's map to auto-generated map
	 */
	private void generateMap(){
		gm.setGeneratedWorld();
		worldFile.setText("(auto-generated map)");
		
		//Check if we can start the tournament
		checkCanStartTournament();
	}

	/**
	 * Opens a dialog window to allow user to choose the map file;
	 * checks if it is a valid map
	 */
	private void loadMap() {
		int r = fc.showOpenDialog(this);
		if (r == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile(); 
			checkAndSetMap(file.getAbsolutePath());
		}

		//Check if we can start the tournament
		checkCanStartTournament();
	}
	
	/**
	 * Checks if the world is set and if there are
	 * enough (>= 2) brains to start a tournament.
	 */
	private void checkCanStartTournament() {
		if( (gm.getWorld() != null) && (gm.getTotalPlayers() >=2) ){
			beginButton.setEnabled(true);
		}
	}

	/**
	 * Checks the file at a given path if it is a valid map;
	 * if valid, sets it as a world's map 
	 * @param absPath path to the map file
	 */
	private void checkAndSetMap(String absPath){
		if (gm.setWorld(World.parseWorld(absPath)))
		{
			worldFile.setText(absPath);
		} else {
			worldFile.setText("");
			JOptionPane.showMessageDialog(this, "Error!\nThis Map text file is invalid.\n"+
		"Please check the contents of this file or try uploading a different one.", 
		"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Starts the tournament.
	 */
	private void startTournament() {
		// play tournament in worker thread
		SwingWorker<Void, Void> worker =
		new SwingWorker<Void, Void>() {
			public Void doInBackground() {
				gm.playTournament();;
				return null;
			}
		};
		worker.execute();
		
		//close window
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * Arrange the components with grid bag layout
	 */
	private void addComponents() {
		Container pane = this.getContentPane();
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel mainPanel = new JPanel(gbl);
		gbc.insets = new Insets(10,10,10,10);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		JLabel worldLabel = new JLabel("World", JLabel.CENTER);
		worldLabel.setFont(bigFont);
		mainPanel.add(worldLabel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("File", JLabel.LEFT), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		mainPanel.add(worldFile, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		mainPanel.add(browseWorldButton, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 1;
		mainPanel.add(generateWorldButton, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		JLabel brainLabel = new JLabel("Ant Brain", JLabel.CENTER);
		brainLabel.setFont(bigFont);
		mainPanel.add(brainLabel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		mainPanel.add(new JLabel("Name", JLabel.LEFT), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		mainPanel.add(brainName, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		mainPanel.add(new JLabel("File", JLabel.LEFT), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		mainPanel.add(brainFile, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 4;
		mainPanel.add(browseBrainButton, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(addBrainButton, gbc);
		
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 6;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(tableScroll, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(beginButton, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 9;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(backButton, gbc);

		pane.add(mainPanel);
	}
	
	
	//  ----- nested classes ------
		
	/**
	* Focus listener for world text field
	*/
	class WorldFocusListener extends FocusAdapter{
		
		String prevText = "";
				
		/**
		 * Checks the path specified in the world text field
		 */
		public void focusLost(FocusEvent e){
			if (("".compareTo(worldFile.getText()) != 0) && 
					(prevText.compareTo(worldFile.getText()) != 0) &&
					("(auto-generated map)".compareTo(worldFile.getText()) != 0)){
				checkAndSetMap(worldFile.getText());
			}
			prevText = worldFile.getText();
			
		}
	}
	
	/**
	 * Focus listener for brain text fields
	 */
	class BrainFocusListener extends FocusAdapter{
		
		String prevText = "";
				
		/**
		 * Checks the path specified in the brain text field
		 */
		public void focusLost(FocusEvent e){
			//if text field has changed and is not empty
			if (("".compareTo(brainFile.getText()) != 0) && 
					(prevText.compareTo(brainFile.getText()) != 0)){
				checkBrain(brainFile.getText());			
			}
			prevText = brainFile.getText();
		}
	}
	
}

