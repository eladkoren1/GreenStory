package greenstory.rtg.com;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_gallery);
        dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        String intentSite = getIntent().getStringExtra("site");
        imageLoadTask = new DBLoadImagesTask();
        imageLoadTask.execute(intentSite,null,null);

        for (int i=0;i<imagesHashMap.size();i++) {
            Uri uri = Uri.parse(imagesHashMap.get(i));
            try {
                picture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            integerBitmapHashMap.put(i, picture);
        }
        Log.d("HashMap Size", String.valueOf(integerBitmapHashMap.size()));
        // data to populate the RecyclerView with
        //String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = 6;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new AdvancedGalleryRecyclerViewAdapter(this, integerBitmapHashMap);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
    }


    private void showImageDialog(){

        Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_image, null);
        ImageView pictureView = (ImageView) mView.findViewById(R.id.iv_dialog_share);
        ImageButton shareButton = mView.findViewById(R.id.btn_share);
        dialog.setContentView(mView);
        //pictureView.setImageBitmap(picture);
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
            super.onPostExecute(aVoid);
        }
    }

}