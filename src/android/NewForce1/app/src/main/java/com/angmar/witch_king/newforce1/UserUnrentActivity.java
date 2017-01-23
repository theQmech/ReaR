package com.angmar.witch_king.newforce1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
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

public class UserUnrentActivity extends AppCompatActivity {
    public String myurl;
    private String standId;
    private UnrentRideTask mRideTask = null;
    private UnrentTask mUnrentTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("TAG", "Inside UserRentActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_unrent);

        // my_child_toolbar is defined in the layout file
        setTitle("Select Ride to Unrent");
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        standId = getIntent().getStringExtra("standId");
        myurl = LoginActivity.myurl;

        mRideTask = new UnrentRideTask();
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

    public class UnrentRideTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "RentedRides";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                boolean first = true;

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
                    if (myob.getString("status").contains("false")) {
                        Log.e("UserUnrentActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("UserUnrentActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("UnrentRideTask","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("UserUnrentActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("UserUnrentActivity","Can't Connect to the network");
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
                Log.e("UnrentRideTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    String [] model = new String[array.length()];
                    String [] color = new String[array.length()];
                    String [] code = new String[array.length()];
                    Integer image_id[] = new Integer[array.length()];
                    String[] rideId = new String[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        // name = row.getString("name");
                        model[i] = row.getString("model");
                        color[i] = row.getString("color");
                        rideId[i] = row.getString("rideid");
                        code[i] =  row.getString("code");
//                        rideStatus[i] = row.getString("status");
                        image_id[i] = R.drawable.ic_directions_bike_black_24dp;

                    }

                    AndroidListAdapter_standRides androidListAdapter = new AndroidListAdapter_standRides(UserUnrentActivity.this, image_id, model, color, rideId,code);
                    final ListView androidListView = (ListView) findViewById(R.id.custom_listview_example);
                    androidListView.setAdapter(androidListAdapter);
                    androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String rideId_unrent = (String) view.getTag();
                            mUnrentTask = new UnrentTask(standId,rideId_unrent);
                            mUnrentTask.execute();
//                            prestationEco str=(prestationEco)o;//As you are using Default String Adapter
//                            Toast.makeText(getBaseContext(),str.getTitle(),Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("UserUnrentActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }


    public class UnrentTask extends AsyncTask<String, Void, String> {

        private final String mStandId;
        private final String mRideId;

        UnrentTask(String username, String password) {
            mStandId = username;
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
                loginData.put("standid", mStandId);
                loginData.put("rideid",mRideId);
                loginData.put("op","unrent");
                Log.e("loginData",loginData.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

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
                        Log.e("UserUnrentActivity","Wrong");
                        return new String("false:"+ myob.getString("message"));
                    }
                    else {
                        Log.e("UserUnrentActivity","Present");

                        return new String("true "+ myob.getString("message"));
                    }
                }
                else{
                    Log.e("UserUnrentActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("UserUnrentActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("UserUnrentActivity","Can't Connect to the network");
                    return new String("Can't Connect");
                }
            }
        }

        @Override
        protected void onPostExecute(String success) {
            mUnrentTask = null;
            Intent screenChange=null;
            Log.e("UserLoginTask",success);
            if (success.startsWith("true")) {
//                    screenChange = new Intent(UserUnrentActivity.this,HomeActivity.class);
                    Helper.makeToast(getApplicationContext(),"Ride Sucessfully Unrented");
                }
            else {
                Helper.makeToast(getApplicationContext(),"Ride Unrent Failed!");
            }
                screenChange = new Intent(UserUnrentActivity.this,HomeActivity.class);
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.setType("text/plain");
                startActivity(screenChange);

        }
    }
}