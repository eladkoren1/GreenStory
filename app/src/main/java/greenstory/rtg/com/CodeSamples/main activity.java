/*package com.example.android.greenstory;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView rfQ, rfA1, rfA2, rfA3;//questions
    RadioButton rb1, rb2, rb3;    //buttons
    int btAnswer = 0;
    int correct = 0;
    int remark = 100;//100%

    EditText newUser;//////////////////////test
    EditText userID;//////////////////////test
    String name="";/////////////////////////input
    int uid = 0; /////////////////////input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rfQ = (TextView)findViewById(R.id.question);
        rfA1 = (TextView)findViewById(R.id.answer1);
        rfA2 = (TextView)findViewById(R.id.answer2);
        rfA3 = (TextView)findViewById(R.id.answer3);

        rb1 = (RadioButton) findViewById(R.id.radioButton1);
        rb2 = (RadioButton) findViewById(R.id.radioButton2);
        rb3 = (RadioButton) findViewById(R.id.radioButton3);

        newUser=(EditText) findViewById(R.id.new_user);////////////test
        userID=(EditText) findViewById(R.id.user_id);////////////test

        name="Elad"; //input!!!!!!!!!!!
        uid=101; //input!!!!!!!!!!!
        new getQuiz().execute("http://eladko.mtacloud.co.il/quiz.php","7","7");//url, lat longitude input!!!!!!!!!!!

    }

    public void rbClick(View v){

        rb1.setChecked(false);
        rb2.setChecked(false);
        rb3.setChecked(false);

        switch (v.getId())
        {
            case R.id.radioButton1:  rb1.setChecked(true); btAnswer = 1; break;
            case R.id.radioButton2:  rb2.setChecked(true); btAnswer = 2; break;
            case R.id.radioButton3:  rb3.setChecked(true); btAnswer = 3; break;
        }

    }

    public void send(View v){

        switch (correct)
        {
            case 1:  if(rb1.isChecked()) {
                        Toast.makeText(getBaseContext(),"Correct Answer! remark: "+remark,Toast.LENGTH_LONG).show();
                        new SendAnswer().execute("http://eladko.mtacloud.co.il/user.php",uid+"",remark+"");//send remark
                     }
                     else{
                        remark -= 20;
                        Toast.makeText(getBaseContext(),"Wrong Answer",Toast.LENGTH_LONG).show();

                    }
                     break;
            case 2:  if(rb2.isChecked()) {
                        Toast.makeText(getBaseContext(),"Correct Answer! remark: "+remark,Toast.LENGTH_LONG).show();
                        new SendAnswer().execute("http://eladko.mtacloud.co.il/user.php",uid+"",remark+"");//send remark
                    }
                    else{
                        remark -= 20;
                        Toast.makeText(getBaseContext(),"Wrong Answer",Toast.LENGTH_LONG).show();
                    }
                    break;
            case 3:  if(rb3.isChecked()) {
                        Toast.makeText(getBaseContext(),"Correct Answer! remark: "+remark,Toast.LENGTH_LONG).show();
                        new SendAnswer().execute("http://eladko.mtacloud.co.il/user.php",uid+"",remark+"");//send remark
            }
                    else{
                        remark -= 20;
                        Toast.makeText(getBaseContext(),"Wrong Answer",Toast.LENGTH_LONG).show();
                    }
                    break;
        }

    }

    public void signIn(View v){


        new newUserTask().execute("http://eladko.mtacloud.co.il/user.php",userID.getText()+"",newUser.getText()+"");// input from Elad xml!!!!!!!!!!!

    }

    private class getQuiz extends AsyncTask<String, String, String>
    {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            String result = null;

            try {


                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                String postParameters = "lat=" + args[1] + "&longitude=" + args[2];

                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());// request to server
                out.print(postParameters);
                out.close();


                //---------- we sent request and now we waiting for response on hold
                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); //input of connection -response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));// taking out the data and put them as string
                result = reader.readLine();// parameter who got json as array


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            super.onPostExecute(jsonResult);

            try{
                JSONArray jsonArray = new JSONArray(jsonResult);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    String id = jsonobject.getString("id");
                    String info = jsonobject.getString("info");
                    String question = jsonobject.getString("question");
                    String answer1 = jsonobject.getString("answer1");
                    String answer2 = jsonobject.getString("answer2");
                    String answer3 = jsonobject.getString("answer3");
                    correct = jsonobject.getInt("correct");//global answer

                    rfQ.setText(question);
                    rfA1.setText(answer1);
                    rfA2.setText(answer2);
                    rfA3.setText(answer3);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private class SendAnswer extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            String result = null;

            try {


                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                String postParameters = "uid=" + args[1]  + "&remark=" + args[2];


                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());// request to server
                out.print(postParameters);
                out.close();


                //---------- we sent the answer and now we waiting for the result
                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); //input of connection -response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));// taking out the data and put them as string
                result = reader.readLine();//right or wrong


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

           // Toast.makeText(getBaseContext(),"Result: "+result,Toast.LENGTH_LONG).show();

        }

    }


    private class newUserTask extends AsyncTask<String, String, String>
    {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {
            String result = null;

            try {


                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                String postParameters = "new_uid=" + args[1] + "&new_user=" + args[2];////// all parameters!!!!!

                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());// request to server
                out.print(postParameters);
                out.close();



                InputStream in = new BufferedInputStream(urlConnection.getInputStream()); //input of connection -response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));// taking out the data and put them as string
                result = reader.readLine();


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Toast.makeText(getBaseContext(),"Result: "+result,Toast.LENGTH_LONG).show();

        }

    }



}
*/