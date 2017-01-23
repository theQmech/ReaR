package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class UserRentActivity extends Activity {

    public String myurl;
    public static String userId;
    public static String EXTRA_MESSAGE;
    TableLayout table_layout;
    private UserRentTask mTableTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("TAG", "Inside UserRentActivity.onCreate");
        super.onCreate(savedInstanceState);
        setTitle("Pickup Locations");
        setContentView(R.layout.activity_user_rent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        table_layout = (TableLayout) findViewById(R.id.table_layout);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        userId = getIntent().getStringExtra("userId");
        myurl = LoginActivity.myurl;
        if (userId.contains("Nothing")){
            Log.e("MainActivity. OnCreate", "userId error");
        }
//        makeHeader();
        mTableTask = new UserRentTask(userId);
        mTableTask.execute();
    }

    public void makeHeader() {
//        TODO Use Rtype to create Header
        Log.v("TAG", "Inside UserRentActivity.makeHeader");
        TableRow row = new TableRow(UserRentActivity.this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        TextView heading = new TextView(UserRentActivity.this);
        heading.setLayoutParams(lp);
//        heading.setText("StandId");
//        heading.setPadding(2,2,2,2);
//        row.addView(heading);

//        TextView heading0 = new TextView(UserRentActivity.this);
//        heading0.setLayoutParams(lp);
//        heading0.setText("Name");
//        heading0.setPadding(2,2,2,2);
////        if(row.getParent()!=null)
////            row.getParent().removeView(row);
//        row.addView(heading0);

        TextView heading1 = new TextView(UserRentActivity.this);
        heading1.setLayoutParams(lp);
        heading1.setText("Address");
        heading1.setPadding(2,2,2,2);
        heading1.setTextSize(18);
        heading1.setTypeface(Typeface.DEFAULT_BOLD);
        row.addView(heading1);

//        TextView heading2 = new TextView(UserRentActivity.this);
//        heading2.setLayoutParams(lp);
//        heading2.setText("Location");
//        heading2.setPadding(2,2,2,2);
//        row.addView(heading2);

        TextView heading5 = new TextView(UserRentActivity.this);
        heading5.setLayoutParams(lp);
        heading5.setText("Bikes Available");
        heading5.setPadding(2,2,2,2);
        heading5.setTextSize(18);
        heading5.setTypeface(Typeface.DEFAULT_BOLD);
        row.addView(heading5);
        table_layout.addView(row);
    }

    public class UserRentTask extends AsyncTask<String, Void, JSONObject> {
        private final String mUsername;
        private final String mLocation;
        UserRentTask(String username) {
            mUsername = username;
            mLocation = "";
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "Stands";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("id", mUsername);
                loginData.put("location",mLocation);
                Log.e("loginData",loginData.toString());
                conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestProperty("Content-Type", "application/json");
//                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");
//                conn.setRequestProperty("Content-length", "0");
//                conn.setUseCaches(false);
//                conn.setAllowUserInteraction(false);
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setConnectTimeout(100000);
//                conn.setReadTimeout(100000);
//                conn.connect();
//                HttpClient httpclient = new DefaultHttpClient();
////
//                // make GET request to the given URL
//                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));


//                OutputStream out = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
//                StringBuilder output = new StringBuilder();
                boolean first = true;

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
//                out.close();
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
                        Log.e("UserRentActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("LoginActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("LoginActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("LoginActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("LoginActivity","Can't Connect to the network");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject myob) {
            mTableTask = null;
//            String name,address,location,numbikes,standId;

//            showProgress(false)
            try {
                Log.e("UserRentTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    String [] address = new String[array.length()];
                    String [] location = new String[array.length()];
                    String [] numbikes = new String[array.length()];
                    String [] standId = new String[array.length()];
                    Integer image_id[] = new Integer[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                       // name = row.getString("name");
                        address[i] = row.getString("address");
//                        location[i] = row.getString("location");
                        numbikes[i] = row.getString("numbikes");
                        standId[i] = row.getString("standid");
                        image_id[i] = R.mipmap.ic_launcher;
//                        final TableRow tbrow = new TableRow(UserRentActivity.this);
////                        TextView t1v = new TextView(UserRentActivity.this);
////                        t1v.setText(standId);
//                        //                t1v.setTextColor(Color.WHITE);
//                        //                t1v.setGravity(Gravity.CENTER);
////                        tbrow.addView(t1v);
////                        TextView t2v = new newTextView(UserRentActivity.this);
////                        t2v.setText(name);
////                        //                t2v.setTextColor(Color.WHITE);
////                        //                t2v.setGravity(Gravity.CENTER);
////                        tbrow.addView(t2v);
//                        TextView t3v = new TextView(UserRentActivity.this);
//                        t3v.setText(address);
//                        t3v.setTextSize(18);
//                        //                t3v.setTextColor(Color.WHITE);
//                        //                t3v.setGravity(Gravity.CENTER);
//                        tbrow.addView(t3v);
////                        TextView t4v = new TextView(UserRentActivity.this);
////                        t4v.setText(location);
////                        //                t4v.setTextColor(Color.WHITE);
////                        //                t4v.setGravity(Gravity.CENTER);
////                        tbrow.addView(t4v);
//                        TextView t5v = new TextView(UserRentActivity.this);
//                        t5v.setText(numbikes);
//                        t5v.setTextSize(18);
//                        //                t4v.setTextColor(Color.WHITE);
//                        //                t4v.setGravity(Gravity.CENTER);
//                        tbrow.addView(t5v);
//                        tbrow.setTag(standId);
////                        tbrow.setMinimumHeight(30);
//                        tbrow.setPadding(0,10,0,10);
////                        tbrow.serFocusable
////                        tbrow.setBackgroundDrawable(R.drawable.list_selector_background);
////                        tbrow.setFo
//                        tbrow.setFocusable(true);
//                        tbrow.setFocusableInTouchMode(true);
////                        ShapeDrawable border = new ShapeDrawable(new RectShape());
////                        border.getPaint().setStyle(Paint.Style.STROKE);
////                        border.getPaint().setColor(Color.BLACK);
////                        tbrow.setBackgroundResource(R.drawable.border);
////                        tbrow.set
////                        TableRow.LayoutParams lp = new TableRow.LayoutParams(
////                                TableRow.LayoutParams.MATCH_PARENT, 27);
////                        tbrow.setLayoutParams(lp);
//                        tbrow.setOnClickListener ( new OnClickListener() {
//                            @Override
//                            public void onClick( View v ) {
//                                //Do Stuff
//                                Intent screenChange=null;
//                                screenChange = new Intent(UserRentActivity.this,StandRentActivity.class);
//                                screenChange.setAction(Intent.ACTION_SEND);
//                                screenChange.putExtra(UserRentActivity.EXTRA_MESSAGE, (String)tbrow.getTag());
//                                screenChange.putExtra("userId",userId);
//                                screenChange.setType("text/plain");
//                                startActivity(screenChange);
//                            }
//                        } );
//                        table_layout.addView(tbrow);
                    }

//                    AndroidListAdapter androidListAdapter = new AndroidListAdapter(UserRentActivity.this, image_id, address, numbikes, standId);
//                    ListView androidListView = (ListView) findViewById(R.id.custom_listview_example);
//                    androidListView.setAdapter(androidListAdapter);

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("LoginActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }
}
