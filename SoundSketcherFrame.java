package assign11;
/**
 * @author Thanh Le
 * @assignment 11 Song File
 * @version 3/12/2024
 */



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundSketcherFrame extends JFrame implements ActionListener, ChangeListener {
	// Instance variables for managing components in the SoundSketcherFrame
	private SongPanel songPanel; // Panel for displaying the song's waveform
	private BetterDynamicArray<TrackPanel> trackPanelList; // List holding track panels
	private final int maxTracksCount = 16; // Maximum number of tracks
	private boolean isAddingTrack; // Flag to prevent adding tracks during updates
	private JPanel controlPanel; // Control panel containing playback and tempo controls
	private JToggleButton playButton; // Play/Stop button for the song
	private JToggleButton loopButton; // Loop toggle button for looping the song
	private JSlider tempoSlider; // Slider for adjusting the tempo
	private JLabel tempoLabel; // Label displaying the current tempo
	private JTabbedPane trackTabPane; // Tabbed pane for managing tracks
	private SimpleSynthesizer synthesizer; // Synthesizer for generating sound

	private JMenuItem saveMenuItem; // Menu item for saving the song
	private JMenuItem loadMenuItem; // Menu item for loading a song

	// Constructor to initialize the SoundSketcherFrame
	public SoundSketcherFrame() {
		// Initialize components
		synthesizer = new SimpleSynthesizer(); // Initialize synthesizer for sound generation
		songPanel = new SongPanel(700, 700); // Initialize the SongPanel with a specified size
		trackPanelList = new BetterDynamicArray<>(); // Initialize the track panel list
		isAddingTrack = false; // Initially not adding any tracks
		trackTabPane = new JTabbedPane(); // Initialize the tabbed pane for track management

		// Control panel setup
		controlPanel = new JPanel(); // Panel for controls like play, loop, and tempo
		playButton = new JToggleButton("Play"); // Play button to toggle between play and stop
		playButton.addActionListener(this); // Add action listener to play button
		loopButton = new JToggleButton("Loop"); // Loop button to toggle looping on/off
		loopButton.addActionListener(this); // Add action listener to loop button
		tempoSlider = new JSlider(0, 140, 50); // Tempo slider to adjust tempo (0-140 BPM)
		tempoSlider.setMajorTickSpacing(25); // Set major tick spacing for the slider
		setTempoSlider(50); // Set the initial tempo to 50 BPM
		tempoSlider.setMinorTickSpacing(5); // Set minor tick spacing for the slider
		tempoSlider.setPaintTicks(true); // Show ticks on the slider
		tempoSlider.setPaintLabels(true); // Show labels for ticks on the slider
		tempoLabel = new JLabel("Tempo: " + tempoSlider.getValue() + " BPM"); // Label showing the current tempo
		tempoSlider.addChangeListener(this); // Add change listener to the tempo slider
		controlPanel.add(playButton); // Add play button to control panel
		controlPanel.add(loopButton); // Add loop button to control panel
		controlPanel.add(tempoLabel); // Add tempo label to control panel
		controlPanel.add(tempoSlider); // Add tempo slider to control panel

		// Track initialization
		TrackPanel firstTrackPanel = new TrackPanel(650, 650, 0, synthesizer); // Initialize the first track panel
		firstTrackPanel.setLength(4); // Set the track length to 4
		firstTrackPanel.setTempo(50); // Set the track tempo to 50 BPM
		trackPanelList.add(firstTrackPanel); // Add the first track panel to the list
		songPanel.setTrackList(trackPanelList); // Set the track list for the song panel
		trackTabPane.addTab("Song", songPanel); // Add the song tab
		trackTabPane.addTab("Track 0", firstTrackPanel); // Add the first track tab
		trackTabPane.addTab("Add Track", new JPanel()); // Add a tab to add new tracks
		trackTabPane.setSelectedIndex(1); // Set the initial selected tab to Track 0
		trackTabPane.addChangeListener(this); // Add change listener to the tab pane

		// Main panel setup with layout
		JPanel mainPanel = new JPanel(new BorderLayout()); // Create a panel with BorderLayout
		mainPanel.add(controlPanel, BorderLayout.NORTH); // Add the control panel to the top
		mainPanel.add(trackTabPane, BorderLayout.CENTER); // Add the tabbed pane to the center

		// Menu setup for file operations
		JMenuBar menuBar = new JMenuBar(); // Create a menu bar
		JMenu fileMenu = new JMenu("File"); // Create a file menu
		saveMenuItem = new JMenuItem("Save"); // Create save menu item
		loadMenuItem = new JMenuItem("Load"); // Create load menu item
		saveMenuItem.addActionListener(this); // Add action listener to save menu item
		loadMenuItem.addActionListener(this); // Add action listener to load menu item
		fileMenu.add(saveMenuItem); // Add save item to the file menu
		fileMenu.add(loadMenuItem); // Add load item to the file menu
		menuBar.add(fileMenu); // Add file menu to the menu bar
		setJMenuBar(menuBar); // Set the menu bar for the frame

		// Frame setup
		this.setTitle("Sound Sketcher"); // Set the window title
		this.setPreferredSize(new Dimension(800, 800)); // Set the preferred size of the frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // Close the application when the window is closed
		setContentPane(mainPanel); // Set the main panel as the content pane
		this.pack(); // Pack the components within the frame
	}

	// ActionPerformed method to handle actions for play, loop, save, and load buttons
	@Override
	public void actionPerformed(ActionEvent event) {
		// If the source of the action is the play button
		if (event.getSource() == playButton) {
			boolean play = playButton.isSelected(); // Check if the play button is selected
			for (int i = 0; i < trackPanelList.size(); i++) {
				if (play) {
					trackPanelList.get(i).play(); // Start playing all tracks
					playButton.setText("Stop"); // Change button text to "Stop"
				} else {
					trackPanelList.get(i).stop(); // Stop playing all tracks
					playButton.setText("Play"); // Change button text to "Play"
				}
			}
		}
		// If the source of the action is the loop button
		else if (event.getSource() == loopButton) {
			boolean loop = loopButton.isSelected(); // Check if the loop button is selected
			for (int i = 0; i < trackPanelList.size(); i++) {
				trackPanelList.get(i).setLoop(loop); // Set the loop property for all tracks
			}
		}
		// If the source of the action is the save menu item
		else if (event.getSource() == saveMenuItem) {
			saveToFile(); // Call the save method
		}
		// If the source of the action is the load menu item
		else if (event.getSource() == loadMenuItem) {
			loadFromFile(); // Call the load method
		}
	}


	// Method to save the current song data to a file
	private void saveToFile() {
		// Create a new file chooser for selecting where to save the file
		JFileChooser chooser = new JFileChooser();

		// Set the file filter so only files with the extension ".song" are shown
		chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));

		// Show the save dialog and get the result
		int result = chooser.showSaveDialog(this);

		// If the user approves the save action (selects a location and filename)
		if (result == JFileChooser.APPROVE_OPTION) {
			// Get the selected file
			File file = chooser.getSelectedFile();

			// Call a method to write the song data to the file
			writeFile(file);
		}
	}

	// Method to load song data from a file
	private void loadFromFile() {
		// Create a new file chooser for selecting the file to load
		JFileChooser chooser = new JFileChooser();

		// Set the file filter to show only ".song" files
		chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));

		// Show the open dialog and get the result
		int result = chooser.showOpenDialog(this);

		// If the user approves the open action (selects a file)
		if (result == JFileChooser.APPROVE_OPTION) {
			// Get the selected file
			File file = chooser.getSelectedFile();

			// Read the file and get the tempo value
			int tempo = readFile(file);

			// Set the tempo slider value to the tempo read from the file
			tempoSlider.setValue(tempo);

			// Update the tabs to reflect the loaded song data
			updateTabs();
		}
	}

	private void writeFile(File file) {
		SongFiles.writeFile(file,tempoSlider.getValue(),trackPanelList,songPanel);
		// Implementation for reading file
		// Restore the state and return the tempo value
	}

	private int readFile(File file) {
		return SongFiles.readFile(file,synthesizer, trackPanelList,songPanel,700,700 );

		// Implementation for reading file
		// Restore the state and return the tempo value

	}

	// Method to update the track tabs, ensuring that they reflect the current track list
	private void updateTabs() {
		isAddingTrack = true; // Prevents changes while adding a new track

		// Remove any tabs beyond the second tab, which is for adding new tracks
		while (trackTabPane.getTabCount() > 2) {
			trackTabPane.remove(1); // Remove the tab at index 1 (index 0 is for the song panel)
		}

		// Add a tab for each track currently in the trackPanelList
		int trackNumber = 0;
		while (trackNumber < trackPanelList.size()) {
			// Insert a new tab for each track, with a label like "Track 0", "Track 1", etc.
			trackTabPane.insertTab("Track " + trackNumber, null, trackPanelList.get(trackNumber), null, trackNumber + 1);
			trackNumber++; // Move to the next track
		}

		// Select the first track tab (default after updating)
		trackTabPane.setSelectedIndex(1);
		isAddingTrack = false; // Allow further updates after track tab modification
	}

	// Method to add a new track to the trackPanelList and update the tabs
	public void addTrack() {
		isAddingTrack = true; // Prevents changes while adding a new track

		// Check if the number of tracks is less than the maximum allowed
		if (trackPanelList.size() < maxTracksCount) {
			// Create a new track panel with size and synthesizer settings
			TrackPanel newTrackPanel = new TrackPanel(650, 650, trackPanelList.size(), synthesizer);
			newTrackPanel.setTempo(tempoSlider.getValue()); // Set the tempo for the new track
			newTrackPanel.setLoop(loopButton.isSelected()); // Set whether the track should loop
			trackPanelList.add(newTrackPanel); // Add the new track panel to the list
			newTrackPanel.setLength(4); // Set the length of the track

			// Insert the new track tab at the second to last position
			trackTabPane.insertTab("Track " + (trackPanelList.size() - 1), null, trackPanelList.get(trackPanelList.size() - 1),
					null, trackTabPane.getTabCount() - 1);
		}

		// Select the new track tab that was just added
		trackTabPane.setSelectedIndex(trackTabPane.getTabCount() - 2);
		isAddingTrack = false; // Allow further updates after adding the track
	}

	// Method to handle state changes in components (like tab changes or tempo changes)
	public void stateChanged(ChangeEvent event) {
		// Check if the event source is the track tab pane and if it's the "Add Track" tab
		if (event.getSource() == trackTabPane && trackTabPane.getSelectedIndex() == trackTabPane.getTabCount() - 1
				&& !isAddingTrack) {
			addTrack(); // Add a new track when the "Add Track" tab is selected
		}
		// Check if the event source is the tempo slider
		else if (event.getSource() == tempoSlider) {
			int newTempo = tempoSlider.getValue(); // Get the new tempo value from the slider
			tempoLabel.setText("Tempo: " + newTempo + " BPM"); // Update the tempo label with the new value
			songPanel.setTempo(newTempo); // Update the tempo of the song panel
			// Update the tempo for each track in the track panel list
			for (int i = 0; i < trackPanelList.size(); i++) {
				trackPanelList.get(i).setTempo(newTempo);
			}
		}
	}



	// Method to set the tempo of the tempo slider and adjust its range if necessary
	private void setTempoSlider(int newTempo) {
		// If the new tempo is less than the current minimum value, update the minimum
		if (newTempo < tempoSlider.getMinimum()) {
			tempoSlider.setMinimum(newTempo); // Set the slider's minimum value to the new tempo
		}

		// If the new tempo is greater than the current maximum value, update the maximum
		if (newTempo > tempoSlider.getMaximum()) {
			tempoSlider.setMaximum(newTempo); // Set the slider's maximum value to the new tempo
		}

		// Set the current value of the tempo slider to the new tempo
		tempoSlider.setValue(newTempo); // Update the slider to reflect the new tempo value
	}

}