package com.angmar.witch_king.newforce1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminMainActivity extends AppCompatActivity {

    public String myurl;
    public static String userId;
    public String EXTRA_MESSAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        Button request = (Button) findViewById(R.id.btn_request);
        Button complaint = (Button) findViewById(R.id.btn_complaint);
        Button logoutButton = (Button) findViewById(R.id.btn_logout);
        TextView userDetails = (TextView) findViewById(R.id.userDetails);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AdminMainActivity.this, LoginActivity.class);
                add.putExtra(EXTRA_MESSAGE, userId);
                startActivity(add);
            }
        });
        request.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AdminMainActivity.this, AdminRequestActivity.class);
                add.putExtra(EXTRA_MESSAGE, userId);
                startActivity(add);
            }
        });
        complaint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AdminMainActivity.this, AdminComplaintActivity.class);
                add.putExtra(EXTRA_MESSAGE, userId);
                startActivity(add);
            }
        });
        userId = getIntent().getStringExtra(LoginActivity.EXTRA_MESSAGE);
        userDetails.append(" "+ userId);
        myurl = LoginActivity.myurl;
        if (userId.contains("Nothing")){
            Log.e("MainActivity. OnCreate", "userId error");
        }
    }
}
