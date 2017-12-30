package greenstory.rtg.com.classes;

import android.support.annotation.NonNull;

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
    private String trackArea;
    private HashMap<Integer,Question> questionsHashMap = new HashMap<Integer, Question>();


    public Track(String trackName, String trackArea, HashMap<Integer,Question> questionsHashMap) {
        this.trackName = trackName;
        this.trackArea = trackArea;
        this.questionsHashMap = questionsHashMap;

    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArea() { return trackArea; }

    public HashMap<Integer,Question> getQuestionsHashMap() {
        return questionsHashMap;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackName='" + trackName + '\'' +
                ", trackArea='" + trackArea + '\'' +
                '}';
    }
}

