package assign11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * The SongFiles class provides methods to read and write song data, including tempo, tracks, and audio events,
 * to and from files.
 * @assign 11: Song file
 * @author Thanh Le
 * @version 3/12/2024
 */
public class SongFiles {

    /**
     * Writes the song data, including tempo, tracks, and events, to the specified file.
     *
     * @param file   The file to write the song data to.
     * @param tempo  The tempo of the song.
     * @param tracks The tracks included in the song, represented as a BetterDynamicArray of TrackPanel objects.
     * @param song   The SongPanel containing the main song data and events.
     */
    public static void writeFile(File file, int tempo, BetterDynamicArray<TrackPanel> tracks, SongPanel song) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write tempo
            writer.write(tempo + "\n");

            // Write number of tracks
            writer.write(tracks.size() + "\n");

            // Write each track block
            for (int i = 0; i < tracks.size(); i++) {
                TrackPanel track = tracks.get(i);
                writer.write("track" + i + "\n"); // Track label
                writer.write(i + "\n"); // Track number
                writer.write(track.getInstrument() + "\n");
                writer.write(track.getVolume() + "\n");
                writer.write(track.getLength() + "\n");
                writer.write(track.getSequencer().getEventCount() + "\n");

                // Write audio events for each track
                SimpleSequencer events = track.getSequencer();
                for (AudioEvent event : events) {
                    if (event instanceof TrackEvent) {
                        TrackEvent trackEvent = (TrackEvent) event;
                        writer.write("track\n");
                        writer.write(trackEvent.getName() + "\n");
                        writer.write(trackEvent.getTime() + "\n");
                        writer.write(trackEvent.getChannel() + "\n");
                        writer.write(0 + "\n");
                        writer.write(trackEvent.getDuration() + "\n");
                    } else if (event instanceof NoteEvent) {
                        NoteEvent noteEvent = (NoteEvent) event;
                        writer.write("note\n");
                        writer.write(noteEvent.getName() + "\n");
                        writer.write(noteEvent.getTime() + "\n");
                        writer.write(noteEvent.getChannel() + "\n");
                        writer.write(noteEvent.getPitch() + "\n");
                        writer.write(noteEvent.getDuration() + "\n");
                    } else if (event instanceof ChangeEvent) {
                        ChangeEvent changeEvent = (ChangeEvent) event;
                        writer.write("change\n");
                        writer.write(changeEvent.getName() + "\n");
                        writer.write(changeEvent.getTime() + "\n");
                        writer.write(changeEvent.getChannel() + "\n");
                        writer.write(changeEvent.getValue() + "\n");
                        writer.write(0 + "\n");
                    }
                }
            }

            // Write the song block
            writer.write("song\n");
            writer.write(song.getLength() + "\n");
            writer.write(song.getSequencer().getEventCount() + "\n");

            // Write events for the song
            SimpleSequencer events = song.getSequencer();
            for (AudioEvent event : events) {
                if (event instanceof TrackEvent) {
                    TrackEvent trackEvent = (TrackEvent) event;
                    writer.write("track\n");
                    writer.write(trackEvent.getName() + "\n");
                    writer.write(trackEvent.getTime() + "\n");
                    writer.write(trackEvent.getChannel() + "\n");
                    writer.write(0 + "\n");
                    writer.write(trackEvent.getDuration() + "\n");
                } else if (event instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) event;
                    writer.write("note\n");
                    writer.write(noteEvent.getName() + "\n");
                    writer.write(noteEvent.getTime() + "\n");
                    writer.write(noteEvent.getChannel() + "\n");
                    writer.write(noteEvent.getPitch() + "\n");
                    writer.write(noteEvent.getDuration() + "\n");
                } else if (event instanceof ChangeEvent) {
                    ChangeEvent changeEvent = (ChangeEvent) event;
                    writer.write("change\n");
                    writer.write(changeEvent.getName() + "\n");
                    writer.write(changeEvent.getTime() + "\n");
                    writer.write(changeEvent.getChannel() + "\n");
                    writer.write(changeEvent.getValue() + "\n");
                    writer.write(0 + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    /**
     * Reads the song data, including tempo, tracks, and events, from the specified file.
     *
     * @param file        The file to read the song data from.
     * @param synthesizer The synthesizer to link audio events to.
     * @param tracks      The BetterDynamicArray to store the tracks read from the file.
     * @param song        The SongPanel to store the main song data and events.
     * @param width       The width of the TrackPanel.
     * @param height      The height of the TrackPanel.
     * @return The tempo of the song.
     */
    public static int readFile(File file, SimpleSynthesizer synthesizer, BetterDynamicArray<TrackPanel> tracks,
                               SongPanel song, int width, int height) {
        int tempo = 0;
        try (Scanner scanner = new Scanner(file)) {
            // Read tempo
            tempo = scanner.nextInt();

            // Read number of tracks
            int trackCount = scanner.nextInt();

            // Clear existing data
            tracks.clear();
            song.clear();

            // Read each track block
            for (int i = 0; i < trackCount; i++) {
                String trackLabel = scanner.next(); // "trackX"
                int trackNumber = scanner.nextInt();
                int instrument = scanner.nextInt();
                int volume = scanner.nextInt();
                int length = scanner.nextInt();
                int numEvents = scanner.nextInt();

                TrackPanel track = new TrackPanel(width, height, trackNumber, synthesizer);
                track.setInstrument(instrument);
                track.setVolume(volume);
                track.setLength(length);
                BetterDynamicArray<AudioEvent> events = new BetterDynamicArray<>();

                // Read audio events for the track
                for (int j = 0; j < numEvents; j++) {
                    String type = scanner.next();
                    String name = scanner.next();
                    int time = scanner.nextInt();
                    int channel = scanner.nextInt();
                    int value = scanner.nextInt();
                    int duration = scanner.nextInt();
                    AudioEvent event;

                    if (type.equals("change")) {
                        event = new ChangeEvent(time, name, channel, value, synthesizer);
                    } else if (type.equals("note")) {
                        event = new NoteEvent(time, name, channel, duration, value, synthesizer);
                    } else if (type.equals("track")) {
                        event = new TrackEvent(time, name, channel, duration, tracks.get(channel).getSequencer());
                    } else {
                        throw new IllegalArgumentException("Unknown event type: " + type);
                    }

                    events.add(event);
                }
                track.setEvents(events);
                tracks.add(track);
            }

            // Read song block
            String songHeader = scanner.next(); // "song"
            int songLength = scanner.nextInt();
            song.setLength(songLength);
            int numSongEvents = scanner.nextInt();
            BetterDynamicArray<AudioEvent> songEvents = new BetterDynamicArray<>();
            for (int j = 0; j < numSongEvents; j++) {
                String type = scanner.next();
                String name = scanner.next();
                int time = scanner.nextInt();
                int channel = scanner.nextInt();
                int value = scanner.nextInt();
                int duration = scanner.nextInt();
                AudioEvent event;

                if (type.equals("change")) {
                    event = new ChangeEvent(time, name, channel, value, synthesizer);
                } else if (type.equals("note")) {
                    event = new NoteEvent(time, name, channel, duration, value, synthesizer);
                } else if (type.equals("track")) {
                    event = new TrackEvent(time, name, channel, duration, tracks.get(channel).getSequencer());
                } else {
                    throw new IllegalArgumentException("Unknown event type: " + type);
                }

                songEvents.add(event);
            }

            song.setEvents(songEvents);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        song.setTempo(tempo);

        return tempo;
    }
}
