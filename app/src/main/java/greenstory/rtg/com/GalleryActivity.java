package greenstory.rtg.com;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.Utils;

public class GalleryActivity extends AppCompatActivity {

    GridView androidGridView;
    SQLiteDatabase mDb;
    HashMap<Integer,String> imagesHashMap = new HashMap<>();
    Context context = this;
    Bitmap picture = null;
    GreenStoryDbHelper dbHelper;
    DBLoadImagesTask imageLoadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        dbHelper = new GreenStoryDbHelper(this);
        try{
            mDb = dbHelper.getReadableDatabase();
        } catch (SQLiteDatabaseLockedException e){
            Log.d("Locked","locked");
        } catch (java.lang.RuntimeException e){
            Log.d("locked","locked");
        }


        androidGridView = findViewById(R.id.gridview_android_example);
        androidGridView.setAdapter(new ImageAdapter(context));
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
            }
        });

        String intentSite = getIntent().getStringExtra("site");
        imageLoadTask = new DBLoadImagesTask();
        imageLoadTask.execute(intentSite,null,null);


    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imagesHashMap.size();

        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Uri imageUri = Uri.parse(imagesHashMap.get(position));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                picture = ThumbnailUtils.extractThumbnail(bitmap,144,144);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(picture);
            return imageView;
        }
    }

    class DBLoadImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... stringArray) {
            imagesHashMap = Utils.LoadImagesFromDB(stringArray[0], mDb);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}