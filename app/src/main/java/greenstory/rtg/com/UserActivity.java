package greenstory.rtg.com;

import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    private Button points;
    private TextView mNameTV;
    private TextView mNameValue;
    private EditText mNameET;
    private TextView mFamilyName;
    private TextView mPartnerName;
    private TextView mAge;
    private TextView mPartnerAge;
    private CheckBox mIsFamily;

    private Button setFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mNameTV = (TextView) findViewById(R.id.tv_name);
        mNameValue = (TextView) findViewById(R.id.tv_name_value);
        mNameET = (EditText) findViewById(R.id.et_name);
        setFields = (Button) findViewById(R.id.btn_edit_fields);
        setFields.setOnClickListener(listener);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch
            if (view.getId()==setFields.getId()){
                mNameET.setVisibility(View.VISIBLE);
                mNameTV.setVisibility(View.INVISIBLE);
                mNameValue.setVisibility(View.INVISIBLE);
            }
        }
    };

}
