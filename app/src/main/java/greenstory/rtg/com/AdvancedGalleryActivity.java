package greenstory.rtg.com;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.Utils;

public class AdvancedGalleryActivity extends AppCompatActivity implements AdvancedGalleryRecyclerViewAdapter.ItemClickListener {
    HashMap<Integer,Bitmap> integerBitmapHashMap = new HashMap<>();
    AdvancedGalleryRecyclerViewAdapter adapter;
    DBLoadImagesTask imageLoadTask;
    HashMap<Integer,String> imagesHashMap = new HashMap<>();
    GreenStoryDbHelper dbHelper;
    SQLiteDatabase mDb;
    Bitmap picture = null;
    Bitmap pictureThumbnail = null;
    boolean isLoadFinished=false;
    Context context = this;
    private TextView titleTextView;
    String bitmapPath;
    String intentSite;
    Bitmap dialogPicture;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.rvNumbers);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_gallery);
        dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_no_burger);
        titleTextView = findViewById(R.id.tv_action_title_bar);
        titleTextView.setText("גלריה");

        intentSite = getIntent().getStringExtra("site");
        imageLoadTask = new DBLoadImagesTask();
        imageLoadTask.execute(intentSite,null,null);



        // data to populate the RecyclerView with
        //String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        // set up the RecyclerView


    }

    @Override
    public void onItemClick(View view, int position) {
        showImageDialog(position);
    }


    private void showImageDialog(final Integer position){

        final Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = getLayoutInflater().inflate(R.layout.activity_gallery_dialog_image, null);
        ImageView pictureView = mView.findViewById(R.id.iv_dialog_show_image);
        ImageButton shareButton = mView.findViewById(R.id.btn_share_gallery);
        ImageButton deleteButton = mView.findViewById(R.id.btn_delete);
        dialog.setContentView(mView);
        Uri uri = Uri.parse(imagesHashMap.get(position));
        dialogPicture=null;
        try {
            dialogPicture = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
        pictureView.setImageBitmap(dialogPicture);
        System.gc();
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        final Bitmap finalDialogPicture = dialogPicture;
        bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), finalDialogPicture,null, null);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri bitmapUri = Uri.parse(bitmapPath);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, bitmapUri );
                startActivity(Intent.createChooser(share, "Share using"));

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBDeleteImagesTask().execute(imagesHashMap.get(position));
                recyclerView.invalidate();
                dialog.dismiss();
            }
        });
       doKeepDialog(dialog);
    }

    class DBLoadImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... stringArray) {
            imagesHashMap = Utils.LoadImagesFromDB(stringArray[0], mDb);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    integerBitmapHashMap = new HashMap<>();
                    for (int i=0;i<imagesHashMap.size();i++) {

                        try {
                            Uri uri = Uri.parse(imagesHashMap.get(i));
                            //picture = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            //int thumbnailHeight = 100;
                            //pictureThumbnail = ThumbnailUtils.extractThumbnail(picture,((int)(thumbnailHeight*1.7)),thumbnailHeight);
                            final int THUMBSIZE = 160;
                            File finalFile = new File(getRealPathFromURI(uri));
                            pictureThumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(String.valueOf(finalFile)),
                                    THUMBSIZE, THUMBSIZE);
                            integerBitmapHashMap.put(i,pictureThumbnail);
                            pictureThumbnail=null;
                            System.gc();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    Log.d("HashMap Size", String.valueOf(integerBitmapHashMap.size()));
                    recyclerView = findViewById(R.id.rvNumbers);
                    int numberOfColumns = 2;
                    adapter = new AdvancedGalleryRecyclerViewAdapter(context, integerBitmapHashMap);
                    adapter.setClickListener((AdvancedGalleryRecyclerViewAdapter.ItemClickListener) context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

                }
            });
            thread.run();

            super.onPostExecute(aVoid);
        }
    }

    class DBDeleteImagesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... stringArray) {
            dbHelper = new GreenStoryDbHelper(context);
            mDb = dbHelper.getWritableDatabase();
           // TODO: Utils.removeImageDataFromDb(stringArray[0], mDb);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            dbHelper = new GreenStoryDbHelper(context);
            mDb = dbHelper.getWritableDatabase();
            new DBLoadImagesTask().execute(intentSite);
            super.onPostExecute(aVoid);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

}