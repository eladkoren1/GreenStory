package greenstory.rtg.com.classes;

import android.support.annotation.NonNull;

import com.google.maps.android.data.kml.KmlLayer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Elad on 23/12/2017.
 */

public class Track implements Serializable{

    private String trackName;
    private String trackDescription;
    private String kmlSource;
    private ArrayList<Question> questionArrayList = new ArrayList<>();


    public Track(String trackName, String trackDescription, String kmlSource) {
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

    public String getKmlUrl() {
        return kmlSource;
    }

    public void setKmlUrl(String kmlUrl) {
        this.kmlSource = kmlUrl;
    }

    public String trackDescription() {
        return trackDescription;
    }

    public void setTrackArea(String trackDescription) {
        this.trackDescription = trackDescription;
    }

    public void addQuestion(Question question){
        questionArrayList.add(question);
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackName='" + trackName + '\'' +
                ", trackDescription='" + trackDescription + '\'' +
                '}';
    }
}

