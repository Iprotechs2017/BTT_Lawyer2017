package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.utils.CloseCaseStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class CloseCaseActivity extends Activity {
    Toolbar toolbar;
    SharedPreferences prefs;
    Button confirm_case;
    JSONObject immigrant;
    RadioButton accept_radio,reject_radio;
    LinearLayout accept_layout,reject_layout;
    int updatedStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_case);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        confirm_case= (Button) findViewById(R.id.confirm_case);
        accept_layout= (LinearLayout) findViewById(R.id.accept_layout);
        reject_layout= (LinearLayout) findViewById(R.id.reject_layout);
        accept_radio= (RadioButton) findViewById(R.id.accept_radio);
        reject_radio= (RadioButton) findViewById(R.id.reject_radio);
        immigrant=DashBoardActivity.immigrantProfiles.get(DashBoardActivity.selectedImmigrantId);
        accept_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reject_radio.isChecked())
                {
                    reject_radio.setChecked(false);
                }
                accept_radio.setChecked(true);
                updatedStatus=1;
                try {
                    immigrant.put("status", updatedStatus);
                    Log.e("immigrant", immigrant.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        reject_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accept_radio.isChecked())
                {
                    accept_radio.setChecked(false);
                }
                reject_radio.setChecked(true);
                updatedStatus=2;
                try {
                    immigrant.put("status", updatedStatus);
                    Log.e("immigrant", immigrant.toString());

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        });
        Log.e("immm",DashBoardActivity.immigrantProfiles.toString());
        Log.e("immigrant", immigrant.toString());
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        confirm_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new CloseCaseStatus(CloseCaseActivity.this,immigrant,CloseCaseActivity.this);
            }
        });


        if (prefs.getInt("userType", -1) == 0)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_theam_color);
            confirm_case.setBackgroundColor((getResources().getColor(R.color.immigrant_theam_color)));
        }
        else if(prefs.getInt("userType", -1) == 1)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
            confirm_case.setBackgroundColor((getResources().getColor(R.color.solicor_theam_color)));
            changeTheam(R.color.solicor_theam_color);
        }
        else if(prefs.getInt("userType", -1) == 2)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_theam_color);
            confirm_case.setBackgroundColor((getResources().getColor(R.color.barrister_theam_color)));
        }

        toolbar.setTitle("Close Case");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

    }
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
    public static void closeApp()
    {

    }
}
