package com.angmar.witch_king.newforce1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

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

public class CameraActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public String myurl;
    private ImageView imageView;
    private String type = new String("2 wheeler");
    private FloatingActionButton fab;
    private AddRidesTask mRideTask = null;
    private AutoCompleteTextView mModelView;
    private AutoCompleteTextView mColorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // my_child_toolbar is defined in the layout file
        setTitle("Add New Bike");
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myurl = LoginActivity.myurl;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        imageView = (ImageView) findViewById(R.id.image_view);
        mModelView = (AutoCompleteTextView) findViewById(R.id.bikeModel);
        mColorView = (AutoCompleteTextView) findViewById(R.id.bikeColor);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 100);
                }
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rides_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRideTask = new AddRidesTask(mModelView.getText().toString(),mColorView.getText().toString(),spinner.getSelectedItem().toString());
                mRideTask.execute();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String item = parent.getItemAtPosition(pos).toString();
        type = new String(item);

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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fab.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }

    public class AddRidesTask extends AsyncTask<String, Void, JSONObject> {

        private final String mModel;
        private final String mColor;
        private final String mType;

        AddRidesTask(String username, String password, String type) {
            mModel = username;
            mColor = password;
            mType = type;

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "AddBike";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("model", mModel);
                loginData.put("color",mColor);
                loginData.put("type",mType);
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
                        Log.e("CameraActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        Log.e("Data",myob.getString("data"));
                        return myob;
                    }
                    else {
                        Log.e("CameraActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("CameraActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("CameraActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("CameraActivity","Can't Connect to the network");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject myob) {
            mRideTask = null;
            try {
                Log.e("AddRideTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    Intent screenChange = new Intent();
                    Helper.makeToast(getApplicationContext(),"Ride Successfully Added");
                    setResult(Activity.RESULT_OK,screenChange);
                } else {
                    // do something
                    Log.e("Error Message",myob.getString("message"));
                    Intent screenChange = new Intent();
                    Helper.makeToast(getApplicationContext(),"Ride Addition Failed");
                    setResult(Activity.RESULT_CANCELED,screenChange);
                }
                finish();

            } catch (Exception e)
            {
                Log.e("CameraActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }

}
