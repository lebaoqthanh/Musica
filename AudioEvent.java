package assign11;

/**
 * @author Prof Eric Heisler and Thanh Le
 * @assignment 7: Music
 * @version 24/10/2024
 * Abstract class representing an audio event.
 * This class serves as the base class for all types of audio events.
 */
public abstract class AudioEvent implements Comparable<AudioEvent> {
    private int time;    // The time of the event in milliseconds
    private String name; // The name or type of the event
    private int channel; // The channel number for the event

    /**
     * Constructor to create an AudioEvent.
     * 
     * @param time    The time of the event.
     * @param name    The name of the event.
     * @param channel The channel number of the event.
     */
    public AudioEvent(int time, String name, int channel) {
        this.time = time;
        this.name = name;
        this.channel = channel;
    }

    /**
     * Gets the time of the event.
     * 
     * @return The time of the event.
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Gets the name of the event.
     * 
     * @return The name of the event.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the channel number of the event.
     * 
     * @return The channel number.
     */
    public int getChannel() {
        return this.channel;
    }

    /**
     * Executes the event. This method must be implemented by subclasses.
     */
    public abstract void execute();

    /**
     * Completes the event. This method must be implemented by subclasses.
     */
    public abstract void complete();

    /**
     * Cancels the event. This method must be implemented by subclasses.
     */
    public abstract void cancel();

  

}
