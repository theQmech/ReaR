package com.angmar.witch_king.newforce1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener{

    SupportMapFragment sMapFragment;
    FragmentManager fm;
    GoogleMap mMap;
    public String myurl;
    android.support.v4.app.FragmentManager sFm;
    boolean mPermissionDenied = false;
    final short LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker mSelectedMarker;
    private List<Stand> standList;
    private List<Marker> standMarker;
    FloatingActionButton fabut;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private UserRentTask mStandTask = null;
    AutoCompleteTextView tv ;
    public Stand standSelected = null;
    public String bikeId;
    public String rideType;
    public String rideModel;
    public String rideColor;
    public String rideStatus;
    public String rideURL;
    NavigationView navigationView;
    private RentRideTask mAuthTask;
    private UserLogoutTask mLogoutTask = null;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sMapFragment = SupportMapFragment.newInstance();
        fm = getFragmentManager();
        sFm = getSupportFragmentManager();
        setContentView(R.layout.activity_home);
        standList = new ArrayList<>();
        standMarker = new ArrayList<>();
        ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextView)).clearFocus();

        fabut = (FloatingActionButton) findViewById(R.id.fab);
        fabut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Rent A Bike");
                final EditText input = new EditText(HomeActivity.this);
                builder.setMessage("Enter Bike ID");
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bikeId = input.getText().toString();
                        mAuthTask = new RentRideTask(bikeId);
                        mAuthTask.execute();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setHideable(true);
        count = 0;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.bottom_sheet);
        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.relative_layout);
        rlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO Rent Popup
                Intent screenChange = new Intent(HomeActivity.this, StandRentActivity.class);
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.putExtra("standId", standSelected.standId);
                screenChange.putExtra("standName", standSelected.name);
                screenChange.setType("text/plain");
                startActivity(screenChange);
            }
        });
//        rlayout.setOnTouchListener(new setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                TODO Rent Popup
//                Intent screenChange = new Intent(HomeActivity.this, StandRentActivity.class);
//                screenChange.setAction(Intent.ACTION_SEND);
//                screenChange.putExtra("standId", standSelected.standId);
//                screenChange.putExtra("standName", standSelected.name);
//                screenChange.setType("text/plain");
//                startActivity(screenChange);
//            }
//        }));
//        TODO add stand from servlet

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportFragmentManager().beginTransaction().replace
                (R.id.content_frame,sMapFragment).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        sFm.beginTransaction().add(R.id.content_frame,sMapFragment);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = getSharedPreferences(
                "com.angmar.witch_king.newforce1", Context.MODE_PRIVATE);

        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tvName = (TextView)hView.findViewById(R.id.name);
        TextView tvEmail = (TextView)hView.findViewById(R.id.email);

        tvName.setText(sharedPref.getString("name", "Android Studio"));
        tvEmail.setText(sharedPref.getString("email", "android.studio@android.com"));

        FragmentManager fm = getFragmentManager();
//        fm.beginTransaction().replace(R.id.content_frame, (Fragment) sMapFragment).commit();
        myurl = LoginActivity.myurl;
        mStandTask = new UserRentTask();
        mStandTask.execute();
