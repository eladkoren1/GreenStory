package greenstory.rtg.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

public class HomeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    EditText mUserName;
    EditText mFamilyName;
    CheckBox mIsFamily;
    User user = new User();
    private GoogleMap mMap;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    LatLng centerLatLng = new LatLng(32.110385,35.5004833);
    LatLng northAreaLatLng = new LatLng(32.852164,35.4706053);
    LatLng centerAreaLatLng = new LatLng(32.110385,35.054833);
    LatLng southAreaLatLng = new LatLng(31.721215,35.0095763);
    private static float initialZoom = 8f;

    String[] homeScreenOptionsArray = {"משתמש", "מסלולים", "מפה", "משתתפים", "אודות", "צור קשר", "חנות"};

    KmlLayer siteLayer;
    Context context = this;
    private SQLiteDatabase mDb;

    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;
    boolean backClicked=false;

    TextView northAreaTextView;
    TextView centerAreaTextView;
    TextView southAreaTextView;

    /*View.OnClickListener onAreaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_north_area:
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(northAreaLatLng,9.3f));
                    backClicked=false;
                    northAreaTextView.setOnClickListener(null);
                    centerAreaTextView.setOnClickListener(null);
                    southAreaTextView.setOnClickListener(null);
                    northAreaTextView.setVisibility(View.GONE);
                    northAreaTextView.setEnabled(false);
                    break;
                case R.id.tv_center_area:
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerAreaLatLng,9.5f));
                    backClicked=false;
                    northAreaTextView.setOnClickListener(null);
                    centerAreaTextView.setOnClickListener(null);
                    southAreaTextView.setOnClickListener(null);
                    break;
                case R.id.tv_south_area:
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(southAreaLatLng,9.3f));
                    backClicked=false;
                    northAreaTextView.setOnClickListener(null);
                    centerAreaTextView.setOnClickListener(null);
                    southAreaTextView.setOnClickListener(null);
                    break;
            }
        }
    };*/
    HashMap<String,Marker> markerIntegerHashMap = new HashMap<String, Marker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        checkDataPermissions(user);

        //northAreaTextView = (TextView) findViewById(R.id.tv_north_area);
        //centerAreaTextView = (TextView) findViewById(R.id.tv_center_area);
        //southAreaTextView = (TextView) findViewById(R.id.tv_south_area);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, homeScreenOptionsArray));
        mDrawerList.setOnItemClickListener(new HomeMapActivity.DrawerItemClickListener());


        //new DBLoadUserTask().execute(user,null,null);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        if (!backClicked){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,initialZoom));
            //northAreaTextView.setOnClickListener(onAreaClickListener);
            //centerAreaTextView.setOnClickListener(onAreaClickListener);
            //southAreaTextView.setOnClickListener(onAreaClickListener);
           // northAreaTextView.setVisibility(View.VISIBLE);
            //northAreaTextView.setEnabled(true);
            backClicked=true;
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermissions(mMap);//Checking for permissions and Initiating map properties
        addSiteLayer();

        LoadTracksMarkers(siteLayer);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if((isCoarseLocationGranted==false||isCoarseLocationGranted==false)){
                    showOutDialog();
                }
            }
        });
    }

    public void addSiteLayer() {
        try {
            siteLayer = new KmlLayer(mMap, R.raw.main_map, getApplicationContext());
            siteLayer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadTracksMarkers(final KmlLayer kmlLayer) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String placemarkPointName = null;
                if (kmlLayer.isLayerOnMap()) {
                    if (kmlLayer.hasContainers()) {
                        Iterable<KmlContainer> layerContainers = kmlLayer.getContainers();
                        for (KmlContainer layerContainer: layerContainers) {
                            if (layerContainer.hasContainers()) {
                                Iterable<KmlContainer> tracksContainers = layerContainer.getContainers();
                                for (KmlContainer tracksContainer : tracksContainers) {
                                    Iterable<KmlContainer> trackContainer = tracksContainer.getContainers();
                                    for (KmlContainer track: trackContainer) {
                                        if (track.hasPlacemarks()) {
                                            Iterable<KmlPlacemark> trackPlacemarks = track.getPlacemarks();
                                            for (KmlPlacemark placemark: trackPlacemarks) {
                                                if (placemark.hasGeometry()) {
                                                    if (placemark.getGeometry().toString().contains("Point")) {
                                                        KmlPoint point = (KmlPoint) placemark.getGeometry();
                                                        LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                                                        //tracksPlacemarksHashMap.put(latLng, 1);
                                                        Log.d("latlng", point.toString());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @SuppressLint("MissingPermission")
    void initiateLocation(GoogleMap map) {
        map.setMyLocationEnabled(false);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(32.824166,35.4986072))
                .title("הר ארבל")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbel)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,initialZoom));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(context,marker.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        //northAreaTextView.setOnClickListener(onAreaClickListener);
        //centerAreaTextView.setOnClickListener(onAreaClickListener);
        //southAreaTextView.setOnClickListener(onAreaClickListener);
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
            Toast.makeText(this,"fine location granted already",Toast.LENGTH_SHORT).show();
            isFineLocationGranted=true;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }

        else {
            Toast.makeText(this,"coarse location granted already",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);
            }
        }
    }
}
