package greenstory.rtg.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends AppCompatActivity {
    Intent trackIntent;
    Button map1Button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        map1Button = (Button) findViewById(R.id.map1);
        map1Button.setOnClickListener(buttonListener);
        trackIntent = new Intent(this,TrackActivity.class);
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            trackIntent.putExtra("MapNumber", 1);
            startActivity(trackIntent);
        }
    };

}


