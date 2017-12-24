package greenstory.rtg.com.classes;

/**
 * Created by Elad on 23/12/2017.
 */

public class TravelSite {

    private String siteName;
    private String tracksKmlPath;
    private int id;

    public TravelSite(String siteName, String tracksKmlPath, int id) {
        this.siteName = siteName;
        this.tracksKmlPath = tracksKmlPath;
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTracksKmlPath() {
        return tracksKmlPath;
    }

    public void setTracksKmlPath(String tracksKmlPath) {
        this.tracksKmlPath = tracksKmlPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

