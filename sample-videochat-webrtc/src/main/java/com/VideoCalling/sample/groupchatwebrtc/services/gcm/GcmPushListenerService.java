package com.VideoCalling.sample.groupchatwebrtc.services.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;
import com.quickblox.sample.core.gcm.CoreGcmPushListenerService;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.constant.GcmConsts;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.LoginActivity;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.services.MyService;
import com.quickblox.users.model.QBUser;

/**
 * Created by tereha on 13.05.16.
 */
public class GcmPushListenerService extends GcmListenerService {
    private static final String TAG = CoreGcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString(GcmConsts.EXTRA_GCM_MESSAGE);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d("datadata",data.toString());
        if(message.trim().split("@").length>1)
        {
            startService(new Intent(this, MyService.class).putExtra("msg",message.trim().split("@")[1].toString()));
        }
       // showNotification(message);
       // startService(new Intent(this, MyService.class).putExtra("msg",message));
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        if (sharedPrefsHelper.hasQbUser()) {
            Log.d(TAG, "App have logined user");
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            startLoginService(qbUser);
        }
    }

    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
    }

}