//        sMapFragment.getMapAsync(this);

        ((CoordinatorLayout)findViewById(R.id.my_coordinator_layout)).requestFocus();
    }
    public class RentRideTask extends AsyncTask<String, Void, String> {

        private final String rideID;

        RentRideTask(String ride) {
            rideID = ride;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "Ride?rideid="+rideID;
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("rideid", rideID);
                Log.e("Rent Bike Details",loginData.toString());
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
                    Log.e("Server Response",sb.toString());
                    if (myob.getString("status").contains("false")) {
                        Log.e("RentBikeActivity","Wrong");
                        return new String("false:"+ myob.getString("message"));
                    }
                    else {
                        Log.e("RentBikeActivity","Present");
                        JSONObject array = myob.getJSONObject("data");
                        rideType=array.getString("type");
                        rideColor=array.getString("color");
                        rideModel=array.getString("model");
                        rideStatus=array.getString("status");
                        rideURL = array.getString("url");
                        return new String("true:"+ myob.getString("message"));
                    }
                }
                else{
                    Log.e("RentBikeActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("RentBikeActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("RentBikeActivity","Can't Connect to the network");
                    return new String("Can't Connect");
                }
            }
//            return new String("false");
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            if(success.contains("true")){
//                model.setText(rideModel);
//                color.setText(rideColor);
//                type.setText(rideType);
//                int loader = R.mipmap.ic_launcher;
//                ImageLoader imgLoader = new ImageLoader(getApplicationContext());
//                imgLoader.DisplayImage(rideURL, loader, imageView);
                Intent screenChange=null;
                screenChange = new Intent(HomeActivity.this,RentBikeActivity.class);
                screenChange.putExtra("rideModel",rideModel);
                screenChange.putExtra("rideColor",rideColor);
                screenChange.putExtra("rideType",rideType);
                screenChange.putExtra("rideURL",rideURL);
                screenChange.putExtra("bikeId",bikeId);
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.setType("text/plain");
                startActivity(screenChange);

            } else {
//                TODO
//                Intent screenChange=null;
//                screenChange = new Intent(RentBikeActivity.this,HomeActivity.class);
                Toast.makeText(getApplicationContext(), success.substring(6), Toast.LENGTH_SHORT).show();
//                screenChange.setAction(Intent.ACTION_SEND);
//                screenChange.setType("text/plain");
//                startActivity(screenChange);

            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public void changeScreen(View view) {
        Intent screenChange = new Intent(HomeActivity.this, StandRentActivity.class);
        screenChange.setAction(Intent.ACTION_SEND);
        screenChange.putExtra("standId", standSelected.standId);
        screenChange.putExtra("standName", standSelected.name);
        screenChange.setType("text/plain");
        startActivity(screenChange);
    }
    private void updateBottomSheetContent(Marker marker) {
        int i;
        TextView name = (TextView) bottomSheet.findViewById(R.id.detail_name);
        TextView numCars = (TextView) bottomSheet.findViewById(R.id.numCars);
        TextView numBikes = (TextView) bottomSheet.findViewById(R.id.numBikes);
        for (i = 0; i < standList.size();i++){
            if(marker.getTitle().equals(standList.get(i).name)){
                break;
            }
        }
        standSelected = standList.get(i);
        numCars.setText("Cars: " + standList.get(i).numCars);
        numBikes.setText("Bikes: " + standList.get(i).numBikes);
        name.setText(marker.getTitle());
        Button button = (Button) bottomSheet.findViewById(R.id.unrent_button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              TODO Intent to Unrent
                Intent screenChange = new Intent(HomeActivity.this, UserUnrentActivity.class);
                screenChange.putExtra("standId", standSelected.standId);
                screenChange.setAction(Intent.ACTION_SEND);
                screenChange.setType("text/plain");
                startActivity(screenChange);
            }
        };
        // assign click listener to the OK button (btnOK)
        button.setOnClickListener(listener);
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if(count == 0){
            fabut.animate().translationYBy(-bottomSheet.getHeight());
            count++;
        }
    }

    private boolean addMarkers(List<Stand> standList) {
        standMarker = new ArrayList<>();
        Log.e(String.valueOf(standList.size()),"WTF");
        for (int i = 0; i < standList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(standList.get(i).name);
            Log.e("Stand " + i,standList.get(i).name);
            markerOptions.position(new LatLng(standList.get(i).latitude,standList.get(i).longitude));
            Marker marker = mMap.addMarker(markerOptions);
            standMarker.add(marker);
        }
        Log.e("StandMarker Size",String.valueOf(standMarker.size()));
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fm = getFragmentManager();
        sFm = getSupportFragmentManager();

        int id = item.getItemId();

        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).commit();

        if (id == R.id.my_rides) {
//            To ensure back Works
            Menu drawer_menu = navigationView.getMenu();
            MenuItem menuItem = drawer_menu.findItem(R.id.nav_home);
            if(!menuItem.isChecked())
            {
                menuItem.setChecked(true);
            }
            sFm.beginTransaction().show(sMapFragment).addToBackStack("tag").commit();
            Intent screenChange = new Intent(HomeActivity.this, MyRidesActivity.class);
            screenChange.setAction(Intent.ACTION_SEND);
            screenChange.setType("text/plain");
            startActivity(screenChange);

        } else if (id == R.id.nav_home) {

            if (!sMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.content_frame, sMapFragment).commit();
            else
                sFm.beginTransaction().show(sMapFragment).commit();

        } else if (id == R.id.nav_history) {
            //            TODO replace fragment
            Menu drawer_menu = navigationView.getMenu();
            MenuItem menuItem = drawer_menu.findItem(R.id.nav_home);
            if(!menuItem.isChecked())
            {
                menuItem.setChecked(true);
            }
            sFm.beginTransaction().show(sMapFragment).addToBackStack("tag").commit();
            Intent screenChange = new Intent(HomeActivity.this, HistoryActivity.class);
            screenChange.setAction(Intent.ACTION_SEND);
            screenChange.setType("text/plain");
            startActivity(screenChange);
        }
        else if (id == R.id.log_out) {
            mLogoutTask = new UserLogoutTask();
            mLogoutTask.execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class UserLogoutTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "LogoutServlet";
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
                    Log.e("Server Response",sb.toString());

                    return myob.toString();
                }
                else{
                    Log.e("RentBikeActivity","Error");
                    return new String("false: "+ responseCode);
                }
            } catch (Exception e) {
                Log.e("RentBikeActivity","Exception" + e.getLocalizedMessage());
                return new String("false: Exception Occured " + e.getLocalizedMessage());
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("RentBikeActivity","Can't Connect to the network");
                    return new String("Can't Connect");
                }
            }
