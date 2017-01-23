package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class RentBikeActivity extends AppCompatActivity {

    public String rideType;
    public String rideModel;
    public String rideColor;
    public String rideStatus;
    public String rideURL;
    public String myurl;
    private RentRideTask mAuthTask;
    private TextView model;
    private TextView color;
    private TextView type;
    private ImageView imageView;
    private RentTask mRentTask = null;
    private String rideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bike);


        // my_child_toolbar is defined in the layout file
        setTitle("Rent Bike");
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myurl = LoginActivity.myurl;
        imageView = (ImageView) findViewById(R.id.thumbnail);
        imageView.setImageResource(R.mipmap.ic_launcher);
        model = (TextView) findViewById(R.id.model);
        color = (TextView) findViewById(R.id.color);
        type = (TextView) findViewById(R.id.type);
        rideID = getIntent().getStringExtra("bikeId");
        Button rentBtn = (Button) findViewById(R.id.rent);
        rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRentTask = new RentTask(rideID);
                mRentTask.execute();

            }
        });
        model.setText(getIntent().getStringExtra("rideModel"));
        color.setText(getIntent().getStringExtra("rideColor"));
        type.setText(getIntent().getStringExtra("rideType"));
        int loader = R.mipmap.ic_launcher;
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.DisplayImage(getIntent().getStringExtra("rideURL"), loader, imageView);
//        mAuthTask = new RentRideTask(rideID);
//        mAuthTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RentRideTask extends AsyncTask<String, Void, String> {

        private final String rideID;

        RentRideTask(String ride) {
            rideID = ride;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "Ride?rideid="+rideID;
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("rideid", rideID);
                Log.e("Rent Bike Details",loginData.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    StringBuffer sb = new StringBuffer("");
                    InputStream in = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    int data = isr.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isr.read();
                        sb.append(current);
                    }
                    in.close();
                    JSONObject myob = new JSONObject(sb.toString());
                    Log.e("Server Response",sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("RentBikeActivity","Wrong");
                        return new String("false:"+ myob.getString("message"));
                    }
                    else {
                        Log.e("RentBikeActivity","Present");
                        JSONObject array = myob.getJSONObject("data");
                        rideType=array.getString("type");
                        rideColor=array.getString("color");
                        rideModel=array.getString("model");
                        rideStatus=array.getString("status");
                        rideURL = array.getString("url");
                        return new String("true: "+ myob.getString("data"));
                    }
                }
                else{
                    Log.e("RentBikeActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("RentBikeActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("RentBikeActivity","Can't Connect to the network");
                    return new String("Can't Connect");
                }
            }
//            return new String("false");
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            if(success.contains("true")){
                model.setText(rideModel);
                color.setText(rideColor);
                type.setText(rideType);
                String code = success.substring(6);
                int loader = R.mipmap.ic_launcher;
                ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                imgLoader.DisplayImage(rideURL, loader, imageView);
            } else {
//                TODO
                Intent screenChange=null;
                screenChange = new Intent(RentBikeActivity.this,HomeActivity.class);
                Toast.makeText(getApplicationContext(), success.substring(6), Toast.LENGTH_SHORT).show();
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.setType("text/plain");
                startActivity(screenChange);

            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public class RentTask extends AsyncTask<String, Void, String> {

        private final String mRideId;

        RentTask(String password) {
            mRideId = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "RentUnrent";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("rideid",mRideId);
                loginData.put("op","rent");
                Log.e("loginData",loginData.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                StringBuilder output = new StringBuilder();
                boolean first = true;

                Iterator<String> itr = loginData.keys();

                while(itr.hasNext()){
                    String key= itr.next();
                    Object value = loginData.get(key);

                    if (first) {
                        first = false;
                    }
                    else {
                        output.append("&");
                    }
                    output.append(URLEncoder.encode(key, "UTF-8"));
                    output.append("=");
                    output.append(URLEncoder.encode(value.toString(), "UTF-8"));

                }
                writer.write(output.toString());

                writer.flush();
                writer.close();
                out.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    StringBuffer sb = new StringBuffer("");
                    InputStream in = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    int data = isr.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isr.read();
                        sb.append(current);
                    }
                    in.close();
                    JSONObject myob = new JSONObject(sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("RentBikeActivity","Wrong");
                        return new String("false:"+ myob.getString("message"));
                    }
                    else {
                        Log.e("RentBikeActivity","Present");

                        return new String("true: "+ myob.getString("data"));
                    }
                }
                else{
                    Log.e("RentBikeActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("RentBikeActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(String success) {
            mRentTask = null;
            Intent screenChange=null;
            Log.e("RentTask",success);
            if (success.startsWith("true")) {
//                    screenChange = new Intent(UserUnrentActivity.this,HomeActivity.class);
                String code = success.substring(6);

                AlertDialog.Builder builder = new AlertDialog.Builder(RentBikeActivity.this);
                builder.setTitle("Ride Successfully Rented");
                builder.setMessage("Unlock you ride using the code: "+code+". You can always find this later under MyRides section, in case you forget it.");
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent add = new Intent();
                        setResult(Activity.RESULT_OK,add);
                        finish();
                    }
                });
                builder.show();

            }
            else {
                Helper.makeToast(getApplicationContext(),"Ride Rent Failed!");

                Intent add = new Intent();
                setResult(Activity.RESULT_OK,add);
                finish();
            }


        }
    }


}
