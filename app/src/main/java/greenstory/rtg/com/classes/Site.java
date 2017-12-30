package greenstory.rtg.com.classes;

import java.util.HashMap;

/**
 * Created by Elad on 23/12/2017.
 */

public class Site {

    private String siteName;
    private String siteKmlResId;
    private HashMap<Integer,Track> tracks;

    public Site(String siteName, String siteKmlResId,HashMap<Integer,Track> tracks) {
        this.siteName = siteName;
        this.siteKmlResId = siteKmlResId;
        this.tracks = tracks;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteKmlResId() {
        return siteKmlResId;
    }

    public HashMap<Integer, Track> getTrack() {
        return tracks;
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteName='" + siteName + '\'' +
                ", markers=" +
                '}';
    }
}