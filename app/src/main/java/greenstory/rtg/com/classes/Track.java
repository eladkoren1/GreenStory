package greenstory.rtg.com.classes;

/**
 * Created by Elad on 23/12/2017.
 */

public class Track {

    private String trackName;
    private String trackArea;
    private Marker markers;

    public Track(String trackName, String trackArea, Marker markers) {
        this.trackName = trackName;
        this.trackArea = trackArea;
        this.markers = markers;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArea() { return trackArea; }

    public Marker getTracksMarkers() {
        return markers;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackName='" + trackName + '\'' +
                ", trackArea='" + trackArea + '\'' +
                '}';
    }
}

