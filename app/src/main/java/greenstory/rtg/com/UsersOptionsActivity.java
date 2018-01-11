package greenstory.rtg.com;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ListView siteInfo;
    String[] sitesArray;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView titleTextView;
    ImageButton drawerImageButton;
    Button editUserButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_options);

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        new DBLoadUserTask().execute(user,null,null);

        editUserButton = (Button)findViewById(R.id.btn_edit_user);
        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserEditActivity.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });
        Resources res = getResources();
        sitesArray = res.getStringArray(R.array.sites_array);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        getSupportActionBar().setTitle("Green Story");
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        titleTextView = findViewById(R.id.tv_action_title_bar);
        titleTextView.setText("עריכת פרטים");
        drawerImageButton = (ImageButton) findViewById(R.id.ib_open_drawer);
        drawerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.openDrawer(Gravity.LEFT,true);
                }
                if (mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.closeDrawer(Gravity.LEFT,true);
                }
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.users_drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle (
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer_white,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerList = (ListView) findViewById(R.id.users_left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, res.getStringArray(R.array.options_array)));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*if (item.getItemId() == R.id.edit_menu_item) {

    }*/
       // return true;
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

    class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position, true);
            if (position == 0) {
                Intent intent = new Intent(context, UsersOptionsActivity.class);
                startActivity(intent);
            }
            if (position == 2) {
                //showAttractionsDialog();
            }

            if (position == 3) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context,AdvancedGalleryActivity.class);
                        intent.putExtra("site","תוצרת הארץ");
                        startActivity(intent);
                    }
                });
                thread.run();
            }
        }
    }
}
