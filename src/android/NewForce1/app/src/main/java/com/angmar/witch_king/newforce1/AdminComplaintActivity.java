package com.angmar.witch_king.newforce1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
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
import java.util.Iterator;

public class AdminComplaintActivity extends AppCompatActivity {

    ConnectTask mAuthTask = null;
    TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaint);
        table_layout = (TableLayout) findViewById(R.id.table_layout);
        Log.v("TAG", "INside AdminComplaintActivity.onCreate");
    }

    public void makeHeader(String rtype) {
//        TODO Use Rtype to create Header
        Log.v("TAG", "INside AdminComplaintActivity.makeHeader");
        TableRow row = new TableRow(AdminComplaintActivity.this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        TextView heading = new TextView(AdminComplaintActivity.this);
        heading.setLayoutParams(lp);
        heading.setText("Bike Id");
        heading.setPadding(2,2,2,2);
        row.addView(heading);

        TextView heading1 = new TextView(AdminComplaintActivity.this);
        heading1.setLayoutParams(lp);
        heading1.setText("Request");
        heading1.setPadding(2,2,2,2);
        row.addView(heading1);

        TextView heading2 = new TextView(AdminComplaintActivity.this);
        heading2.setLayoutParams(lp);
        heading2.setText("Details");
        heading2.setPadding(2,2,2,2);
        row.addView(heading2);

        TextView heading5 = new TextView(AdminComplaintActivity.this);
        heading5.setLayoutParams(lp);
        heading5.setText("Settle");
        heading5.setPadding(2,2,2,2);
        row.addView(heading5);
        table_layout.addView(row);
    }

    public void buildRow(String[] values) {
        Log.v("TAG", "INside AdminComplaintActivity.buildRow");
        TableRow row = new TableRow(AdminComplaintActivity.this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < values.length; i++) {
            TextView tv = new TextView(AdminComplaintActivity.this);
            tv.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
//            tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setPadding(1, 1, 1, 1);
            tv.setText(values[i]);
            row.addView(tv);
        }

        RadioButton delButton = new RadioButton(AdminComplaintActivity.this);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder popup = new AlertDialog.Builder(AdminComplaintActivity.this);
                popup.setMessage("Do you want to Delete?");
                popup.setCancelable(true);
                popup.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                TableRow row = (TableRow) v.getParent();
                                String reqId = ((TextView)row.getChildAt(1))
                                        .getText().toString();
                                table_layout.removeView((View) v.getParent());
                                //settleRequest task = new settleRequest(rtype,reqId);
                                settleRequest task = new settleRequest("data",reqId);
                                task.execute();
                            }
                        }
                );
                popup.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
                AlertDialog success = popup.create();
                popup.show();
            }
        });
        row.addView(delButton);
        table_layout.addView(row);
    }



    public class ConnectTask extends AsyncTask<String, Void, String> {

        private final String rtype;
        private JSONObject jsonObj;
        public JSONArray jsonarray;

        ConnectTask(String reqType) {
            rtype = reqType;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("TAG", "INside AdminComplaintActivity.ConnectTask.doInBackground");
            HttpURLConnection urlConnection = null;
            try {
                URL url;
//                TODO url
                url  = new URL(LoginActivity.myurl+"register");

                JSONObject coursedetails = new JSONObject();
                coursedetails.put("id", rtype);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(10000);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(coursedetails));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    StringBuilder str = new StringBuilder("");
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    int data = isr.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isr.read();
                        str.append(current);
                    }
                    in.close();

                    String s = str.toString();
                    jsonObj = new JSONObject(s);

                    if (jsonObj.optString("Present").startsWith("No")){
                        System.out.println("False Session");
                        Intent myintent = new Intent(AdminComplaintActivity.this, LoginActivity.class);
                        startActivity(myintent);
                    }
                    else{
                        if (jsonObj.optString("message").startsWith("No")){
                            Intent myintent = new Intent(AdminComplaintActivity.this, LoginActivity.class);
                            startActivity(myintent);
                        }
                        else{
                            jsonarray = new JSONArray(jsonObj.optString("data"));
                            return new String("true");
                        }
                    }
                    return new String("false");
                }
                else{
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e){
                e.printStackTrace();
                return new String("false");
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Log.v("TAG", "INside AdminComplaintActivity.ConnectTask.onPostExecute");
            mAuthTask = null;
            Log.v("TAG", "Array building starts");
            if(result.startsWith("true"))
            {
                Log.v("TAG", "Display the array");
                try {
                    makeHeader(rtype);
                    for (int i = 0; i < jsonarray.length(); i++) {
//                      TODO  JSON Object Assummed
                        JSONObject jsobj = jsonarray.getJSONObject(i);
                        String[] dataStrings = new String[3];
                        dataStrings[0] = jsobj.getString("bikeId");
                        dataStrings[1] = jsobj.getString("reqId");
                        dataStrings[2] = jsobj.getString("reqDetails");
                        buildRow(dataStrings);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        public String getPostDataString(JSONObject params) throws Exception {
            Log.v("TAG", "INside AdminComplaintActivity.ConnectTask.onCancelled");
            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator<String> itr = params.keys();
            while(itr.hasNext()){
                String key= itr.next();
                Object value = params.get(key);
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
            return result.toString();
        }
    }





    public class settleRequest extends AsyncTask<String, Void, String> {
        String rtype;
        String requestId;
        JSONArray registered;

        settleRequest(String type,String reqId) {
            rtype = type;
            requestId = reqId;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("TAG", "INside AdminComplaintActivity.SettleRequest.doInBackground");
            HttpURLConnection conn = null;
//            TODO Servlet Name
            String urlLogin = LoginActivity.myurl+"AddDelete";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject adjustData =new JSONObject();
                adjustData.put("id", requestId);
                adjustData.put("data", rtype);
                Log.e("AdjustTask: Back",adjustData.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(100);
                conn.setReadTimeout(100);

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                StringBuilder output = new StringBuilder();
                boolean first = true;

                Iterator<String> itr = adjustData.keys();

                while(itr.hasNext()){
                    String key= itr.next();
                    Object value = adjustData.get(key);

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
                    StringBuilder stringBuilder = new StringBuilder("");
                    InputStream in = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    int info = isr.read();
                    while (info != -1) {
                        char current = (char) info;
                        info = isr.read();
                        stringBuilder.append(current);
                    }
                    in.close();

                    JSONObject myob = new JSONObject(stringBuilder.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("LoginActivity","Wrong");
                        return new String("false");
                    }
                    else {
                        Log.e("LoginActivity","Present");
                        registered = new JSONArray(myob.optString("data"));
                        return new String("true");
                    }
                }
                else{
                    Log.e("LoginActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("LoginActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("LoginActivity","Can't Connect to the network");
                    return new String("Can't Connect");
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("TAG", "INside AdminComplaintActivity.SettleRequest.doInBackground");
            mAuthTask.jsonarray = registered;
            mAuthTask.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}
