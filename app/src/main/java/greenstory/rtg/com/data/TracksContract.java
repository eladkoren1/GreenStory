package greenstory.rtg.com.data;

import android.provider.BaseColumns;

import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CLOSE_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CREATE_TABLE;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_OPEN_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT_NOT_NULL;


/**
 * Created by Elad on 20/12/2017.
 */
public class TracksContract {


    public static final class TrackEntry implements BaseColumns {

        public static final String TABLE_NAME = "tracks";
        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_TRACK_AREA = "track_area";


        public static final String SQL_CREATE_USERS_TABLE = OP_CREATE_TABLE + TABLE_NAME +
                OP_OPEN_PARENTHESIS +
                COLUMN_TRACK_NAME + TYPE_TEXT +
                COLUMN_TRACK_AREA + " TEXT " +
                OP_CLOSE_PARENTHESIS;





    }


}







