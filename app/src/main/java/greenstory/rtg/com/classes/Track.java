package greenstory.rtg.com.classes;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Elad on 23/12/2017.
 */

public class Track {

    private String trackName;
    private String trackDescription;
    private int kmlSource;
    private HashMap<Integer,Question> questionsHashMap = new HashMap<Integer, Question>();


    public Track(String trackName, String trackDescription, int kmlSource) {
        this.trackName = trackName;
        this.trackDescription = trackDescription;
        this.kmlSource = kmlSource;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String trackDescription() {
        return trackDescription;
    }

    public void setTrackArea(String trackDescription) {
        this.trackDescription = trackDescription;
    }

    public int getKmlSource() {
        return kmlSource;
    }

    public void setKmlSource(int kmlSource) {
        this.kmlSource = kmlSource;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackName='" + trackName + '\'' +
                ", trackDescription='" + trackDescription + '\'' +
                '}';
    }
}

