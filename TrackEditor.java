package assign11;
/**@author Thanh Le
 * @assign 10 GridCanvas
 * @version 11-22-2024
 * */



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Vector;

public class TrackEditor extends GridCanvas {
    // Declare instance variables for sequencer, synthesizer, track number, dimensions, and current pitch
    private SimpleSequencer simpleSequencer;
    private SimpleSynthesizer simpleSynthesizer;
    private int trackNumber;
    private int width;
    private int height;
    private int currentPitch;

    // Constructor: Initializes the TrackEditor with given dimensions and synthesizer, and sets up the sequencer
    public TrackEditor(int width, int height, int trackNumber, SimpleSynthesizer simpleSynthesizer) {
        super(width, height, 120, 4, 10, 4); // Set the grid: 120 rows (pitches), 4 columns (default track length)
        this.width = width;
        this.height = height;
        this.trackNumber = trackNumber;
        this.simpleSynthesizer = simpleSynthesizer;

        setPreferredSize(new Dimension(width, height)); // Set the preferred size of the panel

        // Create a new SimpleSequencer with a default length of 16 columns
        this.simpleSequencer = new SimpleSequencer(1);
        setRestrictions(1, -1); // Set restrictions for the grid (e.g., row and column limitations)

        // Add mouse listeners for interacting with the grid
        addMouseListener(this);
        addMouseMotionListener(this);

        // Initialize the pitch range with Middle C (C4) set to 60
        this.currentPitch = 60; // Middle C
    }

    // Set the length of the track (number of columns in the grid)
    public void setLength(int length) {
        simpleSequencer.setLength(length); // Update sequencer length
        setColumns(length); // Adjust grid columns to match the track length
    }

    // Get the current length of the track
    public int getLength() {
        return simpleSequencer.getLength();
    }

    // Set the volume for the track
    public void setVolume(int volume) {
        simpleSynthesizer.setVolume(this.trackNumber, volume); // Update volume in the synthesizer
    }

    // Get the current volume for the track
    public int getVolume() {
        return simpleSynthesizer.getVolume(this.trackNumber); // Return the track's volume
    }

    // Set whether the track is muted
    public void setMute(boolean mute) {
        simpleSynthesizer.setMute(this.trackNumber, mute); // Mute or unmute the track in the synthesizer
    }

    // Set the instrument for the track
    public void setInstrument(int instrument) {
        simpleSynthesizer.setInstrument(this.trackNumber, instrument); // Set the instrument in the synthesizer
    }

    // Get a list of available instrument names
    public Vector<String> getInstrumentNames() {
        return new Vector<>(simpleSynthesizer.getInstrumentNames()); // Return available instrument names
    }

    // Get the sequencer associated with this track editor
    public SimpleSequencer getSequencer() {
        return simpleSequencer;
    }

    // Clear the grid and stop the sequencer
    @Override
    public void clear() {
        super.clear(); // Clear the grid canvas
        simpleSequencer.stop(); // Stop the sequencer
        simpleSequencer.clear(); // Clear events from the sequencer
    }

    // Set new events in the sequencer and update the grid
    public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
        clear(); // Clear existing events and grid cells

        // Loop through each event and add it to the grid if it's a NoteEvent
        for (int i = 0; i < newEvents.size(); i++) {
            if (newEvents.get(i) instanceof NoteEvent noteEvent) {
                addCell(noteEvent.getPitch(), noteEvent.getTime(), 1, noteEvent.getDuration()); // Add note to the grid
            }
        }
        simpleSequencer.updateSequence(newEvents); // Update the sequencer with the new events
    }

    // Paint method for the component; updates the visual representation of the track
    @Override
    public void paintComponent(Graphics g) {
        width = getWidth(); // Get the current width of the panel
        height = getHeight(); // Get the current height of the panel
        super.paintComponent(g); // Call the superclass method to handle the default painting

        // Draw the time indicator for the current position in the sequence
        int elapsedTime = (int) simpleSequencer.getElapsedTime(); // Get the elapsed time from the sequencer
        int xPosition = elapsedTime * width / simpleSequencer.getLength(); // Calculate the x-position of the indicator
        g.setColor(Color.RED); // Set the color for the time indicator (red)
        g.fillRect(xPosition, 0, 5, height); // Draw the indicator as a vertical line

        repaint(); // Trigger continuous repainting for real-time updates
    }

    // Handle mouse press events on the grid (start a note)
    @Override
    public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
        currentPitch = row; // Set the current pitch to the row where the mouse was pressed
        simpleSynthesizer.noteOn(trackNumber, row); // Start the note for the track
    }

    // Handle mouse drag events on the grid (change the note's pitch)
    @Override
    public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
        if (row != currentPitch) {
            simpleSynthesizer.noteOff(trackNumber, currentPitch); // Turn off the current note
            currentPitch = row; // Update the current pitch to the new row
            simpleSynthesizer.noteOn(trackNumber, row); // Start the new note
        }
    }

    // Handle mouse release events on the grid (end a note and add it to the sequencer)
    @Override
    public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
        if (colSpan > 0) { // Ensure the note is valid (colSpan > 0)
            // Create a new NoteEvent with the current details and add it to the sequencer
            NoteEvent noteEvent = new NoteEvent(col, "note", trackNumber, colSpan, row, simpleSynthesizer);
            simpleSequencer.add(noteEvent); // Add the note to the sequencer
            simpleSynthesizer.noteOff(trackNumber, row); // Turn off the note after it has been added
        }
    }

    // Handle the removal of a cell (event) from the grid and sequencer
    @Override
    public void onCellRemoved(int row, int col) {
        for (AudioEvent event : simpleSequencer) {
            // Check if the event is a NoteEvent and matches the row and column of the removed cell
            if (event instanceof NoteEvent noteEvent && noteEvent.getPitch() == row && noteEvent.getTime() == col) {
                simpleSequencer.remove(event); // Remove the event from the sequencer
                break;
            }
        }
    }
}
