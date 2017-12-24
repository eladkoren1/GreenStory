package greenstory.rtg.com;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
    Button updateDB;
    Button goToMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("unsigned int",String.valueOf(java.util.Calendar.getInstance().getTimeInMillis()));

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        scrollView = (ScrollView) findViewById(R.id.scroller);
        scrollChildLayout = (LinearLayout) findViewById(R.id.scroll_contents);
        addText = (Button) findViewById(R.id.add_item_button);
        addText.setOnClickListener(listener);
        updateDB = (Button) findViewById(R.id.update_db_button);
        updateDB.setOnClickListener(listener);
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
                                new DBUpdateTask().execute(null,null);
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
                mDb.delete("users",null,null);
                Log.d("Pressed", "onClick: ");
            }

            if (v.getId() == goToMap.getId()) {
                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);
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

    public class DBUpdateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Utils.UpdateInitialScreenUser(new User(String.valueOf(mUserName.getText())
                                , String.valueOf(mFamilyName.getText())
                                , (mIsFamily.isChecked()))
                        , mDb);
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


