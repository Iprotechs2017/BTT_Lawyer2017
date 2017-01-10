package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.VideoCalling.sample.groupchatwebrtc.R;

/**
 * Created by Harishma Velagala on 09-01-2017.
 */
public class TheamChange {
    SharedPreferences prefs;
    Context context;
    Activity activity;
    public TheamChange(Toolbar toolbar,Context context)
    {
        this.context=context;
        prefs = context.getSharedPreferences("loginDetails", context.MODE_PRIVATE);
        if (prefs.getInt("userType", -1) == 0)
        {
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_theam_color);
        }
        else if(prefs.getInt("userType", -1) == 1)
        {
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.solicor_theam_color));
            changeTheam(R.color.solicor_theam_color);
        }
        else if(prefs.getInt("userType", -1) == 2)
        {
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_theam_color);
        }

    }
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(getResources().getColor(color));
        }
    }
}
