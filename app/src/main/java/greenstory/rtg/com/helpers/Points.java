package greenstory.rtg.com.helpers;

/**
 * Created by Elad on 18/12/2017.
 */

public class Points {

    private String trackName;
    private int totalPoints;

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public Points() {

        this.trackName = null;
        this.totalPoints = 0;
    }
}
