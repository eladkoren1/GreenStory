package greenstory.rtg.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;
import java.util.HashMap;
import java.util.List;

import greenstory.rtg.com.classes.Site;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

public class HomeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText mUserName;
    EditText mFamilyName;

    AlertDialog sitesDialog;
    AlertDialog attractionsDialog;
    AlertDialog galleryDialog;
    AlertDialog aboutDialog;
    User user = new User();
    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ListView siteInfo;
    ListView sites;
    ListView gallery;
    String[] sitesArray;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView nameTitleTextView;
    ImageButton drawerImageButton;
    LatLng centerLatLng = new LatLng(32.698123394504464,35.14352526515722);
    LatLngBounds mapBounds = new LatLngBounds(new LatLng(32.0736685,34.7799253),
                            new LatLng(32.9002805,35.5586083));
    private static float initialZoom = 8.5f;
    Context context = this;
    private SQLiteDatabase mDb;
    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;
    boolean backClicked=false;
    ImageButton closeTrackDialogImageButton;

    HashMap<Integer,MarkerOptions> intMarkerOptionsHashMap = new HashMap<>();
    HashMap<Integer,Site> integerSiteHashMap = new HashMap<>();
    private TextView siteName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        Resources res = getResources();
        sitesArray = res.getStringArray(R.array.sites_array);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        getSupportActionBar().setTitle("Green Story");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        nameTitleTextView = findViewById(R.id.tv_action_title_bar);
        drawerImageButton = (ImageButton)findViewById(R.id.ib_open_drawer);
        drawerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.openDrawer(Gravity.START,true);
                }
                if (mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.closeDrawer(Gravity.START,true);
                }
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle (
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer_white,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, res.getStringArray(R.array.options_array)));
        mDrawerList.setOnItemClickListener(new HomeMapActivity.DrawerItemClickListener());
        checkDataPermissions(user);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!user.equals(null)){
            nameTitleTextView.setText("ברוך הבא "+user.getUserName());
        }




    }

    @Override
    public void onBackPressed() {
        LatLng currentLatLng = mMap.getCameraPosition().target;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,initialZoom));
        if(currentLatLng.equals(centerLatLng)){
            super.onBackPressed();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermissions(mMap);
        initiateLocation(mMap);//Checking for permissions and Initiating map properties
        initialiseMarkers();
        initialiseSites();
        addMarkers(mMap);
    }

    public void initialiseMarkers(){
        intMarkerOptionsHashMap.put(0,new MarkerOptions()
                .position(new LatLng(32.0737617,34.7995856))
                .title("תוצרת הארץ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.totzeret_haaretz_72)));
        intMarkerOptionsHashMap.put(1,new MarkerOptions()
                .position(new LatLng(32.0477291,34.7609729))
                .title("המכללה האקדמית תל אביב יפו")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mta_72)));
        intMarkerOptionsHashMap.put(2,new MarkerOptions()
                .position(new LatLng(32.824166,35.4986072))
                .title("שמורת הר ארבל")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbel)));

    }

    public void initialiseSites(){
        integerSiteHashMap.put(0,
                new Site("תוצרת הארץ",
                        1,
                        "שכונת נחלת יצחק היא שכונה בדרום-מזרח תל אביב שהוקמה בשנת 1925 מזרחית לנחל איילון (ואדי מוסררה, כיום נתיבי איילון), על ידי קבוצת יהודים שבאו מקובנה. השכונה סמוכה לשכונות ביצרון ורמת ישראל"));
        integerSiteHashMap.put(1,
                new Site("המכללה האקדמית תל אביב יפו",
                        2,
                        "האקדמית תל אביב-יפו הוקמה בשנת 1994 ביוזמה משותפת של אוניברסיטת תל אביב, עיריית תל אביב-יפו והוועדה לתכנון ולתקצוב של המועצה להשכלה גבוהה, כמוסד אקדמי ציבורי להשכלה גבוהה (האקדמית זכתה להכרה כמוסד להשכלה גבוהה ב-1996)"));
        integerSiteHashMap.put(2,
                new Site("שמורת הר ארבל",
                        3,
                        "הגן הלאומי כולל בתוכו את רוב שטחו של הר הארבל, הר ניתאי, הר סביון, קרני חיטין ורמת ארבל. בשטח הגן הלאומי מסומנים שבילי טיול. השביל המוליך ממגרש החניה קצר ונוח להליכה. הוא עולה בשיפוע מתון עד אל שפת המצוק, המתנשא מעל סביבתו לגובה 400 מטר ומעניק מראות נוף למרחקים"));

    }

    public void addMarkers(GoogleMap map) {

        for (int i=0;i<intMarkerOptionsHashMap.size();i++){
            map.addMarker(intMarkerOptionsHashMap.get(i));
        }
    }

    @SuppressLint("MissingPermission")
    void initiateLocation(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setLatLngBoundsForCameraTarget(mapBounds);
        map.setMinZoomPreference(7f);
        map.setMaxZoomPreference(15f);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,initialZoom));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showTrackDialog(marker);
                backClicked=false;
                return true;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                backClicked=false;
            }
        });
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                backClicked=false;
            }
        });
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void initiateDB(final User user) {
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        new DBLoadUserTask().execute(user,null,null);
        if (!isUserIdExists(mDb)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.activity_home_dialog_login, null);
            Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
            mUserName = mView.findViewById(R.id.etUserName);
            mFamilyName = mView.findViewById(R.id.etFamilyName);
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
                        user.setIsFamily(false);
                        user.setPoints(0);
                        new DBUserRegisterTask().execute(user, null, null);
                        dialog.dismiss();
                        if (!user.equals(null)){
                            nameTitleTextView.setText("ברוך הבא "+user.getUserName());
                        }

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

    private void checkDataPermissions(User user) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }
        else {
            initiateDB(user);
        }
    }

    private void checkLocationPermissions(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            isFineLocationGranted=true;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }

        else {
            isCoarseLocationGranted=true;
        }

        if(isCoarseLocationGranted&&isFineLocationGranted){
            Log.d("Initiation: ","check permissions");
            initiateLocation(map);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (isFineLocationGranted && isFineLocationGranted) {
            return;
        } else {
            switch (requestCode) {
                case 1: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        isCoarseLocationGranted = true;

                    } else {
                        return;
                    }
                }
                case 2: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        isFineLocationGranted = true;
                    } else {
                        return;
                    }
                }
                case 3: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        initiateDB(user);

                    } else {
                        Toast.makeText(context, "Can't work without permissions :(", Toast.LENGTH_LONG).show();

                    }
                }
            }
            if (isFineLocationGranted && isCoarseLocationGranted) {
                Log.d("Initiation: ", "permissions results");
                initiateLocation(mMap);
            } else {
                return;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTrackDialog(final Marker marker) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_track_dialog, null);
        Button goToTrackBtn = (Button) mView.findViewById(R.id.btn_go_to_track);
        TextView siteInfo = (TextView) mView.findViewById(R.id.tv_dialog_track_details);
        closeTrackDialogImageButton = (ImageButton) mView.findViewById(R.id.btn_close_track_dialog);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        closeTrackDialogImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        for (int i=0;i<integerSiteHashMap.size();i++) {
            if (marker.getTitle().contentEquals(integerSiteHashMap.get(i).getSiteName())) {
                siteInfo.setText(integerSiteHashMap.get(i).getSiteData());
                break;
            }
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        goToTrackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                for (int i=0;i<intMarkerOptionsHashMap.size();i++){
                    if(marker.getTitle().contentEquals(intMarkerOptionsHashMap.get(i).getTitle())){
                        intent.putExtra("kmlResource", i);
                        break;
                    }
                }
                dialog.dismiss();
                startActivity(intent);
            }
        });
    }

    private void showAttractionsDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_attractions_dialog, null);
        siteInfo = mView.findViewById(R.id.lv_attractions);
        Resources res = getResources();
        siteInfo.setAdapter(new ArrayAdapter<String>(this,
                R.layout.attractions_list_item, res.getStringArray(R.array.attractions_array)));
        siteInfo.setOnItemClickListener(new HomeMapActivity.attractionsItemClickListener());
        mBuilder.setView(mView);
        attractionsDialog = mBuilder.create();
        attractionsDialog.show();
        attractionsDialog.setCancelable(true);
        attractionsDialog.setCanceledOnTouchOutside(true);
    }

    private void showSitesDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_sites_dialog, null);
        sites = mView.findViewById(R.id.lv_sites);
        Resources res = getResources();
        sites.setAdapter(new ArrayAdapter<String>(this,
                R.layout.sites_list_item, res.getStringArray(R.array.sites_array)));
        sites.setOnItemClickListener(new HomeMapActivity.sitesItemClickListener());
        mBuilder.setView(mView);
        sitesDialog = mBuilder.create();
        sitesDialog.show();
        sitesDialog.setCancelable(true);
        sitesDialog.setCanceledOnTouchOutside(true);
    }

    private void showGalleryDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_gallery_dialog, null);
        gallery = mView.findViewById(R.id.lv_gallery);
        Resources res = getResources();
        gallery.setAdapter(new ArrayAdapter<String>(this,
                R.layout.sites_list_item, res.getStringArray(R.array.sites_array)));
        gallery.setOnItemClickListener(new HomeMapActivity.galleryItemClickListener());
        mBuilder.setView(mView);
        galleryDialog = mBuilder.create();
        galleryDialog.show();
        galleryDialog.setCancelable(true);
        galleryDialog.setCanceledOnTouchOutside(true);
    }

    private void showAboutDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_about_dialog, null);
        TextView about = (TextView) mView.findViewById(R.id.tv_dialog_about);
        mBuilder.setView(mView);
        aboutDialog = mBuilder.create();
        aboutDialog.show();
        aboutDialog.setCancelable(true);
        aboutDialog.setCanceledOnTouchOutside(true);
        }


    class DBUserRegisterTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... user) {

            try {
                Utils.UpdateInitialScreenUser(user[0], mDb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    class DBUserUpdateTask extends AsyncTask<User, Void, Void> {

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

    class DBLoadUserTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... userArray) {

            try {
                user = Utils.LoadUserFromDB(userArray[0], mDb);
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

    class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position, true);
            if (position == 0) {
                Intent intent = new Intent(context, UsersOptionsActivity.class);
                mDrawerLayout.closeDrawer(Gravity.START,true);
                startActivity(intent);
            }
            if (position == 1) {
               showSitesDialog();
            }
            if (position == 2) {
                showAttractionsDialog();
            }

            if (position == 3) {

                showGalleryDialog();
            }
            if (position == 4) {

                showAboutDialog();
            }
        }
    }

    class attractionsItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String site = String.valueOf(siteInfo.getItemAtPosition(position));
            String url = "https://www.google.co.il/search?q="+site;
            //Uri uri = Uri.parse(url);
            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            mDrawerLayout.closeDrawer(Gravity.START,true);
            attractionsDialog.dismiss();
            startActivity(intent);

            }
    }

    class sitesItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String site = String.valueOf(sites.getItemAtPosition(position));
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("kmlResource", position);
            sitesDialog.dismiss();
            mDrawerLayout.closeDrawer(Gravity.START,true);
            startActivity(intent);
        }
    }

    class galleryItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String site = String.valueOf(gallery.getItemAtPosition(position));
            Intent intent = new Intent(context, AdvancedGalleryActivity.class);
            intent.putExtra("site",sitesArray[position]);
            galleryDialog.dismiss();
            mDrawerLayout.closeDrawer(Gravity.START,true);
            startActivity(intent);
        }
    }
}



