package com.angmar.witch_king.newforce1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.angmar.witch_king.newforce1.LoginActivity.myurl;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ConnectTask mAuthTask;
    // TODO: Rename and change types of parameters
    private String rtype;
    private String mParam2;
    TableLayout table_layout;

    private OnFragmentInteractionListener mListener;

    public AdminTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminTabFragment newInstance(String param1, String param2) {
        AdminTabFragment fragment = new AdminTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rtype = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_admin_tab, container, false);
        table_layout = (TableLayout) view.findViewById(R.id.table_layout);
        mAuthTask = new ConnectTask(rtype);
        mAuthTask.execute();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void makeHeader(String rtype) {
//        TODO Use Rtype to create Header
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        TextView heading = new TextView(getActivity());
        heading.setLayoutParams(lp);
        heading.setText("Bike Id");
        heading.setPadding(2,2,2,2);
        row.addView(heading);

        TextView heading1 = new TextView(getActivity());
        heading1.setLayoutParams(lp);
        heading1.setText("Request");
        heading1.setPadding(2,2,2,2);
        row.addView(heading1);

        TextView heading2 = new TextView(getActivity());
        heading2.setLayoutParams(lp);
        heading2.setText("Details");
        heading2.setPadding(2,2,2,2);
        row.addView(heading2);

        TextView heading5 = new TextView(getActivity());
        heading5.setLayoutParams(lp);
        heading5.setText("Settle");
        heading5.setPadding(2,2,2,2);
        row.addView(heading5);
        table_layout.addView(row);
    }

    public void buildRow(String[] values) {

        TableRow row = new TableRow(getActivity());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < values.length; i++) {
            TextView tv = new TextView(getActivity());
            tv.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
//            tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setPadding(1, 1, 1, 1);
            tv.setText(values[i]);
            row.addView(tv);
        }

        RadioButton delButton = new RadioButton(getActivity());
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
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
                                settleRequest task = new settleRequest(rtype,reqId);
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

            HttpURLConnection urlConnection = null;
            try {
                URL url;
//                TODO url
                url  = new URL(myurl+"register");

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
                        Intent myintent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(myintent);
                    }
                    else{
                        if (jsonObj.optString("message").startsWith("No")){
                            Intent myintent = new Intent(getActivity(), LoginActivity.class);
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
            HttpURLConnection conn = null;
//            TODO Servlet Name
            String urlLogin = myurl+"AddDelete";
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
            mAuthTask.jsonarray = registered;
            mAuthTask.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
