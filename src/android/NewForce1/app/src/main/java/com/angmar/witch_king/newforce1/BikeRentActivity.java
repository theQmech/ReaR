package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;


public class BikeRentActivity extends AppCompatActivity {
    public String myurl;
    public static String bikeId;
    public static String userId;
    public static String standId;
    public static String EXTRA_MESSAGE;
    private BikeRentTask mRentTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_rent);
        Button rent = (Button) findViewById(R.id.btn_rent);
        //TextView userDetails = (TextView) findViewById(R.id.userDetails);
        rent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BikeRentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Rent this bike?");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mRentTask = new BikeRentTask(bikeId);
                                mRentTask.execute();
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();

                dialog.show();
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

            }
        });
//        bikeId = getIntent().getStringExtra(StandRentActivity.EXTRA_MESSAGE);
        userId = getIntent().getStringExtra("userId");
        standId = getIntent().getStringExtra("standId");
        myurl = LoginActivity.myurl;
        if (bikeId.contains("Nothing")){
            Log.e("MainActivity. OnCreate", "bikeId error");
        }
//        mRentTask = new BikeRentTask(bikeId);
//        mRentTask.execute();
    }

    public class BikeRentTask extends AsyncTask<String, Void, String> {
        private final String mbikeId;
        BikeRentTask(String username) {
            mbikeId = username;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("bikeId",mbikeId);
            HashMap<String , String> args = new HashMap<>();
            args.put("BikeID", bikeId);
            args.put("StandID",standId);
            args.put("UserID",userId);
            args.put("Op","rent");
            String urlLogin = myurl+ "RentOp";
            String sb = Helper.getResponse("POST",urlLogin,args);
            try {
                // Simulate network access.
//                URL url = new URL(urlLogin);
//                JSONObject loginData =new JSONObject();
//                loginData.put("BikeID", bikeId);
//                loginData.put("StandID",standId);
//                loginData.put("UserID",userId);
//                loginData.put("Op","rent");
//                Log.e("RentData",loginData.toString());
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setConnectTimeout(100);
//                conn.setReadTimeout(100);
//
//                OutputStream out = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
//                StringBuilder output = new StringBuilder();
//                boolean first = true;
//
//                Iterator<String> itr = loginData.keys();
//
//                while(itr.hasNext()){
//                    String key= itr.next();
//                    Object value = loginData.get(key);
//
//                    if (first) {
//                        first = false;
//                    }
//                    else {
//                        output.append("&");
//                    }
//                    output.append(URLEncoder.encode(key, "UTF-8"));
//                    output.append("=");
//                    output.append(URLEncoder.encode(value.toString(), "UTF-8"));
//
//                }
//                writer.write(output.toString());
//
//                writer.flush();
//                writer.close();

                //tring sb = Helper.getResponse("POST",urlLogin,args);
                    JSONObject myob = new JSONObject(sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("LoginActivity","Wrong");
                        return new String("false:"+ myob.getString("message"));
                    }
                    else {
                        Log.e("LoginActivity","Present");

                        return sb.toString();
                    }
//                }
//                else{
//                    Log.e("LoginActivity","Error");
//                    return new String("false: "+ responseCode);
//                }
            } catch (Exception e) {
                Log.e("LoginActivity","Exception" + e.getLocalizedMessage());
                return new String("false:Exception Occured " + e.getLocalizedMessage());
            }
//            finally {
//                if (conn == null) {
//                    conn.disconnect();
//                    Log.e("LoginActivity", "Can't Connect to the network");
//                    return new String("Can't Connect");
//                }
//            }
        }

        @Override
        protected void onPostExecute(String res) {
            if(res.startsWith("false")) {
                Helper.makeToast(getApplicationContext(),res.substring(6));
                Intent add = new Intent(BikeRentActivity.this, UserMainActivity.class);
                add.putExtra("userId", userId);
                add.putExtra("rented", bikeId);
                setResult(Activity.RESULT_OK,add);
                finish();
            }
            else {
                try {
                    JSONObject myob = new JSONObject(res);
                    String code = myob.getString("data");


                        AlertDialog.Builder builder = new AlertDialog.Builder(BikeRentActivity.this);
                        builder.setTitle("Ride Successfully Rented");
                        builder.setMessage("Unlock you ride using the code: "+code+". You can always find this later under MyRides section, in case you forget it.");
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent add = new Intent(BikeRentActivity.this, UserMainActivity.class);
                                add.putExtra("userId", userId);
                                add.putExtra("rented", bikeId);
                                setResult(Activity.RESULT_OK,add);
                                finish();
                            }
                        });
                    builder.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent add = new Intent(BikeRentActivity.this, UserMainActivity.class);
                    add.putExtra("userId", userId);
                    add.putExtra("rented", bikeId);
                    setResult(Activity.RESULT_OK,add);
                    finish();
                }

            }
        }
    }
}
