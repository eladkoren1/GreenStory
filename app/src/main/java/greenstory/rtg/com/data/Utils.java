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
        cv.put("partnerName",user.getPartnerName());
        cv.put("userAge",user.getUserAge());
        cv.put("partnerAge",user.getPartnerAge());
        cv.put("isFamily", user.isFamily() ? 1 : 0);
        cv.put("points",user.getPoints());

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

    public static User LoadUserFromDB(User user, SQLiteDatabase db) {
        Cursor cursor;
        try {
            db.beginTransaction();
            cursor = db.query("users", null, null, null, null, null, null);
            cursor.moveToFirst();
            user.setuId(String.valueOf(cursor.getInt(cursor.getColumnIndex("uId"))));
            user.setUserName(String.valueOf(cursor.getString(cursor.getColumnIndex("name"))));
            user.setFamilyName(String.valueOf(cursor.getString(cursor.getColumnIndex("familyName"))));
            user.setPartnerName(String.valueOf(cursor.getString(cursor.getColumnIndex("partnerName"))));
            user.setUserAge(cursor.getInt(cursor.getColumnIndex("userAge")));
            user.setPartnerAge(cursor.getInt(cursor.getColumnIndex("partnerAge")));
            int family = (cursor.getInt(cursor.getColumnIndex("isFamily")));

            if (family == 0) {
                user.setIsFamily(false);
            }
            if (family == 1) {
                user.setIsFamily(true);
            }

            user.setPoints(cursor.getInt(cursor.getColumnIndex("points")));
            db.setTransactionSuccessful();
            cursor.close();


        }

        catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        finally {
            db.endTransaction();
        }

        return user;
    }

    public static void UpdateUserInfo(User user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put("name", user.getUserName());
        cv.put("familyName", user.getFamilyName());
        cv.put("partnerName",user.getPartnerName());
        cv.put("userAge",user.getUserAge());
        cv.put("partnerAge",user.getPartnerAge());
        cv.put("isFamily", user.isFamily() ? 1 : 0);
        cv.put("points",user.getPoints());

        try {
            db.beginTransaction();
            Cursor cursor = db.query("users", null, null, null, null, null, null);
            cursor.moveToFirst();
            String args[] = new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("uId")))};
            db.update(UsersContract.UserEntry.TABLE_NAME,cv,"uId=?",args);
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
