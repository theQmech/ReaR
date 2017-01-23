package com.angmar.witch_king.newforce1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class UserMainActivity extends AppCompatActivity {
    public String myurl;
    public static String userId;
    public static String EXTRA_MESSAGE;
    public static String rentedBikeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Button rent = (Button) findViewById(R.id.btn_rent);
        Button unrent = (Button) findViewById(R.id.btn_unrent);
        TextView userDetails = (TextView) findViewById(R.id.userDetails);
        rent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent add = new Intent(UserMainActivity.this, UserRentActivity.class);
                add.putExtra(EXTRA_MESSAGE, userId);
                startActivity(add);
            }
        });

        unrent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent add = new Intent(UserMainActivity.this, UserUnrentActivity.class);
                add.putExtra(EXTRA_MESSAGE, userId);
                add.putExtra("bikeId", rentedBikeID);
                startActivity(add);
            }
        });

        userId = getIntent().getStringExtra("userId");
        rentedBikeID = getIntent().getStringExtra("rented");
        if (rentedBikeID == null) {
//            unrent.setVisibility(View.GONE);
        }
        userDetails.append(" "+ userId);
        myurl = LoginActivity.myurl;
//        if (userId.contains("Nothing")){
//            Log.e("MainActivity. OnCreate", "userId error");
//        }
    }
}
