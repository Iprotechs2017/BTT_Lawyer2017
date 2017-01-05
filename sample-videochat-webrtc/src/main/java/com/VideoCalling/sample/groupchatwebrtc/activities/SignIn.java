package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.VideoCalling.sample.groupchatwebrtc.R;

public class SignIn extends AppCompatActivity {
    Toolbar toolbar;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
    }
    public void initViews()
    {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
    /*    login= (Button) findViewById(R.id.signin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this,OpponentsActivity.class));
            }
        });*/
    }
}
