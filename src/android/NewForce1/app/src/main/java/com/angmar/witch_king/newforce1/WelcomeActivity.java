package com.angmar.witch_king.newforce1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Handler;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        CookieManager manager = new CookieManager(new PersistentCookieStore(this.getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

        UserWelcomeTask mWelcomeTask = new UserWelcomeTask();
        mWelcomeTask.execute();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // finish the splash activity so it can't be returned to
//                WelcomeActivity.this.finish();
//                // create an Intent that will start the second activity
//                Intent mainIntent = new Intent(WelcomeActivity.this, SecondActivity.class);
//                WelcomeActivity.this.startActivity(mainIntent);
//            }
//        }, 3000); // 3000 milliseconds
//    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserWelcomeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = LoginActivity.myurl+ "Welcome";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
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
                    return myob.toString();
                }
                else{
                    Log.e("WelcomeActivity","Error");
                    return new String("false");
                }
            } catch (Exception e) {
                Log.e("WelcomeActivity","Exception" + e.getLocalizedMessage());
                return new String("false");
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("LoginActivity","Can't Connect to the network");
                    return new String("false");
                }
            }
        }

        @Override
        protected void onPostExecute(String success) {
            boolean goodtogo = false;

            String email = null;
            String name = null;

            Intent screenChange = null;
            Log.e("UserWelcomeTask", success);
            try {
                if (!success.equals("false")) {
                    JSONObject ob = new JSONObject(success);
                    JSONObject dat = ob.getJSONArray("data").getJSONObject(0);
                    email = dat.getString("riderid");
                    name = dat.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("UserWelcomeTask", "Exception Occured");
            }

            if (name != null && email != null) {
                // GOTO HOME
                screenChange = new Intent(WelcomeActivity.this, HomeActivity.class);
                SharedPreferences sharedPref = getSharedPreferences(
                        "com.angmar.witch_king.newforce1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(("name"), name);
                editor.putString(("email"), email);
                editor.commit();
            } else {
                // GOTO LOGIN

                screenChange = new Intent(WelcomeActivity.this, MainActivity.class);
                screenChange.setFlags(screenChange.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
            WelcomeActivity.this.finish();
            startActivity(screenChange);
        }
    }
}
