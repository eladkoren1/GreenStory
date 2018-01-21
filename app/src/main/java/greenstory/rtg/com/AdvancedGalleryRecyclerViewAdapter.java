package greenstory.rtg.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

import greenstory.rtg.com.classes.Image;

/**
 * Created by Elad on 11/01/2018.
 */

public class AdvancedGalleryRecyclerViewAdapter extends RecyclerView.Adapter<AdvancedGalleryRecyclerViewAdapter.ViewHolder> {

    private String[] mData = new String[0];
    private HashMap<Integer,Bitmap> integerBitmapHashMap = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Bitmap imageToLoad;

    // data is passed into the constructor
    AdvancedGalleryRecyclerViewAdapter(Context context, HashMap<Integer,Bitmap> integerBitmapHashMap) {
        this.mInflater = LayoutInflater.from(context);
        this.integerBitmapHashMap = integerBitmapHashMap;

    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.advanced_gallery_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the imageview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        imageToLoad = integerBitmapHashMap.get(position);
        holder.imageView.setImageBitmap(imageToLoad);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return integerBitmapHashMap.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            //myTextView = (TextView) itemView.findViewById(R.id.info_text);
            imageView = (ImageView) itemView.findViewById(R.id.iv_gallery_picture);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return null;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}