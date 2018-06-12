package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Elad on 29/12/2017.
 */

public class Marker {

    private int id;
    private String markerName=null;
    private Question question;

    public Marker(int id, String markerName, LatLng latLng) {
        this.id = id;
        this.markerName = markerName;


    }

    public int getId() {
        return id;
    }

    public String getMarkerName() {
        return markerName;
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "id=" + id +
                ", markerName='" + markerName + '\'' +
                ",Question=" + question +
                '}';
    }
}
