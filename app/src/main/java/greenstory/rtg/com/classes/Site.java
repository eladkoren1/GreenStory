package greenstory.rtg.com.classes;

/**
 * Created by Elad on 23/12/2017.
 */

public class Site {

    private String siteName;
    private String siteKmlResId;
    private Track tracks;

    public Site(String siteName, String siteKmlResId,Track tracks) {
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

    public Track getTrack() {
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