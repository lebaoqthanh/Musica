package assign11;




/**
 * @author Prof Eric Heisler and Thanh Le
 * @assignment 7: Music
 * @version 24/10/2024
 * TrackEvent class represents an event that contains a sequence of other AudioEvents,
 * like a musical track or playlist. It extends the AudioEvent class and includes additional
 * properties like duration and a sequence of AudioEvents.
 */
public class TrackEvent extends AudioEvent {
    // The duration of the track.
    private int duration;

    // The sequence of AudioEvents associated with the track.
    private SimpleSequencer sequence;

    /**
     * Constructor for creating a TrackEvent object.
     *
     * @param time The time the track starts.
     * @param trackName The name of the track (e.g., "Track1").
     * @param channel The channel associated with the event.
     * @param duration The duration of the track in milliseconds.
     * @param sequence An array of AudioEvents that make up the track sequence.
     */
    public TrackEvent(int time, String trackName, int channel, int duration, SimpleSequencer sequence) {
        super(time, trackName, channel);
        this.duration = duration;
        this.sequence = sequence;
    }

    /**
     * Gets the duration of the track.
     *
     * @return The duration of the track in milliseconds.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the sequence of AudioEvents associated with the track.
     *
     * @return An array of AudioEvents in the track.
     */
    public SimpleSequencer getSequence() {
        return sequence;
    }

    /**
     * Returns a string representation of the TrackEvent.
     * The format is: trackName[channel, time, duration, sequenceSize], followed by each event in the sequence.
     *
     * @return A string describing the TrackEvent and its sequence.
     */
    @Override
    public String toString() {
        // Construct the initial output with track details
        String out = getName() + "[" + getChannel() + ", " + getTime() + ", " + getDuration() + ", " + sequence.getLength() + "]";
        String next="";

        // Loop through the sequence array and build the string
        for (AudioEvent event : sequence) {
            next+= "\n"+ event.toString(); // Ensure each line starts with "- "
        }

        return out + next;
    }

    /**
     * Executes the event by printing out the event's details.
     */
    @Override
    public void execute() {
        sequence.start();
    }

    /**
     * Completes the event (currently not implemented).
     */
    @Override
    public void complete() {
        // Future implementation can be added here.
    }

    /**
     * Cancels the event (currently not implemented).
     */
    @Override
    public void cancel() {
        sequence.stop();
        // Future implementation can be added here.
    }

    /**
     * Compares this TrackEvent with another AudioEvent based on their time.
     * If the times are equal, it compares based on the class type (TrackEvent vs. other types).
     *
     * @param other The other AudioEvent to compare to.
     * @return -1 if this event is earlier, 1 if it is later, and 0 if they are equal in time and type.
     */
    @Override
    public int compareTo(AudioEvent other) {
        if (this.getTime() < other.getTime()) {
            return -1;
        } else if (this.getTime() > other.getTime()) {
            return 1;
        }

        // If times are equal, prioritize TrackEvent as "greater" than other types
        if (other instanceof TrackEvent) {
            return 0; // Both are TrackEvent types with the same time
        } else {
            return 1; // TrackEvent should come after NoteEvent and ChangeEvent if times are equal
        }
    }
}
