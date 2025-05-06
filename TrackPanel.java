package assign11;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The TrackPanel class represents a graphical user interface (GUI) for controlling
 * and editing a music track in a sequencer application. It provides controls for
 * muting the track, adjusting the volume, selecting an instrument, and changing
 * the track length.
 * This class extends the SketchingPanel and implements ActionListener and ChangeListener
 * to handle user interactions.
 *
 * @author Thanh Le and Professor Eric Heisler
 * @assign10 GridCanvas
 * @version 11-22-2024
 */
public class TrackPanel extends SketchingPanel implements ActionListener, ChangeListener {
	// Instance variables
	private TrackEditor trackEditor; // The TrackEditor component responsible for managing track details
	private JToggleButton muteButton; // Button to toggle the mute state of the track
	private JPanel controlPanel; // Panel containing track control components
	private JSpinner lengthSpinner; // Spinner to select the track length
	private JSlider volumeControl; // Slider to control the volume
	private JComboBox<String> instrument; // Dropdown menu to select the instrument
	private JLabel instrumentLabel; // Label for the instrument dropdown
	private JLabel lengthLabel; // Label for the track length spinner
	private JLabel volumeLabel; // Label displaying the current volume
	private boolean isMute; // Boolean flag indicating whether the track is muted
	private SimpleSynthesizer simpleSynthesizer;
	/**
	 * Constructs a TrackPanel with the specified dimensions and track number.
	 *
	 * @param width       the width of the TrackPanel
	 * @param height      the height of the TrackPanel
	 * @param trackNumber the track number to be managed by this panel
	 */
	public TrackPanel(int width, int height, int trackNumber, SimpleSynthesizer simpleSynthesizer) {
		// Initialize the TrackEditor
		this.simpleSynthesizer = simpleSynthesizer;
		trackEditor = new TrackEditor(width, height, trackNumber, this.simpleSynthesizer);
		isMute = false;

		JComponent box= (JComponent) Box.createRigidArea(new Dimension(200, 800));

		// Initialize mute button
		muteButton = new JToggleButton("Mute");
		muteButton.addActionListener(this);

		// Initialize track length spinner
		lengthLabel = new JLabel("Track Length:");
		lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 4, 8, 16, 32, 64, 128, 256, 512 }));
		lengthSpinner.addChangeListener(this);

		// Initialize volume control slider
		volumeControl = new JSlider(SwingConstants.HORIZONTAL, 0, 127, trackEditor.getVolume());
		volumeControl.setMajorTickSpacing(20);
		volumeControl.setMinorTickSpacing(10);
		volumeControl.setPaintTicks(true);
		volumeControl.setPaintLabels(true);

		volumeLabel = new JLabel("Volume: " + trackEditor.getVolume());
		volumeControl.addChangeListener(this);

		// Initialize instrument selector
		instrumentLabel = new JLabel("Instrument:");
		instrument = new JComboBox<>(trackEditor.getInstrumentNames());

		// Initialize control panel
		controlPanel = new JPanel();
		controlPanel.add(muteButton);
		controlPanel.add(volumeLabel);
		controlPanel.add(volumeControl);
		controlPanel.add(lengthLabel);
		controlPanel.add(lengthSpinner);
		controlPanel.add(instrumentLabel);
		controlPanel.add(instrument);

		// Set layout and add components
		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(trackEditor, BorderLayout.CENTER);
		add(box, BorderLayout.EAST);
	}

	/**
	 * Returns the sequencer associated with this TrackPanel.
	 *
	 * @return the SimpleSequencer instance
	 */
	@Override
	public SimpleSequencer getSequencer() {
		return trackEditor.getSequencer();
	}

	/**
	 * Sets the length of the track.
	 *
	 * @param length the new track length
	 */
	@Override
	public void setLength(int length) {
		trackEditor.setLength(length);
		try {
			lengthSpinner.setValue(length);
		}
		catch (IllegalArgumentException e) {
			SpinnerListModel model = (SpinnerListModel)(lengthSpinner.getModel());
			@SuppressWarnings("unchecked")
			ArrayList<Integer> values = new ArrayList<Integer>((List<Integer>)model.getList());
			values.add(length);
			Collections.sort(values);
			model.setList(values);
			lengthSpinner.setValue(length);
		}
	}

	/**
	 * Sets the audio events for the track.
	 *
	 * @param events the audio events to set
	 */
	@Override
	public void setEvents(BetterDynamicArray<AudioEvent> events) {
		trackEditor.setEvents(events);
	}

	/**
	 * Clears the track editor.
	 */
	@Override
	public void clear() {
		trackEditor.clear();
	}

	/**
	 * Returns the current volume of the track.
	 *
	 * @return the current volume
	 */
	public int getVolume() {
		return trackEditor.getVolume();
	}

	/**
	 * Sets the volume of the track.
	 *
	 * @param volume the new volume level
	 */
	public void setVolume(int volume) {
		trackEditor.setVolume(volume);
	}

	/**
	 * Returns the index of the currently selected instrument.
	 *
	 * @return the index of the selected instrument
	 */
	public int getInstrument() {
		return this.instrument.getSelectedIndex();
	}

	/**
	 * Sets the instrument for the track.
	 *
	 * @param instrument the index of the instrument to set
	 */
	public void setInstrument(int instrument) {
		this.instrument.setSelectedIndex(instrument);
		trackEditor.setInstrument(instrument);
	}

	/**
	 * Handles button and combo box actions.
	 *
	 * @param event the action event triggered by a component
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == muteButton) {
			isMute = !isMute; // Toggle mute state
			trackEditor.setMute(isMute);

			if (isMute) {
				setVolume(0); // Mute the track
				volumeControl.setValue(0);
				volumeLabel.setText("Volume: 0");
				muteButton.setText("Unmute");
			} else {
				volumeLabel.setText("Volume: " + trackEditor.getVolume());
				muteButton.setText("Mute");
			}
		}

		if (event.getSource() == instrument) {
			int selectedInstrument = instrument.getSelectedIndex();
			setInstrument(selectedInstrument);
			requestFocus(); // Return focus to the panel
		}
	}

	/**
	 * Handles state changes for volume and length controls.
	 *
	 * @param event the change event triggered by a component
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == lengthSpinner) {
			int newLength = (int) lengthSpinner.getValue();
			setLength(newLength);
		} else if (event.getSource() == volumeControl) {
			int newVolume = volumeControl.getValue();
			setVolume(newVolume);
			volumeLabel.setText("Volume: " + newVolume);
			if (isMute && trackEditor.getVolume() > 0) {
				isMute = false;
				trackEditor.setMute(false);
				muteButton.setText("Mute");
			}
		}
	}
}

