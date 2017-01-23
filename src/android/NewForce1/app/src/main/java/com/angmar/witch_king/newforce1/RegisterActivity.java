package com.angmar.witch_king.newforce1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
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

public class RegisterActivity extends Fragment {

    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    public String myurl;
    private UserRegisterTask mAuthTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_register, container, false);

        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.email);
        mNameView = (AutoCompleteTextView) rootView.findViewById(R.id.name);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });
        myurl = LoginActivity.myurl;
        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        return rootView;
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserRegisterTask(email, password, name);
            mAuthTask.execute();
        }
    }

    public class UserRegisterTask extends AsyncTask<String, Void, String> {

        private final String mUsername;
        private final String mPassword;
        private final String mName;
        UserRegisterTask(String username, String password, String name) {
            mUsername = username;
            mPassword = password;
            mName = name;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection conn = null;
            String urlLogin = myurl+ "Register";
            try {
                // Simulate network access.
                URL url = new URL(urlLogin);
                JSONObject loginData =new JSONObject();
                loginData.put("id", mUsername);
                loginData.put("password",mPassword);
                loginData.put("name",mName);
                Log.e("registerData",loginData.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(1000);

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
                    Log.e("RegisterActivity","Error");
                    return new String("false");
                }
            } catch (Exception e) {
                Log.e("RegisterActivity","Exception" + e.getLocalizedMessage());
                return new String("false");
            }
            finally {
                if (conn == null) {
                    conn.disconnect();
                    Log.e("RegisterActivity","Can't Connect to the network");
                    return new String("false");
                }
            }
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            Intent screenChange=null;
            Log.e("UserRegisterTask",success);

            String email = null;
            String name = null;

            if (success.startsWith("false")) {
                if(success.contains("violates unique constraint"))
                {
                    mEmailView.setError("Email already registered");
                    mEmailView.requestFocus();
                }
                else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    Helper.makeToast(getContext(),success.substring(6));
                }
            }
            else {
                String error = null;
                try {
                    JSONObject ob = new JSONObject(success);
                    String status = ob.getString("status");
                    if (status.equals("true")) {
                        JSONObject dat = ob.getJSONObject("data");
                        email = dat.getString("email");
                        name = dat.getString("name");
                    } else {
                        error = ob.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.toString();
                }

                if (name != null && email != null) {
                    // GOTO HOMEs
                    screenChange = new Intent(getActivity(), HomeActivity.class);
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(
                            "com.angmar.witch_king.newforce1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(("name"), name);
                    editor.putString(("email"), email);
                    editor.commit();
                    Helper.makeToast(getContext(), "Registered Successfully!");
                    startActivity(screenChange);
                } else {

                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();

                    Helper.makeToast(getContext(), error);
                }
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
