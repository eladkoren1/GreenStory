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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import greenstory.rtg.com.classes.Question;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    KmlLayer siteLayer;
    Context context = this;
    private LocationManager locationManager;

    View view;

    LatLng currentLatLng;
    Question question1 = new Question(1,
            new LatLng(32.0732843,34.7963482),
            "מהו הרחוב הוותיק ביותר בתל אביב?","רחוב הרצל","שדרות קק''ל","רחוב אלנבי",
            "שדרות רוטשילד",1,false);
    Question question2 = new Question(2,
            new LatLng(32.0731386,34.7949018),
            "איזו כתובת מהבאות יוצאת דופ]ן","תשובה א'","תשובה ב'","תשובה ג'",
            "תשובה ד'",2,false);
    Question question3 = new Question(3,
            new LatLng(32.0721558,34.7957219),
            "שאלה","תשובה א'","תשובה ב'","תשובה ג'",
            "תשובה ד'",3,false);
    Question question4 = new Question(4,
            new LatLng(32.0723749,34.7973027),
            "שאלה","תשובה א'","תשובה ב'","תשובה ג'",
            "תשובה ד'",4,false);
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
    void initiateLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(32.17939362, 34.91209629)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 3000,
                new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                locationManager.requestLocationUpdates(GPS_PROVIDER,5000,3,
                        new android.location.LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                //Log.d("location","current: " + String.valueOf(currentLatLng.toString())+
                                        //", question: " + String.valueOf(question.getLatLng().toString()));
                                currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                                double delta1Lat = ((Math.abs(currentLatLng.latitude-question1.getLatLng().latitude)));
                                double delta2Lat = ((Math.abs(currentLatLng.latitude-question2.getLatLng().latitude)));
                                double delta3Lat = ((Math.abs(currentLatLng.latitude-question3.getLatLng().latitude)));
                                double delta4Lat = ((Math.abs(currentLatLng.latitude-question4.getLatLng().latitude)));
                                double delta1Lon = ((Math.abs(currentLatLng.longitude-question1.getLatLng().longitude)));
                                double delta2Lon = ((Math.abs(currentLatLng.longitude-question2.getLatLng().longitude)));
                                double delta3Lon = ((Math.abs(currentLatLng.longitude-question3.getLatLng().longitude)));
                                double delta4Lon = ((Math.abs(currentLatLng.longitude-question4.getLatLng().longitude)));
                                BigDecimal bigDelta = BigDecimal.valueOf(delta);
                                String latitudeValue = currentLatLng.toString();
                                //Log.d("current latlng",latitudeValue);
                                //Log.d("location delta",bigDelta.toString());
                                if (delta<0.0001){
                                    Log.d("location delta", "enough");
                                    showQuestionDialog(question);
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
            }
            @Override
            public void onCancel() {

            }
        });
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

    private void showQuestionDialog(final Question question){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_question, null);
        TextView tvQuestion = (TextView) mView.findViewById(R.id.tv_dialog_question);
        Button btnSubmitAnswer = (Button) mView.findViewById(R.id.btn_question_answer);
        final RadioButton rbAnswerA = (RadioButton) mView.findViewById(R.id.rb_answer_a);
        final RadioButton rbAnswerB = (RadioButton) mView.findViewById(R.id.rb_answer_b);
        final RadioButton rbAnswerC = (RadioButton) mView.findViewById(R.id.rb_answer_c);
        final RadioButton rbAnswerD = (RadioButton) mView.findViewById(R.id.rb_answer_d);
        tvQuestion.setText(question.getQuestion());
        rbAnswerA.setText(question.getAnswerA());
        rbAnswerB.setText(question.getAnswerB());
        rbAnswerC.setText(question.getAnswerC());
        rbAnswerD.setText(question.getAnswerD());
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        if (!question.isAnswered()){
            dialog.show();
            question.setIsAnswered(true);
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbAnswerA.isChecked()) {
                    if (question.getCorrectAnswer() == 1) {
                        Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_LONG).show();
                        question.setIsAnswered(true);
                        dialog.dismiss();
                        return;
                    }
                    return;
                }
                if (rbAnswerB.isChecked()) {
                    if (question.getCorrectAnswer() == 2) {
                        Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_LONG).show();
                        question.setIsAnswered(true);
                        dialog.dismiss();
                        return;
                    }
                    return;
                }
                if (rbAnswerC.isChecked()) {
                    if (question.getCorrectAnswer() == 3) {
                        Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_LONG).show();
                        question.setIsAnswered(true);
                        dialog.dismiss();
                        return;
                    }
                    return;
                }
                if (rbAnswerD.isChecked()) {
                    if (question.getCorrectAnswer() == 4) {
                        Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_LONG).show();
                        question.setIsAnswered(true);
                        dialog.dismiss();
                        return;
                    }
                    return;
                }
            }
        });
    }

}