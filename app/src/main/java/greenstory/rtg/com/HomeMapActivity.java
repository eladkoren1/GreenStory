package greenstory.rtg.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import greenstory.rtg.com.classes.Question;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

import static android.location.LocationManager.GPS_PROVIDER;

public class HomeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    User user = new User();
    private GoogleMap mMap;
    LatLng centerLatLng;
    LatLng northAreaLatLng = new LatLng(32.852164,35.3456053);
    LatLng centerAreaLatLng = new LatLng(31.831879,35.0166593);
    LatLng southAreaLatLng = new LatLng(30.5885849,34.8013069);
    KmlLayer siteLayer;
    Context context = this;
    private LocationManager locationManager;
    private SQLiteDatabase mDb;
    Cursor cursor;

    TextView questionsAnsweredTV;
    View view;



    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;

    HashMap<LatLng, Integer> tracksPlacemarksHashMap = new HashMap<LatLng, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this,
                UsersContract.UserEntry.SQL_CREATE_USERS_TABLE);
        mDb = dbHelper.getWritableDatabase();

        new DBLoadUserTask().execute(user,null,null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initiateLocation(mMap);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        addSiteLayer();
        LoadTracksMarkers(siteLayer);
        checkPermissions();//Checking for permissions and Initiating map properties
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
            siteLayer = new KmlLayer(mMap, R.raw.totzeret_haaretz, getApplicationContext());
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
                                                        tracksPlacemarksHashMap.put(latLng, 1);
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
        map.setMyLocationEnabled(true);
        LatLng centerLatLng = new LatLng(31.366385,35.0994833);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,7.4f));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(northAreaLatLng,10f));
    }

    private void checkPermissions() {
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
            initiateLocation(mMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(isFineLocationGranted&&isFineLocationGranted){
            return;
        }
        else {
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
                        }
                        else {
                            return;
                        }
                    }
                }
            }
            if (isFineLocationGranted && isCoarseLocationGranted) {
                Log.d("Initiation: ","permissions results");
                initiateLocation(mMap);
            }
            else {
                return;
        }
    }

    private void showOutDialog(){
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
                Intent intent = new Intent(context,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public class DBUserUpdateTask extends AsyncTask<User, Void, Void> {

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

    public class DBLoadUserTask extends AsyncTask<User, Void, Void> {

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
}