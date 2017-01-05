package com.VideoCalling.sample.groupchatwebrtc.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.LoginActivity;

public class MyService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(this, "Bind Created", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "cmd Created", Toast.LENGTH_LONG).show();
        showNotification(intent.getStringExtra("msg"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();


    }
    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();


    }
    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }
    public void showNotification(String msg)
    {

        Intent snoozeIntent = new Intent(this, LoginActivity.class);
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);
        PendingIntent piSnooze1 = PendingIntent.getService(this, 0, snoozeIntent, 0);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(("This is title"))
                        .setContentText("This is text")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .addAction (R.drawable.logo,
                                "reject", piSnooze1)
                        .addAction (R.drawable.logo,
                                "accept", piSnooze);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }
}
