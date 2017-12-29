package greenstory.rtg.com;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    KmlLayer layer;
    Context context = this;
    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;
    HashMap<Integer, KmlPlacemark> placemarks = new HashMap<Integer, KmlPlacemark>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addsiteLayer();
        checkPermissions();//Checking for permissions and Initiating map properties
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if((isCoarseLocationGranted==false||isCoarseLocationGranted==false)){
                    showOutDialog();
                }
            }
        });
        layer.isLayerOnMap();
        LoadMarkers(layer);

    }

    public void addsiteLayer() {
        try {
            layer = new KmlLayer(mMap, R.raw.academic_tlv, getApplicationContext());
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadMarkers(KmlLayer kmlLayer) {
        if (kmlLayer.isLayerOnMap()) {
            if (kmlLayer.hasContainers()) {
                KmlContainer kmlContainer = kmlLayer.getContainers().iterator().next();
                if (kmlContainer.hasPlacemarks()) {
                    Iterable<KmlPlacemark> placemark = kmlContainer.getPlacemarks();
                    for (KmlPlacemark p : placemark) {
                        if (p.getGeometry().toString().contains("Point")) {
                            KmlPoint point = (KmlPoint) p.getGeometry();
                            LatLng latlng = new LatLng(point.getGeometryObject().latitude,point.getGeometryObject().longitude);
                            Log.d("latlng",point.toString());
                        }
                    }
                }
            }
        }
    }

    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
           // ActivityCompat.requestPermissions(this,
                  //  new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else {
            Toast.makeText(this,"fine location granted already",Toast.LENGTH_SHORT).show();
            isFineLocationGranted=true;
            mMap.setMyLocationEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
            // ActivityCompat.requestPermissions(this,
            //  new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else {
            Toast.makeText(this,"coarse location granted already",Toast.LENGTH_SHORT).show();
            isCoarseLocationGranted=true;
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isCoarseLocationGranted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isFineLocationGranted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                }
            }
            if (isFineLocationGranted&&isCoarseLocationGranted){ mMap.setMyLocationEnabled(true);}
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