//            return new String("false");
        }

        @Override
        protected void onPostExecute(String success) {
            Intent screenChange = null;
            String status = null;
            String error = null;
            Log.e("UserWelcomeTask", success);
            try {
                if (!success.equals("false")) {
                    JSONObject ob = new JSONObject(success);
                    status = ob.getString("status");
                    error = ob.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.toString();
                Log.e("UserWelcomeTask", "Exception Occured");
            }
            if (status != null && status.equals("true")) {
                // GOTO HOME

                screenChange = new Intent(HomeActivity.this, MainActivity.class);
                SharedPreferences sharedPref = getSharedPreferences(
                        "com.angmar.witch_king.newforce1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();
            } else {
                // GOTO LOGIN
                Helper.makeToast(getApplicationContext(),error);
            }
            screenChange.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            screenChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(screenChange);
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if(count > 0){
            fabut.animate().translationYBy(bottomSheet.getHeight()*count);
            count = 0;
        }
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        enableMyLocation();
        addMarkers(standList);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setMapToolbarEnabled(false);
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null){
            double lat =  location.getLatitude();
            double lng = location.getLongitude();
            LatLng coordinate = new LatLng(lat, lng);

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(coordinate);
            mMap.moveCamera(center);
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (count > 0){
            fabut.animate().translationYBy(bottomSheet.getHeight()*count);
            count = 0;
        }
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

//        fabut.setVisibility(View.GONE);
        updateBottomSheetContent(marker);
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        fabut.setVisibility(View.GONE);
        updateBottomSheetContent(marker);
    }

    public class UserRentTask extends AsyncTask<String, Void, JSONObject> {
        UserRentTask() {
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "Stands";
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
                        Log.e("HomeActivity","Wrong");
                        Log.e("Server Returned",myob.getString("message"));
                        return myob;
                    }
                    else {
                        Log.e("HomeActivity","Present");
                        return myob;
                    }
                }
                else{
                    Log.e("HomeActivity","Error");
                    JSONObject err =new JSONObject();
                    err.put("status","false");
                    err.put("message","Response Code:"+responseCode);
                    return err;
                }
            } catch (Exception e) {
                Log.e("HomeActivity","Exception" + e.getLocalizedMessage());
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("HomeActivity","Can't Connect to the network");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject myob) {
            mStandTask = null;

            try {
                Log.e("UserRentTask",myob.getString("status"));
                if (myob.getString("status").contains("true")) {
                    //build rows
                    JSONArray array = myob.getJSONArray("data");
                    Log.e("Data ",myob.toString());
                    Log.e("NumStand",String.valueOf(array.length()));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        Stand stand = new Stand();
                        stand.name = row.getString("name");
                        stand.address = row.getString("address");
                        stand.numBikes = row.getInt("numbikes");
                        stand.latitude = row.getDouble("lat");
                        stand.longitude = row.getDouble("long");
//                        stand.longitude = 0.4;
//                        stand.latitude = 22.0;
                        stand.standId = row.getString("standid");
                        standList.add(stand);
                    }

                    sMapFragment.getMapAsync(HomeActivity.this);
                    tv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                    ArrayAdapter<Stand> adapter = new ArrayAdapter<>(
                            HomeActivity.this, android.R.layout.simple_dropdown_item_1line, standList);
                    tv.setAdapter(adapter);
                    tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                long arg3) {
                            InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            Stand selected = (Stand) arg0.getAdapter().getItem(arg2);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(selected.latitude, selected.longitude)));
                            inputMgr.hideSoftInputFromWindow(tv.getWindowToken(), 0);
                            int i;
                            for(i = 0 ; i < standMarker.size();i++){
                                if (standMarker.get(i).getTitle().equals(selected.name)){
                                    break;
                                }
                            }
                            if (i < standMarker.size()){
                                standMarker.get(i).showInfoWindow();
//                                fabut.setVisibility(View.GONE);
                                fabut.animate().translationYBy(-bottomSheet.getHeight());
                                updateBottomSheetContent(standMarker.get(i));
                            }

                        }
                    });
                    tv.setThreshold(2);
                    ((CoordinatorLayout)findViewById(R.id.my_coordinator_layout)).requestFocus();

                } else {
                    // do something
                    Log.e("HomeActivity ErrorMsg",myob.getString("message"));
                }
            } catch (Exception e)
            {
                Log.e("HomeActivity","Exception" + e.getLocalizedMessage());
            }

        }
    }
}
