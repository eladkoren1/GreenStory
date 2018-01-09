package greenstory.rtg.com.classes;

import com.google.maps.android.data.kml.KmlLayer;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Elad on 23/12/2017.
 */

public class Site implements Serializable{

    private String siteName;
    private int kmlSource;
    private String siteData;
    //private HashMap<Integer,Track> tracks;


    public Site(String siteName, int kmlSource,String siteData) {
        this.siteName = siteName;
        this.kmlSource = kmlSource;
        this.siteData = siteData;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public int getKmlSource() {
        return this.kmlSource;
    }

    public String getSiteData() { return this.siteData;}

    @Override
    public String toString() {
        return "Site{" +
                "siteName='" + siteName + '\'' +
                ", markers=" +
                '}';
    }
}