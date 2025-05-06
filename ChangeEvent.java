package assign11;



/**
 * @author Prof Eric Heisler and Thanh Le
 * @assignment 7: Music
 * @version 24/10/2024
 * ChangeEvent class represents an event that changes a parameter, such as a pitch bend or modulation.
 * It extends the AudioEvent class and adds a value to represent the change.
 */
public class ChangeEvent extends AudioEvent {
    // The value of the change.
    private int change;
    private SimpleSynthesizer synth;

    /**
     * Constructor for creating a ChangeEvent object.
     *
     * @param time The time of the event.
     * @param type The type of event.
     * @param channel The channel associated with the event.
     * @param value The value representing the change (e.g., pitch bend value).
     */
    public ChangeEvent(int time, String type, int channel, int value, SimpleSynthesizer synth) {
        super(time, type, channel);
        this.change = value;
        this.synth= synth;
    }

    /**
     * Gets the value of the change.
     *
     * @return The value of the change.
     */
    public int getValue() {
        return change;
    }

    /**
     * Returns a string representation of the ChangeEvent.
     * The format is: type[channel, time, value]
     *
     * @return A string describing the ChangeEvent.
     */
    @Override
    public String toString() {
        return getName() + "[" + getChannel() + ", " + getTime() + ", " + getValue() + "]";
    }

    /**
     * Executes the event by printing out the event's details.
     */
    @Override
    public void execute() {
        System.out.println(toString());
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
        // Future implementation can be added here.
    }

    /**
     * Compares this ChangeEvent with another AudioEvent based on their time.
     * If the times are equal, it compares based on the class type (ChangeEvent vs. other types).
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
        if (other instanceof ChangeEvent) {
            return 0;
        } else
            return -1;

    }

}

