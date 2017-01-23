package com.angmar.witch_king.newforce1;

/**
 * Created by amey on 01/11/16.
 */

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class Helper {
    static final String BASE_URL     = "http://192.168.1.38:8080/AndroidServer/";
    static final String LOGIN_URL    = BASE_URL + "login";
    static final String REGISTER_URL = BASE_URL + "register";
    static final String ADD_URL      = BASE_URL + "AddDelete";
    static final String DELETE_URL   = BASE_URL + "AddDelete";
    static final String COURSES_URL  = BASE_URL + "courses";
    static final String COURSES_KEY  = "courses";

    static final Integer ADD_COURSE_REQUEST = 0;

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    static String getResponse(String method, String urlString, HashMap<String,String> postDataParams) {
        Log.d("url          ",urlString);
        Log.d("params       ",postDataParams.toString());
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method.toUpperCase());
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(Helper.getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    static Pair<JSONArray, String> parseResponse(String response, String success) {
        JSONArray courses = null;
        String message;
        try {
            if(!response.equals("")) {
                JSONObject obj = new JSONObject(response);
                if(obj.getBoolean("status")) {
                    courses = obj.getJSONArray("data");
                    message = success;
                }
                else {
                    message = obj.getString("message");
                }
            }
            else {
                message = "Null Response";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("v           ",response);
            message = "Encountered JSONException";
        }
        return new Pair<>(courses,message);
    }

    static void makeToast(Context c, String s) {
        if (!s.equals("")) {
            Toast.makeText(c, s, Toast.LENGTH_LONG).show();
        }
    }
}