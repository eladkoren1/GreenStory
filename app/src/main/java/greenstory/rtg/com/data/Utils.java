package greenstory.rtg.com.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

import greenstory.rtg.com.classes.Image;
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

    public static void insertImageDataToDb(Image image, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put("image_uri", image.getUri());
        cv.put("site_name", image.getSiteName());

        try {
            db.beginTransaction();
            db.insert("images", null,cv);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        finally{
            db.endTransaction();
        }
    }

    public static HashMap<Integer,String> LoadImagesFromDB(String site, SQLiteDatabase db) {
        int i=0;
        String[] args = {site};
        HashMap<Integer, String> imagesHashMap = new HashMap<>();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query("images", null, "site_name=?", args, null, null, null);
            while (cursor.moveToNext()) {
                imagesHashMap.put(i, cursor.getString(cursor.getColumnIndex("image_uri")));
                i++;
            }
        } catch (Exception e) {

        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            cursor.close();
            db.close();
        }
        return imagesHashMap;
    }

    public static void removeImageDataFromDb(String uri, SQLiteDatabase db) {
        String[] args = {uri};

        try {
            db.beginTransaction();
            db.delete("images", "image_uri=?",args);
        }
        catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        finally{
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    public static boolean isUserIdExists(SQLiteDatabase db) {
        String[] columns = new String[1];
        columns[0] = "uId";
        Cursor cursor = db.query("users", null, null, null, null, null, null);
        cursor.moveToFirst();
        String content = null;
        try {

            content = String.valueOf(cursor.getInt(cursor.getColumnIndex("uId")));
            if (content!=null) {

                return true;
            } else {

                return false;
            }

        } catch (Exception e) {
            Log.e("Error",e.getMessage());

        }
        return false;
    }
}
