package greenstory.rtg.com;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    CheckBox mIsFamily;
    User user = new User();
    private GoogleMap mMap;
    private Site trackSite;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ListView siteInfo;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    LatLng centerLatLng = new LatLng(32.698123394504464,35.14352526515722);
    LatLngBounds mapBounds = new LatLngBounds(new LatLng(32.0736685,34.7799253),
                                              new LatLng(32.9002805,35.5586083));
    private static float initialZoom = 8.5f;

    Context context = this;
    private SQLiteDatabase mDb;

    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;
    boolean backClicked=false;


    HashMap<Integer,MarkerOptions> intMarkerOptionsHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        checkDataPermissions(user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        getSupportActionBar().setTitle("Green Story");

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
        Resources res = getResources();
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, res.getStringArray(R.array.options_array)));
        mDrawerList.setOnItemClickListener(new HomeMapActivity.DrawerItemClickListener());

        //new DBLoadUserTask().execute(user,null,null);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        addMarkers(mMap);
    }

    public void initialiseMarkers(){
        intMarkerOptionsHashMap.put(0,new MarkerOptions()
                .position(new LatLng(32.0737617,34.7995856))
                .title("תוצרת הארץ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.totzeret_haaretz_72)));
        intMarkerOptionsHashMap.put(1,new MarkerOptions()
                .position(new LatLng(32.824166,35.4986072))
                .title("הר ארבל")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbel)));
        intMarkerOptionsHashMap.put(2,new MarkerOptions()
                .position(new LatLng(32.0477291,34.7609729))
                .title("המכללה האקדמית תל אביב יפו")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mta_72)));

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
                if((isCoarseLocationGranted==false||isCoarseLocationGranted==false)){
                    //showOutDialog();
                }
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
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this,
                UsersContract.UserEntry.SQL_CREATE_USERS_TABLE);
        mDb = dbHelper.getWritableDatabase();
        if (!isUserIdExists(mDb)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.activity_home_dialog_login, null);
            Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
            mUserName = mView.findViewById(R.id.etUserName);
            mFamilyName = mView.findViewById(R.id.etFamilyName);
            mIsFamily = mView.findViewById(R.id.cbIsFamily);
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
                        user.setIsFamily(mIsFamily.isChecked());
                        user.setPoints(0);
                        new DBUserRegisterTask().execute(user, null, null);
                        dialog.dismiss();
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

    private void showOutDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_out, null);
        Button btn_out = (Button) mView.findViewById(R.id.btn_out);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showTrackDialog(final Marker marker) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_track_dialog, null);
        Button goToTrackBtn = (Button) mView.findViewById(R.id.btn_go_to_track);
        TextView siteInfo = (TextView) findViewById(R.id.tv_dialog_track_details);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
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
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
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
                startActivity(intent);
            }
            if (position == 2) {
                showAttractionsDialog();
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
            startActivity(intent);

            }
    }
}

