package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Elad on 23/12/2017.
 */

public class Site implements Serializable {

    private String siteName;
    private String siteDescription;
    private LatLng latLng;
    ArrayList<Track> tracks = null;
    private MarkerOptions siteHomeMarker;

    public Site(String siteName,String siteDescription) {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        tracks = new ArrayList<Track>();
    }

    public Site(String siteName,String siteDescription, LatLng latLng) {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latLng = latLng;
        tracks = new ArrayList<Track>();
    }

    public Site(String siteName, String siteDescription, LatLng latLng, MarkerOptions siteHomeMarkerOptions) {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latLng = latLng;
        this.siteHomeMarker = siteHomeMarkerOptions;
        tracks = new ArrayList<Track>();
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public ArrayList<Track> getTracks() {
        return (ArrayList<Track>) tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public MarkerOptions getSiteHomeMarker() {
        return siteHomeMarker;
    }

    private void setSiteHomeMarker(MarkerOptions siteHomeMarker) {
        this.siteHomeMarker = siteHomeMarker;
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteName='" + siteName + '\'' +
                ", siteDescription='" + siteDescription + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}