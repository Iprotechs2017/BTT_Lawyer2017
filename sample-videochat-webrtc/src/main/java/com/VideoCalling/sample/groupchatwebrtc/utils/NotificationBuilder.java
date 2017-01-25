package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.NotificationActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.ShowmoreDocumentsActivity;

/**
 * Created by Harishma Velagala on 24-01-2017.
 */
public class NotificationBuilder {
Context context;
int NOTIFICATION_ID=1;
    public NotificationBuilder(Context context,String s)
    {
this.context=context;
        createNotification(s);
    }
    public void createNotification(String message) {
        try {
            // Log.e("data", message);
            String[] split = message.split("-splspli-");
            //  Log.e("datata", split[3].toString() + "<--->" + split[2].toString());
            if (split[2].toString().indexOf("documents...") < 0) {
                if (split[2].toString().trim().length() > 0) {
                    Intent intent = new Intent(context, NotificationActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
                    Notification noti = new Notification.Builder(context)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("BTT Lawyer")
                            .setContentText(split[3].toString() + ":" + split[2].toString())
                            .setContentIntent(pIntent)
                            .build();
                    // Log.e("datata", split[3].toString() + "--->" + split[2].toString());

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    noti.defaults = Notification.DEFAULT_ALL;
                    notificationManager.notify(NOTIFICATION_ID++, noti);
                    //Log.e("datata", split[3].toString() + ":" + split[2].toString());
                    
                }
            } else {

                Intent intent = new Intent(context, ShowmoreDocumentsActivity.class).putExtra("id", split[3].toString());
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
                Notification noti = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("BTT Lawyer")
                        .setContentText(split[2].toString())
                        .setContentIntent(pIntent)
                        .build();
                // Log.e("datata", split[3].toString() + "--->" + split[2].toString());

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                noti.defaults = Notification.DEFAULT_ALL;
                notificationManager.notify(NOTIFICATION_ID++, noti);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
