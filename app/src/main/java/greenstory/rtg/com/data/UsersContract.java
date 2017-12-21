package greenstory.rtg.com.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import greenstory.rtg.com.classes.User;

import static greenstory.rtg.com.data.GreenStoryDbHelper.*;


/**
 * Created by Elad on 20/12/2017.
 */
public class UsersContract {


    public static final class UsersEntry implements BaseColumns {

        public static final String TABLE_NAME = "users";
        public static final String COLUMN_UID = "uId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FAMILY_NAME = "familyName";
        public static final String COLUMN_PARTNER_NAME = "partnerName";
        public static final String COLUMN_USER_AGE = "userAge";
        public static final String COLUMN_PARTNER_AGE = "partnerAge";
        public static final String COLUMN_IS_FAMILY = "isFamily";

        public static final String SQL_CREATE_USERS_TABLE = OP_CREATE_TABLE + TABLE_NAME + OP_OPEN_PARENTHESIS +
                COLUMN_UID + TYPE_INTEGER_PRIMARY_KEY +
                COLUMN_NAME + TYPE_TEXT_NOT_NULL +
                COLUMN_FAMILY_NAME + TYPE_TEXT +
                COLUMN_PARTNER_NAME + TYPE_TEXT +
                COLUMN_USER_AGE + TYPE_INTEGER +
                COLUMN_PARTNER_AGE + TYPE_INTEGER +
                COLUMN_IS_FAMILY + " INTEGER "+ OP_CLOSE_PARENTHESIS;





    }


}







