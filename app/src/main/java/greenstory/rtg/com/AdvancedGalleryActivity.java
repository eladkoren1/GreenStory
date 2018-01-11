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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_gallery);
        dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        String intentSite = getIntent().getStringExtra("site");
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


    private void showImageDialog(Integer position){

        Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_image, null);
        ImageView pictureView = (ImageView) mView.findViewById(R.id.iv_dialog_share);
        ImageButton shareButton = mView.findViewById(R.id.btn_share);
        dialog.setContentView(mView);
        Uri uri = Uri.parse(imagesHashMap.get(position));
        Bitmap dialogPicture=null;
        try {
            dialogPicture = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pictureView.setImageBitmap(dialogPicture);
        dialogPicture=null;
        System.gc();
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                //String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), picture,null, null);
                //Uri bitmapUri = Uri.parse(bitmapPath);
                share.setType("image/jpg");
                //share.putExtra(Intent.EXTRA_STREAM, bitmapUri );
                startActivity(Intent.createChooser(share, "Share using"));
            }
        });
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
                    for (int i=0;i<imagesHashMap.size();i++) {

                        try {
                            System.gc();
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
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
                    int numberOfColumns = 2;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
                    adapter = new AdvancedGalleryRecyclerViewAdapter(context, integerBitmapHashMap);
                    adapter.setClickListener((AdvancedGalleryRecyclerViewAdapter.ItemClickListener) context);
                    recyclerView.setAdapter(adapter);
                }
            });
            thread.run();

            super.onPostExecute(aVoid);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}