package assign11;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * The SongPanel class represents a graphical user interface (GUI) for controlling
 * and editing a song in a sequencer application. It provides controls for playing
 * or stopping the song, looping the song, and adjusting the song length.
 * This class extends the SketchingPanel and implements ActionListener and ChangeListener
 * to handle user interactions.
 *
 * @author  Thanh Le and Professor Eric Heisler
 * @assign10 GridCanvas
 * @version 11-22-2024
 */
public class SongPanel extends SketchingPanel implements ActionListener, ChangeListener {
    private SongEditor songEditor; // The SongEditor component responsible for managing song details
    private JPanel controlPanel; // Panel containing song control components
    private JToggleButton playStopButton; // Button to play or stop the song
    private JToggleButton loopButton; // Button to toggle loop state of the song
    private JSpinner lengthSpinner; // Spinner to select the song length

    /**
     * Constructs a SongPanel with the specified dimensions.
     *
     * @param width  the width of the SongPanel
     * @param height the height of the SongPanel
     */
    public SongPanel(int width, int height) {

        // Initialize the SongEditor
        songEditor = new SongEditor(width, height);

        // Initialize play/stop button
        playStopButton = new JToggleButton("Play");
        playStopButton.addActionListener(this);

        // Initialize loop button
        loopButton = new JToggleButton("Loop");
        loopButton.addActionListener(this);

        // Initialize song length spinner
        JLabel lengthLabel = new JLabel("Song Length:");
        lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 4, 8, 16, 32, 64, 128, 256, 512 }));
        lengthSpinner.addChangeListener(this);

        // Initialize control panel
        controlPanel = new JPanel();
        controlPanel.add(playStopButton);
        controlPanel.add(loopButton);
        controlPanel.add(lengthLabel);
        controlPanel.add(lengthSpinner);

        // Set layout and add components
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(songEditor, BorderLayout.CENTER);
    }

    /**
     * Returns the sequencer associated with this SongPanel.
     *
     * @return the SimpleSequencer instance
     */
    @Override
    public SimpleSequencer getSequencer() {
        return songEditor.getSequencer();
    }

    /**
     * Sets the length of the song.
     *
     * @param length the new song length
     */
    @Override
    public void setLength(int length) {
        songEditor.setLength(length);
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
     * Sets the audio events for the song.
     *
     * @param events the audio events to set
     */
    @Override
    public void setEvents(BetterDynamicArray<AudioEvent> events) {
        songEditor.setEvents(events);
    }

    /**
     * Clears the song editor.
     */
    @Override
    public void clear() {
        songEditor.clear();
    }

    /**
     * Sets the list of tracks in the song.
     *
     * @param trackList the list of tracks to set
     */
    public void setTrackList(BetterDynamicArray<TrackPanel> trackList) {
        songEditor.setTrackList(trackList);
    }

    /**
     * Handles button and toggle actions.
     *
     * @param event the action event triggered by a component
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Object button = event.getSource();

        if (button == playStopButton) {
            System.out.println(getSequencer());
            if (playStopButton.isSelected()) {
                play();// Inherited method to play the song
                playStopButton.setText("Stop");
            } else {
                stop();  // Inherited method to stop the song
                playStopButton.setText("Play");
            }
        } else if (button == loopButton) {
            setLoop(loopButton.isSelected());  // Toggle loop setting
        }
    }

    /**
     * Handles changes in the song length spinner.
     *
     * @param event the change event triggered by the length spinner
     */
    @Override
    public void stateChanged(ChangeEvent event) {
        if (event.getSource() == lengthSpinner) {
            int newLength = (int) lengthSpinner.getValue();
            setLength(newLength);
        }
    }
}
