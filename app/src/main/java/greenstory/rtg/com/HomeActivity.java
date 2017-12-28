package greenstory.rtg.com;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;


public class HomeActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    ScrollView scrollView;
    LinearLayout scrollChildLayout;
    TextView trackTV;
    Button addText;
    public Context context = this;
    public int textViewId = 1;
    ArrayList<String> tracks = new ArrayList<>();
    EditText mUserName;
    EditText mFamilyName;
    CheckBox mIsFamily;
    Button resetDB;
    Button goToMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermissions();

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this, UsersContract.UsersEntry.SQL_CREATE_USERS_TABLE);
        mDb = dbHelper.getWritableDatabase();
        resetDB = (Button)findViewById(R.id.btn_delete_db);

        scrollView = (ScrollView) findViewById(R.id.scroller);
        scrollChildLayout = (LinearLayout) findViewById(R.id.scroll_contents);

        goToMap = (Button) findViewById(R.id.goToMaps);
        goToMap.setOnClickListener(listener);

        if (!isUserIdExists(mDb)){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
            mUserName = mView.findViewById(R.id.etUserName);
            mFamilyName = mView.findViewById(R.id.etFamilyName);
            mIsFamily = mView.findViewById(R.id.cbIsFamily);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mUserName.getText().toString().isEmpty() && !mFamilyName.getText().toString().isEmpty()){
                        Toast.makeText(context,
                                 "ברוך הבא " + mUserName.getText(),
                                Toast.LENGTH_SHORT).show();
                                User user = new User(String.valueOf(mUserName.getText()),String.valueOf(mFamilyName.getText()),mIsFamily.isChecked());
                                new DBUserRegisterTask().execute(user,null,null);
                        dialog.dismiss();
                    }
                    else{
                        Toast.makeText(context,
                                "השלם שדות חסרים",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
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

            if (v.getId() == goToMap.getId()) {
                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);
            }

            if (v.getId()==resetDB.getId()) {
                mDb.delete("users",null,null);
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
}


