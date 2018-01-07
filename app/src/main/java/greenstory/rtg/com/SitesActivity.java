package greenstory.rtg.com;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class SitesActivity extends AppCompatActivity {


    ScrollView scrollView;
    public LinearLayout scrollChildLayout;
    TextView trackTV;
    public Context context = this;
    public int i = 1;
    String[] tracks = {"track1","track2","track3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites);
        scrollView = (ScrollView) findViewById(R.id.site_scroller);
        scrollChildLayout = (LinearLayout) findViewById(R.id.scroll_contents);
        createTracksList(tracks);

    }

    private void createTracksList(String[] tracks){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (int i=0;i<tracks.length;i++){
            trackTV = new TextView(context);
            trackTV.setLayoutParams(layoutParams);
            trackTV.setText("Track "+String.valueOf(i));
            scrollChildLayout.addView(trackTV);
        }
    }
}