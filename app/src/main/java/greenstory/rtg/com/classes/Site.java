package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlLayer;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Elad on 23/12/2017.
 */

public class Site implements Serializable {

    private String siteName;
    private String siteDescription;
    private LatLng latLng;
    private HashMap<Integer,Track> tracks;

    public Site(String siteName,String siteDescription) {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        }

    public Site(String siteName,String siteDescription, LatLng latLng) {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteName='" + siteName + '\'' +
                ", latLng=" + latLng +
                ", siteData='" + siteDescription + '\'' +
                ", tracks=" + tracks +
                '}';
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getSiteData() {
        return siteDescription;
    }

    public void setSiteData(String siteData) {
        this.siteDescription = siteData;
    }

    public HashMap<Integer, Track> getTracks() {
        return tracks;
    }

    public void setTracks(HashMap<Integer, Track> tracks) {
        this.tracks = tracks;
    }
}