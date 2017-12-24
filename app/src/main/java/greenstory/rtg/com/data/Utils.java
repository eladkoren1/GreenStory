package greenstory.rtg.com.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import greenstory.rtg.com.classes.User;

/**
 * Created by Elad on 23/12/2017.
 */

public class Utils {

    public static void UpdateInitialScreenUser(User user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        Long lng = java.util.Calendar.getInstance().getTimeInMillis()/1000;

        //String idFromDate = String.valueOf(java.util.Calendar.getInstance().getTimeInMillis());

        cv.put("uId", lng.intValue());
        cv.put("name", user.getUserName());
        cv.put("familyName", user.getFamilyName());
        cv.put("isFamily", user.isFamily() ? 1 : 0);

        try {
            db.beginTransaction();
            db.insert("users", null,cv);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        finally{
            db.endTransaction();
        }

    }
}
