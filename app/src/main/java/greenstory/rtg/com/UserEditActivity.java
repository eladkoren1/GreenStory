package greenstory.rtg.com;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        mNameET = (EditText) findViewById(R.id.et_name);
        mNameTV = (TextView) findViewById(R.id.tv_name);
        mNameValueTV = (TextView) findViewById(R.id.tv_name_value);

        mFamilyNameET = (EditText)findViewById(R.id.et_familyName);
        mFamilyNameTV = (TextView)findViewById(R.id.tv_familyName);
        mFamilyNameValueTV = (TextView)findViewById(R.id.tv_familyName_value);

        mPartnerNameET = (EditText)findViewById(R.id.et_partnerName);
        mPartnerNameTV = (TextView)findViewById(R.id.tv_partnerName);
        mPartnerNameValueTV = (TextView)findViewById(R.id.tv_partnerName_value);

        mAgeET = (EditText)findViewById(R.id.et_age);
        mAgeTV = (TextView)findViewById(R.id.tv_age);
        mAgeValueTV = (TextView)findViewById(R.id.tv_age_value);

        mPartnerAgeET = (EditText)findViewById(R.id.et_partnerAge);
        mPartnerAgeTV = (TextView)findViewById(R.id.tv_partnerAge);
        mPartnerAgeValueTV = (TextView)findViewById(R.id.tv_partnerAge_value);

        mIsFamilyCB = (CheckBox)findViewById(R.id.cb_edit_isFamily);

        mSetFieldsBtn = (Button) findViewById(R.id.btn_edit_fields);
        mSetFieldsBtn.setOnClickListener(listener);
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this, UsersContract.UserEntry.SQL_CREATE_USERS_TABLE);
        mDb = dbHelper.getWritableDatabase();

        //ImageView familyPictureImageView = (ImageView) findViewById(R.id.im_family_picture);


    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId()==mSetFieldsBtn.getId()){
                findViewById(R.id.editTextLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textViewLayout).setVisibility(View.GONE);

                if (isSetFieldsBtnEdit) {
                    mSetFieldsBtn.setText("סיום");
                    isSetFieldsBtnEdit = false;
                }
                else {
                    if (isNumber(String.valueOf(mAgeET.getText()))&&isNumber(String.valueOf(mPartnerAgeET.getText()))) {
                        User editedUser = new User(String.valueOf(mNameET.getText()),
                                String.valueOf(mFamilyNameET.getText()),
                                String.valueOf(mPartnerNameET.getText()),
                                Integer.parseInt(String.valueOf(mAgeET.getText())),
                                Integer.parseInt(String.valueOf(mPartnerAgeET.getText())),
                                mIsFamilyCB.isChecked(),0);
                        new DBEditUserDetailsTask().execute(editedUser);
                        findViewById(R.id.editTextLayout).setVisibility(View.GONE);
                        findViewById(R.id.textViewLayout).setVisibility(View.VISIBLE);

                        mSetFieldsBtn.setText("עריכה");
                        isSetFieldsBtnEdit = true;
                    }
                    else {
                        Toast.makeText(context, "Insert number in age!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private boolean isNumber(String text){
        try{
            Integer.parseInt(text);
            return true;
        }
        catch (Exception e){
            e.getMessage();
            return false;
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
            Toast.makeText(context,
                    "פעולת עדכון משתמש הושלמה בהצלחה",
                    Toast.LENGTH_SHORT).show();

        }
    }

}
