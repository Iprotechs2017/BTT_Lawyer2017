package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.fragments.UplodedDocs;

public class ShowmoreDocumentsActivity extends AppCompatActivity {
Toolbar toolbar;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmore_documents);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Documents");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_w));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowmoreDocumentsActivity.this.finish();
            }
        });
            prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
            if (prefs.getInt("userType", -1) == 0) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
                changeTheam(R.color.immigrant_theam_color);
            } else if (prefs.getInt("userType", -1) == 1) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
                changeTheam(R.color.solicor_theam_color);
            } else if (prefs.getInt("userType", -1) == 2) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
                changeTheam(R.color.barrister_theam_color);
            }

        Bundle bundle = new Bundle();
        bundle.putString("yes", "full");
        UplodedDocs uplodedDocs=new UplodedDocs();
        uplodedDocs.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameParent,uplodedDocs );
        transaction.commit();
    }
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
}
