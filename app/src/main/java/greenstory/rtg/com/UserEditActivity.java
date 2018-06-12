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
    private TextView mNameTV;
    private TextView mNameValueTV;

    private EditText mFamilyNameET;
    private TextView mFamilyNameTV;
    private TextView mFamilyNameValueTV;

    private EditText mPartnerNameET;
    private TextView mPartnerNameTV;
    private TextView mPartnerNameValueTV;

    private EditText mAgeET;
    private TextView mAgeTV;
    private TextView mAgeValueTV;

    private EditText mPartnerAgeET;
    private TextView mPartnerAgeTV;
    private TextView mPartnerAgeValueTV;

    private CheckBox mIsFamilyCB;

    private boolean isSetFieldsBtnEdit = true;
    Context context = this;

    private SQLiteDatabase mDb;

    private Button mSetFieldsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        mNameET = (EditText) findViewById(R.id.et_name);
        mNameTV = (TextView) findViewById(R.id.tv_name);
        mNameValueTV = (TextView) findViewById(R.id.tv_name_value);

        mFamilyNameET = (EditText) findViewById(R.id.et_familyName);
        mFamilyNameTV = (TextView) findViewById(R.id.tv_familyName);
        mFamilyNameValueTV = (TextView) findViewById(R.id.tv_familyName_value);

        mPartnerNameET = (EditText) findViewById(R.id.et_partnerName);
        mPartnerNameTV = (TextView) findViewById(R.id.tv_partnerName);
        mPartnerNameValueTV = (TextView) findViewById(R.id.tv_partnerName_value);

        mAgeET = (EditText) findViewById(R.id.et_age);
        mAgeTV = (TextView) findViewById(R.id.tv_age);
        mAgeValueTV = (TextView) findViewById(R.id.tv_age_value);

        mPartnerAgeET = (EditText) findViewById(R.id.et_partnerAge);
        mPartnerAgeTV = (TextView) findViewById(R.id.tv_partnerAge);
        mPartnerAgeValueTV = (TextView) findViewById(R.id.tv_partnerAge_value);

        mIsFamilyCB = (CheckBox) findViewById(R.id.cb_edit_isFamily);

        mSetFieldsBtn = (Button) findViewById(R.id.btn_edit_fields);
        mSetFieldsBtn.setOnClickListener(listener);
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        updateTextViewValueFields(user);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == mSetFieldsBtn.getId()) {
                findViewById(R.id.editTextLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textViewLayout).setVisibility(View.GONE);

                if (isSetFieldsBtnEdit) {
                    updateEditTextFields(user);
                    mSetFieldsBtn.setText("סיום");
                    isSetFieldsBtnEdit = false;
                }
                else {
                    user.setUserName(String.valueOf(mNameET.getText()));
                    user.setFamilyName(String.valueOf(mFamilyNameET.getText()));
                    user.setPartnerName(String.valueOf(mPartnerNameET.getText()));
                    if(String.valueOf(mAgeET.getText()).contentEquals("")){
                        user.setUserAge(0);
                    }
                    else {
                        user.setUserAge(Integer.parseInt(String.valueOf(mAgeET.getText())));
                    }
                    if(String.valueOf(mPartnerAgeET.getText()).contentEquals("")){
                        user.setPartnerAge(0);
                    }
                    else{
                        user.setPartnerAge(Integer.parseInt(String.valueOf(mPartnerAgeET.getText())));
                    }
                user.setIsFamily(mIsFamilyCB.isChecked());
                findViewById(R.id.editTextLayout).setVisibility(View.GONE);
                findViewById(R.id.textViewLayout).setVisibility(View.VISIBLE);

                mSetFieldsBtn.setText("עריכה");
                isSetFieldsBtnEdit = true;
                updateTextViewValueFields(user);
                new DBEditUserDetailsTask().execute(user);
                }
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

    private void updateTextViewValueFields(User user) {
        try {
            mNameValueTV.setText(user.getUserName());
            mFamilyNameValueTV.setText(user.getFamilyName());
            if (user.getPartnerName().contentEquals("none")){

            }
            else {
                mPartnerNameValueTV.setText(user.getPartnerName());
            }

            if (user.getUserAge() != 0) {
                mAgeValueTV.setText(String.valueOf(user.getUserAge()));
            }
            else if(user.getPartnerAge()==0){
                mAgeValueTV.setText("");
            }
            if (user.getPartnerAge() != 0) {
                mPartnerAgeValueTV.setText(String.valueOf(user.getPartnerAge()));
            }
            else if(user.getPartnerAge()==0){
                mPartnerAgeValueTV.setText("");
            }
            mIsFamilyCB.setEnabled(false);
            mIsFamilyCB.setChecked(user.isFamily());


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateEditTextFields(User user) {
        try {
            mNameET.setText(user.getUserName());
            mFamilyNameET.setText(user.getFamilyName());
            if (user.getPartnerName().contentEquals("none")){

            }
            else {
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

    public class DBEditUserDetailsTask extends AsyncTask<User, Void, Void> {

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


