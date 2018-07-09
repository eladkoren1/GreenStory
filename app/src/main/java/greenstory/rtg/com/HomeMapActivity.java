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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.kml.KmlLayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import greenstory.rtg.com.classes.Site;
import greenstory.rtg.com.classes.Track;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

public class HomeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean isMapFragmentCalled=false;
    boolean backClicked=false;
    private static float initialZoom = 8.5f;

    private EditText mUserName;
    private EditText mFamilyName;
    private ImageButton closeTrackDialogImageButton;
    private AlertDialog sitesDialog;
    private AlertDialog attractionsDialog;
    private AlertDialog galleryDialog;
    private AlertDialog aboutDialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView siteInfo;
    private ListView sites;
    private ListView gallery;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView nameTitleTextView;
    private ImageButton drawerImageButton;
    private TextView siteName;

    private LatLng centerLatLng = new LatLng(32.698123394504464,35.14352526515722);
    private LatLngBounds mapBounds = new LatLngBounds(new LatLng(32.0736685,34.7799253),
                                                      new LatLng(32.9002805,35.5586083));
    private Context context = this;
    private SQLiteDatabase mDb;

    private GoogleMap mMap;
    private HashMap<Integer,Site> integerSiteHashMap = new HashMap<>();
    ArrayList<Track> tracks = new ArrayList<Track>();
    HashMap<String,HashMap<Integer,String>> dataLists = new HashMap<>();
    private Track track = null;
    private User user = new User();

    FirebaseDatabase greenStoryFirebaseDB = FirebaseDatabase.getInstance();
    DatabaseReference sitesReference = greenStoryFirebaseDB.getReference("sites");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        checkPermissions();
        initDataLists(dataLists);
        track = new Track("רחוב תוצרת הארץ","",R.raw.totzeret_haaretz);
        //Custom action bar settings
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        getSupportActionBar().setTitle("Green Story");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        nameTitleTextView = findViewById(R.id.tv_action_title_bar);

        //Drawer initialization and settings
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
                R.layout.drawer_list_item, getResources().getStringArray(R.array.options_array)));
        mDrawerList.setOnItemClickListener(new HomeMapActivity.DrawerItemClickListener());
    }

    private void checkPermissions() {
        //Permission asking for writing storage (for DB), fine location, and coarse location
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        //If write permission is already granted, start DB method
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            initiateDB(user);
        }

        //If location permission is already granted, get google map
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (!isMapFragmentCalled) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                isMapFragmentCalled=true;
            }
        }

        //If location permission is already granted, get google map
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (!isMapFragmentCalled) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                isMapFragmentCalled=true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        //Write DB permission result check
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initiateDB(user);
        }
        else {
            Toast.makeText(this,"write permissions is really important...",Toast.LENGTH_LONG).show();
            finish();
        }

        //Fine location permission result check
        if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (!isMapFragmentCalled) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                isMapFragmentCalled=true;
            }
        }

        else {
            Toast.makeText(this,"location permissions is really important...",Toast.LENGTH_LONG).show();
            finish();
        }

        //Coarse location permissions result check
        if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            Toast.makeText(context, "location permissions is really important...", Toast.LENGTH_LONG).show();
            finish();
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
        setupMap(mMap);
        final HashMap<Integer, String> markerIconUrlHashMap = new HashMap<>();
        sitesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot siteKey : dataSnapshot.getChildren()) {
                    markerIconUrlHashMap.put((Integer.parseInt(siteKey.getKey())),
                            siteKey.child("markerIconUrl").getValue().toString());
                }
                new FetchIconsTask().execute(markerIconUrlHashMap, null, null);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void initialiseSites(final HashMap<Integer,Bitmap> markerIconHashMap){
        sitesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.child("names").child("1").getValue(String.class);
                int i=0;
                for (DataSnapshot siteKey : dataSnapshot.getChildren()) {

                    double lat = (Double) siteKey.child("coordinates").child("latitude").getValue();
                    double lon = Double.parseDouble(siteKey.child("coordinates").child("longitude").getValue().toString());
                    String absPath = Uri.parse("android.resource://com.rtg.greenstory/drawable/mta_72").getPath();
                    Site site = new Site((Integer.parseInt(siteKey.getKey())),
                            siteKey.child("siteName").getValue().toString(),
                            siteKey.child("siteDescription").getValue().toString(),
                            new LatLng(lat,lon),
                            new MarkerOptions().position(new LatLng(lat,lon))
                                               .title(siteKey.child("siteName").getValue().toString())
                                               .icon(BitmapDescriptorFactory.fromBitmap(markerIconHashMap.get((Integer.parseInt(siteKey.getKey()))))));
                    integerSiteHashMap.put(i,site);
                    i++;
                }
                addMarkers(mMap,integerSiteHashMap);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //integerSiteHashMap.get(0).getTracks().add(track);
        //integerSiteHashMap.get(1).getTracks().add(new Track("מקיף מכללה","",R.raw.academic_tlv));
        //integerSiteHashMap.get(2).getTracks().add(new Track("סובב הר ארבל","",-1));*/

    }

    public void addMarkers(GoogleMap map,HashMap<Integer,Site> integerSiteHashMap) {

        for (int i=0;i<integerSiteHashMap.size();i++){
            map.addMarker(integerSiteHashMap.get(i).getSiteHomeMarker());
        }
    }

    @SuppressLint("MissingPermission")
    private void setupMap(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setLatLngBoundsForCameraTarget(mapBounds);
        map.setMinZoomPreference(7f);
        map.setMaxZoomPreference(15f);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,initialZoom));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showSiteDialog(marker);
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

    private void initiateDB(final User user) {
        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        new DBLoadUserTask().execute(user,null,null);
        if (!isUserIdExists(mDb)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.activity_home_dialog_login, null);
            Button mLogin = mView.findViewById(R.id.btnLogin);
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
                            //nameTitleTextView.setText("ברוך הבא "+user.getUserName());
                        }

                    }
                    else {
                        Toast.makeText(context, "השלם שדות חסרים", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initDataLists(final HashMap<String, HashMap<Integer, String>> dataLists) {
        greenStoryFirebaseDB.getReference("lists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot list  :  dataSnapshot.getChildren()){
                    dataLists.put(String.valueOf(list.getKey()),new HashMap<Integer, String>());
                    for (DataSnapshot listData : list.getChildren()){
                        Log.d("","");
                        dataLists.get(list.getKey()).put(Integer.parseInt(listData.getKey()),String.valueOf(listData.getValue()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        greenStoryFirebaseDB.getReference("lists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot list  :  dataSnapshot.getChildren()){
                    HashMap<Integer,String> newHashMap = new HashMap<>();
                    for (DataSnapshot listData : list.getChildren()){
                        //dataLists.put(String.valueOf(list.getKey(), newHashMap.put(Integer.parseInt(listData.getKey()), String.valueOf(listData.getValue()));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isUserIdExists(SQLiteDatabase db) {
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

    private void showSiteDialog(final Marker siteMarker) {
        AlertDialog.Builder trackDialogBuilder = new AlertDialog.Builder(this);
        View siteDialogView = getLayoutInflater().inflate(R.layout.activity_home_track_dialog, null);
        Button goToSiteBtn = siteDialogView.findViewById(R.id.btn_go_to_track);
        TextView siteDescription = siteDialogView.findViewById(R.id.tv_dialog_track_details);
        closeTrackDialogImageButton = siteDialogView.findViewById(R.id.btn_close_track_dialog);
        trackDialogBuilder.setView(siteDialogView);
        final AlertDialog siteDialog = trackDialogBuilder.create();
        closeTrackDialogImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteDialog.dismiss();
            }
        });
        siteDialog.show();
        for (int i=0;i<integerSiteHashMap.size();i++) {
            if (siteMarker.getTitle().contentEquals(integerSiteHashMap.get(i).getSiteName())) {
                siteDescription.setText(integerSiteHashMap.get(i).getSiteDescription());
                break;
            }
        }

        siteDialog.setCancelable(true);
        siteDialog.setCanceledOnTouchOutside(true);
        goToSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                for (int i=0;i<integerSiteHashMap.size();i++){
                    if(siteMarker.getTitle().contentEquals(integerSiteHashMap.get(i).getSiteName())){
                        intent.putExtra("kmlResource", i);
                        break;
                    }
                }
                siteDialog.dismiss();
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

    private void showSitesListDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_home_sites_dialog, null);
        sites = mView.findViewById(R.id.lv_sites);
        Resources res = getResources();
        //sites.setAdapter(new ArrayAdapter<String>(this,
              //  R.layout.sites_list_item, res.getStringArray(R.array.sites_array)));
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
       // gallery.setAdapter(new ArrayAdapter<String>(this,
           //     R.layout.sites_list_item, res.getStringArray(R.array.sites_array)));
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


    class FetchIconsTask extends AsyncTask< HashMap<Integer,String>, Void, Void> {
        HashMap<Integer,Bitmap> integerBitmapHashMap = new HashMap<>();

        @Override
        protected Void doInBackground(HashMap<Integer, String>... hashMaps) {

            Bitmap markerIcon = null;
            for (HashMap.Entry<Integer,String> entry : hashMaps[0].entrySet()){
                URL url = null;
                try {
                    url = new URL(entry.getValue());
                    markerIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    e.printStackTrace();
                }
                integerBitmapHashMap.put(entry.getKey(),markerIcon);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initialiseSites(integerBitmapHashMap);
        }
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
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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
        protected Void doInBackground(User... user) {

            try {
                Utils.UpdateInitialScreenUser(user[0], mDb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
               showSitesListDialog();
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
           // intent.putExtra("site",sitesArray[position]);
            galleryDialog.dismiss();
            mDrawerLayout.closeDrawer(Gravity.START,true);
            startActivity(intent);
        }
    }
}



