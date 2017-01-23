package com.angmar.witch_king.newforce1;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
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

public class StandRentActivity extends AppCompatActivity{
    public String myurl;
    public String standId;
    private StandRidesTask mRideTask = null;
    private View rootView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stand_rent);

        // my_child_toolbar is defined in the layout file
        setTitle("Rides Available Here");
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myurl = LoginActivity.myurl;
        standId = getIntent().getStringExtra("standId");
        mRideTask = new StandRidesTask();
        mRideTask.execute();
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


    public class StandRidesTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "RidesAtStand?standid="+standId;
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("standid", standId);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                Log.e("HTTP Response",conn.getResponseMessage());
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
                    Log.e("Server Returned",sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("StandRentActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("StandRentActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("StandRentActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("StandRentActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("StandRentActivity","Can't Connect to the network");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject myob) {
            mRideTask = null;
//            String name,address,location,numbikes,standId;

//            showProgress(false)
            try {
                Log.e("StandRideTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    String [] model = new String[array.length()];
                    String [] color = new String[array.length()];
                    String [] rideStatus = new String[array.length()];
                    String [] rideId = new String[array.length()];
                    Integer image_id[] = new Integer[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        // name = row.getString("name");
                        model[i] = row.getString("model");
                        color[i] = row.getString("color");
//                        rideStatus[i] = row.getString("status");
                        image_id[i] = R.drawable.ic_directions_bike_black_24dp;
                        rideId[i] = row.getString("rideid");
                    }

                    AndroidListAdapter_unrentRides androidListAdapter = new AndroidListAdapter_unrentRides(StandRentActivity.this, image_id, model, color, rideId);
                    ListView androidListView = (ListView) findViewById(R.id.stand_rides);
                    androidListView.setAdapter(androidListAdapter);

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("StandRentActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }

}
