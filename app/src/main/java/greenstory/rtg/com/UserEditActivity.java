package greenstory.rtg.com;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;
import greenstory.rtg.com.data.GreenStoryDbHelper;

public class UserEditActivity extends AppCompatActivity {

    User user;

    private Button points;
    private EditText mNameET;
    private EditText mFamilyNameET;
    private EditText mPartnerNameET;
    private EditText mAgeET;
    private EditText mPartnerAgeET;
    private CheckBox mIsFamilyCB;
    private boolean isSetFieldsBtnEdit = true;
    Context context = this;
    private SQLiteDatabase mDb;
    private Button mSetFieldsBtn;
    TextView titleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        getSupportActionBar().setTitle("Green Story");
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_no_burger);
        titleTextView = findViewById(R.id.tv_action_title_bar);
        titleTextView.setText("עריכת פרטים");

        mNameET = (EditText) findViewById(R.id.et_name);
        mFamilyNameET = (EditText) findViewById(R.id.et_familyName);
        mPartnerNameET = (EditText) findViewById(R.id.et_partnerName);
        mAgeET = (EditText) findViewById(R.id.et_age);
        mPartnerAgeET = (EditText) findViewById(R.id.et_partnerAge);
        mIsFamilyCB = (CheckBox) findViewById(R.id.cb_edit_isFamily);
        mSetFieldsBtn = (Button) findViewById(R.id.btn_edit_fields);
        mSetFieldsBtn.setOnClickListener(listener);
        updateEditTextFields(user);

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == mSetFieldsBtn.getId()) {

                user.setUserName(String.valueOf(mNameET.getText()));
                user.setFamilyName(String.valueOf(mFamilyNameET.getText()));
                user.setPartnerName(String.valueOf(mPartnerNameET.getText()));
                if (String.valueOf(mAgeET.getText()).contentEquals("")) {
                    user.setUserAge(0);
                } else {
                    user.setUserAge(Integer.parseInt(String.valueOf(mAgeET.getText())));
                }
                if (String.valueOf(mPartnerAgeET.getText()).contentEquals("")) {
                    user.setPartnerAge(0);
                } else {
                    user.setPartnerAge(Integer.parseInt(String.valueOf(mPartnerAgeET.getText())));
                }
                user.setIsFamily(mIsFamilyCB.isChecked());
                new DBEditUserDetailsTask().execute(user);
                UserEditActivity.super.onBackPressed();
            }
        }
    };

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }

    }

    private void updateEditTextFields(User user) {
        try {
            mNameET.setText(user.getUserName());
            mFamilyNameET.setText(user.getFamilyName());
            if (user.getPartnerName().contentEquals("none")) {

            } else {
                mPartnerNameET.setText(user.getPartnerName());
            }
            if (user.getUserAge() != 0) {
                mAgeET.setText(String.valueOf(user.getUserAge()));
            }
            if (user.getPartnerAge() != 0) {
                mPartnerAgeET.setText(String.valueOf(user.getPartnerAge()));
            }
            mIsFamilyCB.setEnabled(true);
            mIsFamilyCB.setChecked(user.isFamily());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    class DBEditUserDetailsTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... user) {
            try {
                Utils.UpdateUserInfo(user[0], mDb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}



