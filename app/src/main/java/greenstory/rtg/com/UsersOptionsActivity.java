package greenstory.rtg.com;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsersOptionsActivity extends AppCompatActivity {

    String[] mobileArray = {"ניקוד: 8","כמות שאלות: 5",
            "אתרים: יפו, הר ארבל","מיקום בטבלה: 15"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_options);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_users_options_listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.list_Of_Options_To_Users);
        listView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_users_options, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_menu_item) {
            //Intent intent = new Intent(this, UserActivity.class);
            //startActivity(intent);
        }
        return true;
    }




}
