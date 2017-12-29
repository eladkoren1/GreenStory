package greenstory.rtg.com.data;

import android.provider.BaseColumns;

import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CLOSE_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CREATE_TABLE;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_OPEN_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER_PRIMARY_KEY;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_REAL;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT_NOT_NULL;


/**
 * Created by Elad on 20/12/2017.
 */
public class MarkersContract {


    public static final class MarkerEntry implements BaseColumns {

        public static final String TABLE_NAME = "markers";
        public static final String COLUMN_MARKER_NAME = "marker_name";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";



        public static final String SQL_CREATE_MARKERS_TABLE = OP_CREATE_TABLE + TABLE_NAME + OP_OPEN_PARENTHESIS +
                BaseColumns._ID + TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT +
                COLUMN_MARKER_NAME + TYPE_TEXT_NOT_NULL +
                COLUMN_LATITUDE + TYPE_REAL +
                COLUMN_LONGITUDE + " REAL " +
                OP_CLOSE_PARENTHESIS;





    }


}







