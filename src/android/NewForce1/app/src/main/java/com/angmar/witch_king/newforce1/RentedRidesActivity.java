package com.angmar.witch_king.newforce1;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RentedRidesActivity extends Fragment {

    public String myurl;
    private RentedRidesTask mRideTask = null;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.activity_login);
        rootView = inflater.inflate(R.layout.activity_rented_rides, container, false);

        myurl = LoginActivity.myurl;

        mRideTask = new RentedRidesTask();
        mRideTask.execute();
        return rootView;
    }

    public class RentedRidesTask extends AsyncTask<Void, Void, JSONObject> {

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
                        Log.e("MyRidesActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("MyRidesActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("MyRidesActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("MyRidesActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("MyRidesActivity","Can't Connect to the network");
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
                Log.e("MyRideTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    String [] model = new String[array.length()];
                    String [] code = new String[array.length()];
                    String [] color = new String[array.length()];
                    String [] rideId = new String[array.length()];
                    Integer image_id[] = new Integer[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        // name = row.getString("name");
                        model[i] = row.getString("model");
                        color[i] = row.getString("color");
                        rideId[i] = row.getString("rideid");
                        code[i] = row.getString("code");
                        image_id[i] = R.drawable.ic_directions_bike_black_24dp;
                    }

                    AndroidListAdapter_standRides androidListAdapter = new AndroidListAdapter_standRides(getActivity(), image_id, model, color, rideId, code);
                    ListView androidListView = (ListView) rootView.findViewById(R.id.rented_rides);
                    androidListView.setAdapter(androidListAdapter);

                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("MyRidesActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }
}
