package greenstory.rtg.com;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

public class UsersOptionsActivity extends AppCompatActivity {

    User user = new User();
    String family = "משפחת ";
    private SQLiteDatabase mDb;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_options);

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        new DBLoadUserTask().execute(user,null,null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new DBLoadUserTask().execute(user,null,null);

    }

    public void updateLayout(){
        String[] mobileArray = {"ניקוד: "+user.getPoints(),
                "אתרים: יפו, הר ארבל","מיקום בטבלה: 15"};
        TextView familyNameDisplayTV = (TextView) findViewById(R.id.tv_family_name_display);
        familyNameDisplayTV.setText(family+user.getFamilyName());

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
            Intent intent = new Intent(context,UserEditActivity.class);
            intent.putExtra("User",user);
            startActivity(intent);
        }
        return true;
    }
    public class DBLoadUserTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... userArray) {

            user = Utils.LoadUserFromDB(userArray[0], mDb);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateLayout();

        }
    }
}
