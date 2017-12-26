package greenstory.rtg.com;

import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    private Button points;
    private TextView mName;
    private TextView mFamilyName;
    private TextView mPartnerName;
    private TextView mAge;
    private TextView mPartnerAge;
    private CheckBox mIsFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }



}
