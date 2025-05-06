package assign11;
/**@author Thanh Le
 * @assign 10 GridCanvas
 * @version 11-22-2024
 * */



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class SongEditor extends GridCanvas {
    private SimpleSequencer simpleSequencer;  // Sequencer for handling the timing of events
    private BetterDynamicArray<TrackPanel> trackPanels;  // List of TrackPanels representing each track
    private int width;  // Width of the grid canvas
    private int height;  // Height of the grid canvas
    private int currentTrack;  // Index of the currently selected track

    public SongEditor(int width, int height) {
        super(width, height, 1, 4, 10, 1); // Initial grid size (adjustable as needed)
        this.width = width;
        this.height = height;
        this.trackPanels = new BetterDynamicArray<>();  // Initialize the track panel list
        this.currentTrack = 0;  // Default to the first track
        setPreferredSize(new Dimension(width, height));  // Set the preferred size for the component
        // Initialize SimpleSequencer with a default length
        this.simpleSequencer = new SimpleSequencer(4);

        // Add mouse listeners for interaction (detecting mouse events on the grid)
        addMouseListener(this);
        addMouseMotionListener(this);

        // Ensure we don't divide by zero by initializing the grid with at least one track
        if (trackPanels.size() == 0) {
            // Add a temporary TrackPanel until it is properly initialized
            trackPanels.add(new TrackPanel(this.width, this.height, 0, new SimpleSynthesizer()));
        }
    }

    // Set the length of the sequencer and adjust the grid columns accordingly
    public void setLength(int length) {
        simpleSequencer.setLength(length);
        setColumns(length);  // Adjust grid columns based on length
    }

    // Get the current length of the sequencer
    public int getLength() {
        return simpleSequencer.getLength();
    }

    // Get the SimpleSequencer instance associated with this SongEditor
    public SimpleSequencer getSequencer() {
        return simpleSequencer;
    }

    // Override the clear method to clear the grid and stop the sequencer
    @Override
    public void clear() {
        super.clear();  // Clear the grid canvas
        simpleSequencer.stop();  // Stop the sequencer
        simpleSequencer.clear();  // Clear events from the sequencer
    }

    // Set the new events in the sequencer and update the grid
    public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
        clear();  // Clear existing events in the grid and sequencer
        // Iterate over the events and add corresponding TrackEvents to the grid
        for (int i = 0; i < newEvents.size(); i++) {
            if (newEvents.get(i) instanceof TrackEvent trackEvent) {
                // Add TrackEvent with track number (row) as channel
                addCell(trackEvent.getChannel(), trackEvent.getTime(), 1, trackEvent.getDuration());
            }
        }
        simpleSequencer.updateSequence(newEvents);  // Update the sequencer with the new events
    }

    // Set the list of TrackPanels in the SongEditor
    public void setTrackList(BetterDynamicArray<TrackPanel> trackList) {
        this.trackPanels = trackList;
    }

    // Override paintComponent to draw the grid and the time indicator
    @Override
    public void paintComponent(Graphics g) {
        setRows(trackPanels.size());  // Set the number of rows based on the number of track panels
        width = getWidth();  // Update the width of the component
        height = getHeight();  // Update the height of the component
        super.paintComponent(g);  // Call the superclass method to paint the grid

        // Draw the time indicator (same as in TrackEditor)
        int elapsedTime = (int) simpleSequencer.getElapsedTime();  // Get the elapsed time from the sequencer
        int xPosition = (int)(elapsedTime * (double) width / simpleSequencer.getLength());  // Calculate x position for the indicator
        g.setColor(Color.RED);  // Set the color for the time indicator
        g.fillRect(xPosition, 0, 5, height);

        // Draw the vertical time indicator

        repaint();  // Continuously repaint for real-time updates
    }

    // Handle mouse press on a grid cell (start of interaction)
    @Override
    public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
        currentTrack = row;  // Set current track based on row
        // Set the restrictions for the track's length based on its sequencer
        setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
    }

    // Handle mouse drag on a grid cell (move interaction)
    @Override
    public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
        if (row != currentTrack) {
            currentTrack = row;  // Update the current track when dragged to a different row
            // Update the restrictions for the new track
            setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
        }
    }

    // Handle mouse release on a grid cell (end of interaction)
    @Override
    public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
        if (colSpan > 0) {  // Ensure valid note duration
            // Create a new TrackEvent and add it to the sequencer
            TrackEvent trackEvent = new TrackEvent(col, "TrackEvent", currentTrack, colSpan,
                    trackPanels.get(currentTrack).getSequencer());
            simpleSequencer.add(trackEvent);  // Add TrackEvent to sequencer
        }
    }

    // Handle removal of a cell (remove corresponding event)
    @Override
    public void onCellRemoved(int row, int col) {
        // Iterate over the sequencer's events to find and remove matching TrackEvent
        for (AudioEvent event : simpleSequencer) {
            if (event instanceof TrackEvent trackEvent && trackEvent.getChannel() == row
                    && trackEvent.getTime() == col) {
                simpleSequencer.remove(event);  // Remove matching TrackEvent from sequencer
                break;
            }
        }
    }
}
