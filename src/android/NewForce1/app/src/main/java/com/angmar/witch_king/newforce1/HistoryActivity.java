package com.angmar.witch_king.newforce1;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.angmar.witch_king.newforce1.R.id.table_layout;

public class HistoryActivity extends AppCompatActivity {

    public String myurl;
    private HistoryTask historyTask;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        myurl = LoginActivity.myurl;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        historyTask = new HistoryTask();
        historyTask.execute();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("History Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class HistoryTask extends AsyncTask<String, Void, JSONObject> {
        HistoryTask() {
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl + "History";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                boolean first = true;

                Log.e("HTTP Response", conn.getResponseMessage());
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
                    Log.e("History ", sb.toString());
                    JSONObject myob = new JSONObject(sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("HistoryActivity", "Wrong");
                        Log.e("Server Returned", myob.getString("message"));
                        return myob;
                    } else {
                        Log.e("HistoryActivity", "Present");
                        return myob;
                    }
                } else {
                    Log.e("HistoryActivity", "Error");
                    JSONObject err = new JSONObject();
                    err.put("status", "false");
                    err.put("message", "Response Code:" + responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("HistoryActivity", "Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("HistoryActivity", "Can't Connect to the network");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject myob) {
            try {
                Log.e("UserRentTask", myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    Log.e("Data ", myob.toString());
                    String [] model = new String[array.length()];
                    String[] color = new String[array.length()];
                    String[] fromTime = new String[array.length()];
                    String[] toTime = new String[array.length()];
                    String[] fromStand = new String[array.length()];
                    String[] toStand = new String[array.length()];

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        color[i] = row.getString("color");
                        model[i] = row.getString("model");
                        if (row.has("tostand") && row.has("totime")){
                            toStand[i] = row.getString("tostand");
                            toTime[i] = row.getString("totime");
                        }
                        else{
                            toStand[i] ="null";
                            toTime[i] ="null";
                        }
                        fromStand[i] = row.getString("fromstand");

                        fromTime[i]= row.getString("fromtime");

                    }
                    AndroidListAdapterHistory androidListAdapter = new AndroidListAdapterHistory(HistoryActivity.this, model, color, fromStand, fromTime, toStand, toTime);
                    ListView androidListView = (ListView) findViewById(R.id.history);
                    androidListView.setAdapter(androidListAdapter);

                } else {
                    // do something
                    Log.e("HistoryActivErrorMsg", myob.getString("message"));
                }
            } catch (Exception e) {
                Log.e("HistoryActivity", "Exception" + e.getLocalizedMessage());
            }

        }
    }
}
