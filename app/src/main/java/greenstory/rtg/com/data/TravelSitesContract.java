package greenstory.rtg.com.data;

import android.provider.BaseColumns;

import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CLOSE_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CREATE_TABLE;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_OPEN_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER_PRIMARY_KEY;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT_NOT_NULL;


/**
 * Created by Elad on 20/12/2017.
 */
public class TravelSitesContract {


    public static final class TravelSiteEntry implements BaseColumns {

        public static final String TABLE_NAME = "travelSites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SITE_NAME = "name";
        public static final String COLUMN_TRACKS_KML_PATH = "tracksKmlPath";


        public static final String SQL_CREATE_TRAVEL_SITES_TABLE = OP_CREATE_TABLE + TABLE_NAME + OP_OPEN_PARENTHESIS +
                COLUMN_ID + TYPE_INTEGER_PRIMARY_KEY +
                COLUMN_SITE_NAME + TYPE_TEXT_NOT_NULL +
                COLUMN_TRACKS_KML_PATH + TYPE_TEXT_NOT_NULL +
                OP_CLOSE_PARENTHESIS;





    }


}







