package greenstory.rtg.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsersOptionsActivity extends AppCompatActivity {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_options);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_users_options_listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.list_Of_Options_To_Users);
        listView.setAdapter(adapter);

    }




}
