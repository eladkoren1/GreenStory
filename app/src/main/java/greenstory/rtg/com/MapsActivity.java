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
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import greenstory.rtg.com.classes.Image;
import greenstory.rtg.com.classes.Question;
import greenstory.rtg.com.classes.Site;
import greenstory.rtg.com.classes.Track;
import greenstory.rtg.com.classes.User;
import greenstory.rtg.com.data.GreenStoryDbHelper;
import greenstory.rtg.com.data.ImagesContract;
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
    ImageButton takePictureBtn;
    Image image;
    Bitmap picture;
    Uri imageUri;
    String[] sitesArray;
    TextView questionsAnsweredTV;
    View view;
    HashMap<Integer,Site> integerSiteHashMap = new HashMap<>();
    ArrayList<Track> tracks = new ArrayList<Track>();
    Question question1;
    Question question2;
    Question question3;
    Question question4;
    Question question5;
    Question question6;
    Question question7;
    Question question8;

    HashMap<LatLng, Integer> tracksPlacemarksHashMap = new HashMap<LatLng, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Resources res = getResources();
        //sitesArray = res.getStringArray(R.array.sites_array);
        initialiseQuestions(sitesArray);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //questionsAnsweredTV = (TextView) findViewById(R.id.tv_questions_answered);
        kmlId = getIntent().getIntExtra("kmlResource",-1);
        takePictureBtn = (ImageButton)findViewById(R.id.btn_take_picture);
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent,1);

            }
        });

        GreenStoryDbHelper dbHelper = new GreenStoryDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        new DBLoadUserTask().execute(user,null,null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {


                            //Bitmap photo = (Bitmap) data.getExtras().get("data");
                            //imageUri = getImageUri(getApplicationContext(), photo);
                            try {
                               picture = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            image = new Image(sitesArray[kmlId],data.getData().toString());
                            new DBinsertImageDataToDbTask().execute(image,null,null);
                            showImageDialog();

                        }
                    });
                    thread.run();
                }
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initialiseSiteTracks();
        addSiteLayer();
        initiateLocation();
        //LoadTracksMarkers(siteLayer);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

    }

    public void addSiteLayer() {
        if (kmlId!=-1) {
            try {
                siteLayer = new KmlLayer(mMap, integerSiteHashMap.get(kmlId).getTracks().get(0).getKmlSource(), getApplicationContext());
                siteLayer.addLayerToMap();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, "מסלול לא זמין - מפה בפיתוח", Toast.LENGTH_LONG).show();
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
                            double delta5Lat = ((Math.abs(currentLatLng.latitude-question5.getLatLng().latitude)));
                            double delta6Lat = ((Math.abs(currentLatLng.latitude-question6.getLatLng().latitude)));
                            double delta7Lat = ((Math.abs(currentLatLng.latitude-question7.getLatLng().latitude)));
                            double delta8Lat = ((Math.abs(currentLatLng.latitude-question8.getLatLng().latitude)));
                            double delta5Lon = ((Math.abs(currentLatLng.longitude-question5.getLatLng().longitude)));
                            double delta6Lon = ((Math.abs(currentLatLng.longitude-question6.getLatLng().longitude)));
                            double delta7Lon = ((Math.abs(currentLatLng.longitude-question7.getLatLng().longitude)));
                            double delta8Lon = ((Math.abs(currentLatLng.longitude-question8.getLatLng().longitude)));
                            //BigDecimal bigDelta = BigDecimal.valueOf(delta);
                            String latitudeValue = currentLatLng.toString();
                            Log.d("current latlng",latitudeValue);
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
                            if ((delta5Lat<0.0001)&&(delta5Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question5);
                            }
                            if ((delta6Lat<0.0001)&&(delta6Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question6);
                            }
                            if ((delta7Lat<0.0001)&&(delta7Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question7);
                            }
                            if ((delta8Lat<0.0001)&&(delta8Lon<0.0001)){
                                Log.d("location delta", "enough");
                                showQuestionDialog(question8);
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

        integerSiteHashMap.put(0,
                new Site(1,"תוצרת הארץ",
                        "שכונת נחלת יצחק היא שכונה בדרום-מזרח תל אביב שהוקמה בשנת 1925 מזרחית לנחל איילון (ואדי מוסררה, כיום נתיבי איילון), על ידי קבוצת יהודים שבאו מקובנה. השכונה סמוכה לשכונות ביצרון ורמת ישראל",
                        new LatLng(32.0737617,34.7995856),
                        new MarkerOptions()
                                .position(new LatLng(32.0737617,34.7995856))
                                .title("תוצרת הארץ")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.totzeret_haaretz_72))));
        integerSiteHashMap.put(1,
                new Site(2,"המכללה האקדמית תל אביב יפו",
                        "האקדמית תל אביב-יפו הוקמה בשנת 1994 ביוזמה משותפת של אוניברסיטת תל אביב, עיריית תל אביב-יפו והוועדה לתכנון ולתקצוב של המועצה להשכלה גבוהה, כמוסד אקדמי ציבורי להשכלה גבוהה (האקדמית זכתה להכרה כמוסד להשכלה גבוהה ב-1996)",
                        new LatLng(32.0477291,34.7609729),
                        new MarkerOptions()
                                .position(new LatLng(32.0477291,34.7609729))
                                .title("המכללה האקדמית תל אביב יפו")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mta_72))));
        integerSiteHashMap.put(2,
                new Site(3,"שמורת הר ארבל",
                        "הגן הלאומי כולל בתוכו את רוב שטחו של הר הארבל, הר ניתאי, הר סביון, קרני חיטין ורמת ארבל. בשטח הגן הלאומי מסומנים שבילי טיול. השביל המוליך ממגרש החניה קצר ונוח להליכה. הוא עולה בשיפוע מתון עד אל שפת המצוק, המתנשא מעל סביבתו לגובה 400 מטר ומעניק מראות נוף למרחקים",
                        new LatLng(32.824166, 35.4986072),
                        new MarkerOptions()
                                .position(new LatLng(32.824166, 35.4986072))
                                .title("שמורת הר ארבל")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbel))));
        integerSiteHashMap.get(0).getTracks().add(new Track("רחוב תוצרת הארץ","",R.raw.totzeret_haaretz));
        integerSiteHashMap.get(1).getTracks().add(new Track("מקיף מכללה","",R.raw.academic_tlv));
        integerSiteHashMap.get(2).getTracks().add(new Track("סובב הר ארבל","",-1));

    }

    public void initialiseQuestions(String[] sitesArray){
        question1 = new Question(1,sitesArray[kmlId],
                new LatLng(32.0732843,34.7963482),
                "מהו הרחוב הוותיק ביותר בתל אביב?",
                "רחוב הרצל","שדרות קק''ל","רחוב אלנבי", "שדרות רוטשילד",
                1,false);

        question2 = new Question(2,sitesArray[kmlId],
                new LatLng(32.0731386,34.7949018),
                "איזו כתובת מהבאות יוצאת דופן",
                "כיכר קדומים 14","הרכב 8","יונה הנביא 6", "דוד חכמי 35",
                2,false);

        question3 = new Question(3,sitesArray[kmlId],
                new LatLng(32.0721558,34.7957219),
                "איזו שכונה הייתה מזוהה היסטורית עם מועדון הכדורגל שמשון תל אביב?",
                "יד אליהו","קריית שלום","כפר התימנים", "כפר שלם",
                3,false);

        question4 = new Question(4,sitesArray[kmlId],
                new LatLng(32.0723749,34.7973027),
                "איזו מחנויות הספרים הבאות לא נמצאות באלנבי?",
                "קדמת עדן","הלפר ספרים","לוטוס", "הנסיך הקטן",
                4,false);
        question5 = new Question(5,sitesArray[kmlId],
                new LatLng(32.04764, 34.76005),
                "באיזו שנה הוקם מסגד נוזהה בשדרות ירושלים?",
                "1933","1901","1940", "1970",
                1,false);
        question6 = new Question(6,sitesArray[kmlId],
                new LatLng(32.04733, 34.75951),
                "מה היה אחד השימושים העיקריים בנמל יפו בשנות החמישים?",
                "נמל דיג","נמל לחיל הים","ייצוא פרי הדר", "ייבוא בגדים",
                3,false);
        question7 = new Question(7,sitesArray[kmlId],
                new LatLng(32.04688, 34.75945),
                "מי היה האדריכל האחראי על הקמת בית הדואר ביפו?",
                "נעמי יודקובסקי","יצחק רפופורט","אליעזר ילין", "ויטוריו קורינלדי",
                2,false);
        question8 = new Question(8,sitesArray[kmlId],
                new LatLng(32.04661, 34.76),
                "באיזו שנה הוקמה המכללה האקדמית תל אביב יפו",
                "2004","1999","1994", "2000",
                3,false);
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
                Intent intent = new Intent(context,HomeMapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showImageDialog(){

        Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog_image, null);
        ImageView pictureView = (ImageView) mView.findViewById(R.id.iv_dialog_share);
        ImageButton shareButton = mView.findViewById(R.id.btn_share);
        dialog.setContentView(mView);
        pictureView.setImageBitmap(picture);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), picture,null, null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, bitmapUri );
                startActivity(Intent.createChooser(share, "Share using"));
            }
        });
        doKeepDialog(dialog);
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

                    //questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
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

                    //questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
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

                    //questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
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

                    //questionsAnsweredTV.setText(questionsAnswered+String.valueOf(questionsAnsweredNum));
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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

    public class DBinsertImageDataToDbTask extends AsyncTask<Image, Void, Void> {

        @Override
        protected Void doInBackground(Image... imagesArray) {

            try {
                Utils.insertImageDataToDb(imagesArray[0], mDb);
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

    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}

