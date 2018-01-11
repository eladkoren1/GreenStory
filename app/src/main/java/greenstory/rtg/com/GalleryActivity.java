package greenstory.rtg.com;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
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
    Bitmap pictureDialog = null;
    GreenStoryDbHelper dbHelper;
    DBLoadImagesTask imageLoadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        String intentSite = getIntent().getStringExtra("site");
        imageLoadTask = new DBLoadImagesTask();
        imageLoadTask.execute(intentSite,null,null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                androidGridView = findViewById(R.id.gridview_android_example);
                androidGridView.setAdapter(new ImageAdapter(context));
                androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View v, int position, long id) {
                        Uri uri = Uri.parse(imagesHashMap.get(position));
                        try {
                            picture = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
                            showImageDialog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.run();






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
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Uri imageUri = Uri.parse(imagesHashMap.get(position));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                int thumbnailHeight = 100;
                picture = ThumbnailUtils.extractThumbnail(bitmap,((int)(thumbnailHeight*1.7)),thumbnailHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(picture);
            return imageView;
        }
    }

    private void showImageDialog(){

        Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_image, null);
        ImageView pictureView = (ImageView) mView.findViewById(R.id.iv_dialog_share);
        ImageButton shareButton = mView.findViewById(R.id.btn_share);
        dialog.setContentView(mView);
        pictureView.setImageBitmap(picture);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), picture,null, null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, bitmapUri );
                startActivity(Intent.createChooser(share, "Share using"));
            }
        });
    }

    static class DBLoadImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... stringArray) {
            //imagesHashMap = Utils.LoadImagesFromDB(stringArray[0], mDb);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}