package greenstory.rtg.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
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

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    KmlLayer siteLayer;
    Context context = this;
    private LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("location","current: " + String.valueOf(currentLatLng.toString())+
                    ", question: " + String.valueOf(question.getLatLng().toString()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15),1,null);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            if (currentLatLng.latitude-question.getLatLng().latitude<0.00000005){
                Log.d("location","found");
            }
        }
    };

    View view;

    LatLng currentLatLng;
    Question question = new Question(1,
            new LatLng(32.1788951,34.9118428),
            "why","a","b","c","d","a",false);

    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;

    HashMap<LatLng, Integer> tracksPlacemarksHashMap = new HashMap<LatLng, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
            siteLayer = new KmlLayer(mMap, R.raw.kfar_saba, getApplicationContext());
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
    void initiateLocation() {
        //locationManager.requestLocationUpdates(GPS_PROVIDER,1000,3, new android.location.LocationListener)

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(32.17939362, 34.91209629)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 5000, null);
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
            initiateLocation();
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
                initiateLocation();
            }
            else {
                return;
        }
    }

    private void showOutDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_out, null);
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
}