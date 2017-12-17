package greenstory.rtg.com;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {


    ScrollView scrollView;
    LinearLayout scrollChildLayout;
    TextView trackTV;
    Button addText;
    public Context context = this;
    public int textViewId = 1;
    ArrayList<String> tracks = new ArrayList<>();
    public int tracksListSize = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scrollView = (ScrollView) findViewById(R.id.scroller);
        scrollChildLayout = (LinearLayout) findViewById(R.id.scroll_contents);


        addText = (Button) findViewById(R.id.add_item_button);


        addText.setOnClickListener(listener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_preferences_menu_item) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        }
        return true;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {


            if (v.getId() == addText.getId()) {
                scrollChildLayout.addView(getTextView());
                Log.d("Pressed", "onClick: ");
            }

            if (v.getId() == trackTV.getId()) {
                String uId = String.valueOf(java.util.Calendar.getInstance().getTimeInMillis());
                Toast.makeText(context,uId,Toast.LENGTH_SHORT).show();

                Log.d("Pressed", "onClick har arbel: ");
            }
        }
    };

    public TextView getTextView(){

        trackTV = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        trackTV.setLayoutParams(layoutParams);
        trackTV.setText("הר ארבל");
        trackTV.setTextSize(30);
        trackTV.setId(textViewId);
        trackTV.setOnClickListener(listener);
        textViewId++;

        return trackTV;
    }


}


