package greenstory.rtg.com;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class HomeActivityTwo extends AppCompatActivity {


    public Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_two);
        TextView tv = (TextView) this.findViewById(R.id.RTG_info);
        tv.setSelected(true);  // Set focus to the textview
    }

}