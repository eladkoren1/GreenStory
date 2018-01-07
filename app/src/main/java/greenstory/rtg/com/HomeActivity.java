package greenstory.rtg.com;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;


public class HomeActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    public Context context = this;
    ArrayList<String> tracks = new ArrayList<>();

    ListView usersListView;
    EditText mUserName;
    EditText mFamilyName;
    CheckBox mIsFamily;
    User user = new User();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    String[] homeScreenOptionsArray = {"משתמש", "מסלולים", "מפה", "משתתפים", "אודות", "צור קשר", "חנות"};
    String[] usersAndPointsArray =
            {"אלעד: 8", "משה: 20", "יוסי: 15", "דני: 16", "קרן: 17", "אבי:25", "הדר: 200", "עדי: 1000"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_two);

        checkPermissions(user);

        //users list
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.users_list_item, usersAndPointsArray);
        usersListView = (ListView) findViewById(R.id.lv_users);
        usersListView.setAdapter(adapter);

        //drawer things
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, homeScreenOptionsArray));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    public void initiateDB(final User user) {
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this,
                UsersContract.UserEntry.SQL_CREATE_USERS_TABLE);
        mDb = dbHelper.getWritableDatabase();
        if (!isUserIdExists(mDb)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.activity_home_dialog_login, null);
            Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
            mUserName = mView.findViewById(R.id.etUserName);
            mFamilyName = mView.findViewById(R.id.etFamilyName);
            mIsFamily = mView.findViewById(R.id.cbIsFamily);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mUserName.getText().toString().isEmpty() && !mFamilyName.getText().toString().isEmpty()) {
                        Toast.makeText(context, "ברוך הבא " + mUserName.getText(), Toast.LENGTH_SHORT).show();
                        user.setUserName(String.valueOf(mUserName.getText()));
                        user.setFamilyName(String.valueOf(mFamilyName.getText()));
                        user.setPartnerName("none");
                        user.setUserAge(0);
                        user.setPartnerAge(0);
                        user.setIsFamily(mIsFamily.isChecked());
                        user.setPoints(0);
                        new DBUserRegisterTask().execute(user, null, null);
                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(context, "השלם שדות חסרים", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean isUserIdExists(SQLiteDatabase db) {
        String[] columns = new String[1];
        columns[0] = "uId";
        Cursor cursor = db.query("users", null, null, null, null, null, null);
        cursor.moveToFirst();
        String content = null;
        try {

            content = String.valueOf(cursor.getInt(cursor.getColumnIndex("uId")));
            if (content!=null) {

                return true;
            } else {

                return false;
            }

        } catch (Exception e) {
            Log.e("Error",e.getMessage());

        }
        return false;
    }

    public class DBUserRegisterTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... user) {

            try {
                Utils.UpdateInitialScreenUser(user[0], mDb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context,
                    "פעולת רישום משתמש הושלמה בהצלחה",
                    Toast.LENGTH_SHORT).show();

        }
    }

    private void checkPermissions(User user) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
        }
        else {
            initiateDB(user);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initiateDB(user);

                } else {
                        Toast.makeText(context,"Can't work without permissions :(",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position, true);
            if (position==0){
                Intent intent = new Intent(context, UsersOptionsActivity.class);
                startActivity(intent);
            }
            if (position==2){
                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);
            }
        }
    }
}