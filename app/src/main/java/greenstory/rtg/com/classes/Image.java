package greenstory.rtg.com.classes;

import android.net.Uri;

/**
 * Created by Elad on 10/01/2018.
 */

public class Image {

    private String siteName;
    private String uri = null;

    public Image (String siteName, String uri){
        this.siteName = siteName;
        this.uri = uri;
    }

    public String getSiteName(){return this.siteName;}

    public String getUri(){return this.uri;}

}
