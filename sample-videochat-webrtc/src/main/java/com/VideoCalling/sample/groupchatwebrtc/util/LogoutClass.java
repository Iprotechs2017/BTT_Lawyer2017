package com.VideoCalling.sample.groupchatwebrtc.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.VideoCalling.sample.groupchatwebrtc.activities.LoginActivity;
public class LogoutClass
{
    public LogoutClass()
    {

    }
    public void clearSesson(Context context) {


        SharedPreferences settings = context.getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent intent=new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }
}
