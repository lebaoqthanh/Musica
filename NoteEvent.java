package assign11;



/**
 * @author Prof Eric Heisler and Thanh Le
 * @assignment 7: Music
 * @version 24/10/2024
 * NoteEvent class represents a musical note played on an instrument.
 * It extends the AudioEvent class and includes additional properties like duration and pitch.
 */
public class NoteEvent extends AudioEvent {
    // The duration of the note in milliseconds.
    private int duration;

    // The pitch of the note.
    private int pitch;
    private SimpleSynthesizer synth;

    /**
     * Constructor for creating a NoteEvent object.
     *
     * @param time The time the note starts.
     * @param instrument The name of the instrument.
     * @param channel The channel associated with the event.
     * @param duration The duration of the note in milliseconds.
     * @param pitch The pitch of the note.
     */
    public NoteEvent(int time, String instrument, int channel, int duration, int pitch, SimpleSynthesizer synth) {
        super(time, instrument, channel);
        this.duration = duration;
        this.pitch = pitch;
        this.synth = synth;
    }

    /**
     * Gets the duration of the note.
     *
     * @return The duration of the note in milliseconds.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the pitch of the note.
     *
     * @return The pitch of the note.
     */
    public int getPitch() {
        return pitch;
    }

    /**
     * Returns a string representation of the NoteEvent.
     * The format is: instrument[channel, time, duration, pitch]
     *
     * @return A string describing the NoteEvent.
     */
    @Override
    public String toString() {
        return getName() + "[" + getChannel() + ", " + getTime() + ", " + getDuration() + ", " + getPitch() + "]";
    }

    /**
     * Executes the event by printing out the event's details.
     */
    @Override
    public void execute() {
        synth.noteOn(getChannel(),pitch);
    }

    /**
     * Completes the event (currently not implemented).
     */
    @Override
    public void complete() {
        synth.noteOff(getChannel(),pitch);
        // Future implementation can be added here.
    }

    /**
     * Cancels the event (currently not implemented).
     */
    @Override
    public void cancel() {
        synth.noteOff(getChannel(),pitch);
        // Future implementation can be added here.
    }

    /**
     * Compares this NoteEvent with another AudioEvent based on their time.
     * If the times are equal, it compares based on the class type (NoteEvent vs. other types).
     *
     * @param other The other AudioEvent to compare to.
     * @return -1 if this event is earlier, 1 if it is later, and 0 if they are equal in time and type.
     */
    @Override
    public int compareTo(AudioEvent other) {
        if (this.getTime() < other.getTime())
            return -1;
        else if (this.getTime() > other.getTime())
            return 1;
        if (other instanceof NoteEvent) {
            return 0;
        } else if (other instanceof TrackEvent) {
            return -1;
        } else
            return 1;

    }
}
