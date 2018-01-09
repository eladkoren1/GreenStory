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
import greenstory.rtg.com.classes.Site;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.UsersContract;
import greenstory.rtg.com.data.Utils;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    User user = new User();
    private GoogleMap mMap;
    LatLng currentLatLng;
    KmlLayer siteLayer;
    Context context = this;
    private LocationManager locationManager;
    private SQLiteDatabase mDb;
    Cursor cursor;
    int kmlId;

    TextView questionsAnsweredTV;
    View view;
    HashMap<Integer,Site> intSiteHashMap = new HashMap<>();


    Question question1 = new Question(1,
        new LatLng(32.0732843,34.7963482),
        "מהו הרחוב הוותיק ביותר בתל אביב?",
        "רחוב הרצל","שדרות קק''ל","רחוב אלנבי", "שדרות רוטשילד",
        1,false);

    Question question2 = new Question(2,
        new LatLng(32.0731386,34.7949018),
        "איזו כתובת מהבאות יוצאת דופן",
        "כיכר קדומים 14","הרכב 8","יונה הנביא 6", "דוד חכמי 35",
        2,false);

    Question question3 = new Question(3,
        new LatLng(32.0721558,34.7957219),
        "איזו שכונה הייתה מזוהה היסטורית עם מועדון הכדורגל שמשון תל אביב?",
        "יד אליהו","קריית שלום","כפר התימנים", "כפר שלם",
        3,false);

    Question question4 = new Question(4,
        new LatLng(32.0723749,34.7973027),
        "איזו מחנויות הספרים הבאות לא נמצאות באלנבי?",
        "קדמת עדן","הלפר ספרים","לוטוס", "הנסיך הקטן",
        4,false);

    boolean isCoarseLocationGranted = false;
    boolean isFineLocationGranted = false;

    int questionsAnsweredNum = 0;
    String questionsAnswered = "מס' שאלות שעניתי: ";

    HashMap<LatLng, Integer> tracksPlacemarksHashMap = new HashMap<LatLng, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        questionsAnsweredTV = (TextView) findViewById(R.id.tv_questions_answered);
        kmlId = getIntent().getIntExtra("kmlResource",-1);


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
        initialiseSiteTracks();
        addSiteLayer();
        //LoadTracksMarkers(siteLayer);
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
        if (kmlId!=-1) {
            try {
                siteLayer = new KmlLayer(mMap, intSiteHashMap.get(kmlId).getKmlSource(), getApplicationContext());
                siteLayer.addLayerToMap();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                Log.d("reason","developing map");
                super.onBackPressed();

            }
        }
        else{
            super.onBackPressed();
        }
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
                            //BigDecimal bigDelta = BigDecimal.valueOf(delta);
                            String latitudeValue = currentLatLng.toString();
                            //Log.d("current latlng",latitudeValue);
                            //Log.d("location delta",bigDelta.toString());
                            if ((delta1Lat<0.0001)&&(delta1Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question1);
                            }
                            if ((delta2Lat<0.0001)&&(delta2Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question2);
                            }
                            if ((delta3Lat<0.0001)&&(delta3Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question3);
                            }
                            if ((delta4Lat<0.0001)&&(delta4Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question4);
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

    public void initialiseSiteTracks() {

        intSiteHashMap.put(0,
                new Site("תוצרת הארץ",
                        R.raw.totzeret_haaretz,
                        "data"));

    }

    private void checkPermissions() {
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
                    Toast.makeText(context, "תשובה נכונה! +10 נקודות", Toast.LENGTH_SHORT).show();
                    question.setIsAnswered(true);
                    dialog.dismiss();
                    questionsAnsweredNum++;
                    questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
                    int newPoints = user.getPoints()+10;
                    user.setPoints(newPoints);
                    new MapsActivity.DBUserUpdateTask().execute(user,null,null);
                    return;
                }
                return;
            }
            if (rbAnswerB.isChecked()) {
                if (question.getCorrectAnswer() == 2) {
                    Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_SHORT).show();
                    question.setIsAnswered(true);
                    dialog.dismiss();
                    questionsAnsweredNum++;
                    questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
                    int newPoints = user.getPoints()+10;
                    user.setPoints(newPoints);
                    new MapsActivity.DBUserUpdateTask().execute(user,null,null);
                    return;
                }
                return;
            }
            if (rbAnswerC.isChecked()) {
                if (question.getCorrectAnswer() == 3) {
                    Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_SHORT).show();
                    question.setIsAnswered(true);
                    dialog.dismiss();
                    questionsAnsweredNum++;
                    questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
                    int newPoints = user.getPoints()+10;
                    user.setPoints(newPoints);
                    new MapsActivity.DBUserUpdateTask().execute(user,null,null);
                    return;
                }
                return;
            }
            if (rbAnswerD.isChecked()) {
                if (question.getCorrectAnswer() == 4) {
                    Toast.makeText(context, "תשובה נכונה!", Toast.LENGTH_SHORT).show();
                    question.setIsAnswered(true);
                    dialog.dismiss();
                    questionsAnsweredNum++;
                    questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
                    int newPoints = user.getPoints()+10;
                    user.setPoints(newPoints);
                    new MapsActivity.DBUserUpdateTask().execute(user,null,null);
                    return;
                }
                return;
            }
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