package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Elad on 29/12/2017.
 */

public class Marker {

    private int id;
    private String markerName;
    private double pointLatitude;
    private double pointLongitude;

    public Marker(int id, String markerName, double pointLatitude, double pointLongitude) {
        this.id = id;
        markerName = markerName;
        this.pointLatitude = pointLatitude;
        this.pointLongitude = pointLongitude;
    }

    public int getId() {
        return id;
    }

    public String getMarkerName() {
        return markerName;
    }

    public double getPointLatitude() {
        return pointLatitude;
    }

    public double getPointLongitude() {
        return pointLongitude;

    }

    @Override
    public String toString() {
        return "Marker{" +
                "id=" + id +
                ", markerName='" + markerName + '\'' +
                ", pointLatitude=" + pointLatitude +
                ", pointLongitude=" + pointLongitude +
                '}';
    }
}
