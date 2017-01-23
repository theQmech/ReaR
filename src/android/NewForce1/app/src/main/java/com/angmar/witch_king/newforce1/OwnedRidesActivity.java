package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Iterator;

public class OwnedRidesActivity extends Fragment {
    public String myurl;
    private OwnedRidesTask mRideTask = null;
    FloatingActionButton fabut;
    private View rootView;
    private UpdateStatusTask mUpdateTask = null;


    public static String s0 = "Safely Home";
    public static String s1 = "Lend Requested";
    public static String s2 = "Lent";
    public static String s3 = "Unlend Requested";
    public static String b0 = "Lend";
    public static String b1 = "Cancel Request";
    public static String b2 = "Unlend";
    public static String b3 = "Cancel Request";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.activity_login);
        rootView = inflater.inflate(R.layout.activity_owned_rides, container, false);
        myurl = LoginActivity.myurl;
        fabut = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO Rent Popup
                Intent screenChange = new Intent(getActivity(), CameraActivity.class);
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.setType("text/plain");
                startActivity(screenChange);
            }
        });
        mRideTask = new OwnedRidesTask();
        mRideTask.execute();
        return rootView;
    }

    public class UpdateStatusTask extends AsyncTask<Void, Void, JSONObject> {

        private final String Op;
        private final String OpType;
        private final String rideId;
        private final Button btn;
        UpdateStatusTask(String Op, String OpType, String rideId, View v) {
            this.Op = Op;
            this.OpType = OpType;
            this.rideId = rideId;
            this.btn = (Button)v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btn.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "LendUnlend";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("rideid", rideId);
                loginData.put("op",Op);
                loginData.put("optype",OpType);
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
                        Log.e("OwnedRidesActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("OwnedRidesActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("OwnedRidesActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("OwnedRidesActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("OwnedRidesActivity","Can't Connect to the network");
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
                Log.e("OwnedRideTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    Integer status = myob.getInt("data");
                    TextView v = ((TextView) ((LinearLayout)btn.getParent()).findViewById(R.id.status_view));
                    String toast_val = null;
                    switch (status){

                        case 0 : v.setText(OwnedRidesActivity.s0);
                            btn.setText(OwnedRidesActivity.b0);
                            toast_val = "Request Cancelled Successfully";
                            break;
                        case 1 : v.setText(OwnedRidesActivity.s1);
                            btn.setText(OwnedRidesActivity.b1);
                            toast_val = "Lend Requested Successfully";
                            break;
                        case 2 : v.setText(OwnedRidesActivity.s2);
                            btn.setText(OwnedRidesActivity.b2);
                            toast_val = "Request Cancelled Successfully";
                            break;
                        case 3 : v.setText(OwnedRidesActivity.s3);
                            btn.setText(OwnedRidesActivity.b3);
                            toast_val = "Unlend Requested Successfully";
                            break;
                    }

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("OwnedRidesActivity","Exception" + e.getLocalizedMessage());
            }
            btn.setEnabled(true);

        }
    }


    public class OwnedRidesTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "PersonalRides";
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
                        Log.e("OwnedRidesActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("OwnedRidesActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("OwnedRidesActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("OwnedRidesActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("OwnedRidesActivity","Can't Connect to the network");
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
                Log.e("OwnedRideTask",myob.getString("status"));
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
                        rideStatus[i] = row.getString("status");
                        rideId[i] = row.getString("rideid");

                        image_id[i] = R.drawable.ic_directions_bike_black_24dp;
                    }

                    OwnedRidesActivity.AndroidListAdapter androidListAdapter = new OwnedRidesActivity.AndroidListAdapter(getActivity(), image_id, model, color, rideStatus, rideId);
                    ListView androidListView = (ListView) rootView.findViewById(R.id.owned_rides);
                    androidListView.setAdapter(androidListAdapter);

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("OwnedRidesActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }

    public class AndroidListAdapter extends ArrayAdapter {
        String[] model;
        String[] color;
        String[] rideStatus;
        Integer[] imagesId;
        Context context;
        String[] rideId;

        public AndroidListAdapter(Activity context, Integer[] imagesId, String[] model, String[] color, String[] rideStatus, String[] rideId) {
            super(context, R.layout.custom_list_item_layout, model);
            this.model = model;
            this.color = color;
            this.rideStatus = rideStatus;
            this.imagesId = imagesId;
            this.context = context;
            this.rideId = rideId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewRow = layoutInflater.inflate(R.layout.owned_rides_custom_list_layout, null,
                    true);
            TextView mModelView = (TextView) viewRow.findViewById(R.id.model_view);
            ImageView mimageView = (ImageView) viewRow.findViewById(R.id.image_view);
            mModelView.setText(model[i]);
            mimageView.setImageResource(imagesId[i]);
            TextView mColorView = (TextView) viewRow.findViewById(R.id.color_view);
            mColorView.setText(color[i]);
            TextView mStatusView = (TextView) viewRow.findViewById(R.id.status_view);
            Button mRideStatus = (Button) viewRow.findViewById(R.id.ride_status);
            mRideStatus.setTag(rideId[i]);
            switch (Integer.parseInt(rideStatus[i])){

                case 0 : mStatusView.setText(s0);
                    mRideStatus.setText(b0);
                    break;
                case 1 : mStatusView.setText(s1);
                    mRideStatus.setText(b1);
                    break;
                case 2 : mStatusView.setText(s2);
                    mRideStatus.setText(b2);
                    break;
                case 3 : mStatusView.setText(s3);
                    mRideStatus.setText(b3);
                    break;
            }
            mRideStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String status = ((TextView) ((LinearLayout)v.getParent()).findViewById(R.id.status_view)).getText().toString();
                    Log.e("Status",status);
                    Button b = (Button) v;
                    Integer temp = 0;
                    String Op = null;
                    String OpType = null;
                    if(status.equals(com.angmar.witch_king.newforce1.OwnedRidesActivity.s0)) {
                        Op = "Lend";
                        OpType = "Place";
                    }
                    else if (status.equals(com.angmar.witch_king.newforce1.OwnedRidesActivity.s1)) {
                        Op = "Lend";
                        OpType = "Cancel";
                    }
                    else if (status.equals(com.angmar.witch_king.newforce1.OwnedRidesActivity.s2)) {
                        Op = "Unlend";
                        OpType = "Place";
                    }
                    else if (status.equals(com.angmar.witch_king.newforce1.OwnedRidesActivity.s3)) {
                        Op = "Unlend";
                        OpType = "Cancel";
                    }
                    OwnedRidesActivity.UpdateStatusTask mUpdateTask = new OwnedRidesActivity.UpdateStatusTask(Op, OpType, (String)v.getTag(),v);
                    mUpdateTask.execute();
                }
            });
//        mStatusView.setText(rideStatus[i]);
            return viewRow;
        }
    }
}
