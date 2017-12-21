package greenstory.rtg.com;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;


public class HomeActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;

    ScrollView scrollView;
    LinearLayout scrollChildLayout;
    TextView trackTV;
    Button addText;
    public Context context = this;
    public int textViewId = 1;
    ArrayList<String> tracks = new ArrayList<>();
    public int tracksListSize = 1;
    public User mUser;
    public Boolean userExists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scrollView = (ScrollView) findViewById(R.id.scroller);
        scrollChildLayout = (LinearLayout) findViewById(R.id.scroll_contents);
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);





        mDb = dbHelper.getWritableDatabase();

        addText = (Button) findViewById(R.id.add_item_button);
        mUser = new User("e","k",false);

        addText.setOnClickListener(listener);
        //mDb.delete("users",null,null);


        //TODO 1 if guest, check in SQLITE if there is a user already and store in a boolean var
        //if none, add popup first run box.
        //if there is, skip popup first run and use settings.
        //TODO 2 use aAsyncLoader TO SYNC WITH MYSQL.
        // if no connection availabe, use local data anyway.
        //TODO 3 allow user to open user with only name and offer from menu to add family details after
        if (!isUserIdExists(mDb)){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
            final EditText mEmail = (EditText) mView.findViewById(R.id.etEmail);
            final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
                        Toast.makeText(context,
                                "Login successful",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context,
                                "missing",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {userExists = true;}

        if(!userExists)
        {UpdateInitialScreenUser(mUser, mDb);}

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

    public void UpdateInitialScreenUser(User user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        String idFromDate = String.valueOf(java.util.Calendar.getInstance().getTimeInMillis());

        cv.put("uId", idFromDate);
        cv.put("name", user.getUserName());
        cv.put("familyName", user.getFamilyName());
        cv.put("isFamily", user.isFamily() ? 1 : 0);

        try
        {
            db.beginTransaction();
            db.insert("users", null,cv);
            db.setTransactionSuccessful();


        }
        catch (Exception e)
        {
            Log.e("ERROR", e.toString());
        }
        finally{
            db.endTransaction();
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

}


