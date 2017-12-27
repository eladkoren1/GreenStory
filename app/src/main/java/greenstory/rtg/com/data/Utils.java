package greenstory.rtg.com.data;

import android.content.ContentValues;
import android.database.Cursor;
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

    public static void EditUserInfo(User user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();


        cv.put("name", user.getUserName());
        cv.put("familyName", user.getFamilyName());
        cv.put("partnerName",user.getPartnerName());
        cv.put("userAge",user.getUserAge());
        cv.put("partnerAge",user.getPartnerAge());
        cv.put("isFamily", user.isFamily() ? 1 : 0);

        try {
            db.beginTransaction();
            Cursor cursor = db.query("users", null, null, null, null, null, null);
            cursor.moveToFirst();
            String args[] = new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("uId")))};
            db.update(UsersContract.UsersEntry.TABLE_NAME,cv,"uId=?",args);
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
