package greenstory.rtg.com.data;

import android.provider.BaseColumns;

import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CLOSE_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CREATE_TABLE;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_OPEN_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT_NOT_NULL;


/**
 * Created by Elad on 20/12/2017.
 */
public class ImagesContract {


    public static final class ImageEntry implements BaseColumns {

        public static final String TABLE_NAME = "images";
        public static final String COLUMN_IMAGE_URI = "image_uri";
        public static final String COLUMN_SITE_NAME = "site_name";

        public static final String SQL_CREATE_TRAVEL_SITES_TABLE = OP_CREATE_TABLE + TABLE_NAME + OP_OPEN_PARENTHESIS +
                BaseColumns._ID + TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT +
                COLUMN_IMAGE_URI + TYPE_TEXT_NOT_NULL +
                COLUMN_SITE_NAME + " TEXT NOT NULL " +
                OP_CLOSE_PARENTHESIS;





    }


}







