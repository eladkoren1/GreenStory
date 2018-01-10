package greenstory.rtg.com;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Elad on 09/01/2018.
 */

public class Args extends AppCompatActivity {

    Resources res = getResources();
    public final String[] SITES = res.getStringArray(R.array.sites_array);
    public static final String[]  MENU_OPTIONS = {"משתמש","מסלולים","מפה","משתתפים","אודות","צור קשר","חנות"};
}
