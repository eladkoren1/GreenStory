package greenstory.rtg.com.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import greenstory.rtg.com.classes.TravelSite;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.UsersContract.UsersEntry;





public class GreenStoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "greenstory.db";
    public static final String OP_CREATE_TABLE = "CREATE TABLE ";
    public static final String OP_OPEN_PARENTHESIS=  " (";
    public static final String OP_CLOSE_PARENTHESIS=  "); ";
    public static final String TYPE_INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY, ";
    public static final String TYPE_INTEGER = " INTEGER, ";
    public static final String TYPE_INTEGER_NOT_NULL = " INTEGER NOT NULL, ";
    public static final String TYPE_TEXT = " TEXT, ";
    public static final String TYPE_TEXT_NOT_NULL = " TEXT NOT NULL, ";

    public String tableName = null;
    private static final int DATABASE_VERSION = 1;

    public GreenStoryDbHelper(Context context, String TableName) {
        super(context, Environment.getExternalStorageDirectory()+ File.separator+DATABASE_NAME, null, DATABASE_VERSION);
        this.tableName = TableName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.tableName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TravelSitesContract.TravelSiteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}