package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Elad on 29/12/2017.
 */

public class Marker {

    private int id;
    private String markerName=null;
    private LatLng latLng;
    private Question question;

    public Marker(int id, String markerName, LatLng latLng) {
        this.id = id;
        this.markerName = markerName;
        this.latLng=latLng;
    }

    public int getId() {
        return id;
    }

    public String getMarkerName() {
        return markerName;
    }

    public LatLng getLatLng() {
        return latLng;
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
                ", LatLng" + latLng +
                ",Question=" + question +
                '}';
    }
}
