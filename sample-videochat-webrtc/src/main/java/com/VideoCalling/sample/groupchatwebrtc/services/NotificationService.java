package com.VideoCalling.sample.groupchatwebrtc.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.CallActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.NotificationActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.ShowmoreDocumentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.SplashActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends Service {

    /** indicates how to behave if the service is killed */
    int mStartMode;
    WebSocketClient mWebSocketClient;
    /** interface for clients that bind */
    IBinder mBinder;
  SharedPreferences pref;
    /** indicates whether onRebind should be used */
    boolean mAllowRebind;
    int NOTIFICATION_ID = 12345;
    /** Called when the service is being created. */
    @Override
    public void onCreate() {
     // Toast.makeText(this, "Started service", Toast.LENGTH_SHORT).show();
        connectWebSocket();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public  void showNotification(String data)
{
    Log.e("data-server",data);
    String [] split=data.split("-splspli-");
   if(split[1].toString().equalsIgnoreCase("call")) {
       Log.e("data-server-in", data);
       Intent myIntent = new Intent ( NotificationService.this, SplashActivity.class );
       myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       myIntent.putExtra("call", "yes");
       startActivity(myIntent);
     // CallActivity.start(NotificationService.this, true);
      // startActivity(new Intent(NotificationService.this, OpponentsActivity.class));
   }
    else
   {
       if(split[2].toString().indexOf("documents...")<0) {
           Intent intent = new Intent(NotificationService.this, NotificationActivity.class);
           PendingIntent pIntent = PendingIntent.getActivity(NotificationService.this, (int) System.currentTimeMillis(), intent, 0);
           Notification noti = new Notification.Builder(NotificationService.this)
                   .setSmallIcon(R.drawable.logo)
                   .setContentTitle("BTT Lawyer")
                   .setContentText(split[3].toString() + ":" + split[2].toString())
                   .setContentIntent(pIntent)
                   .build();
           // Log.e("datata", split[3].toString() + "--->" + split[2].toString());

           NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
           noti.flags |= Notification.FLAG_AUTO_CANCEL;
           noti.defaults = Notification.DEFAULT_ALL;
           notificationManager.notify(NOTIFICATION_ID++, noti);
           //Log.e("datata", split[3].toString() + ":" + split[2].toString());
       }
       else
       {
           Intent intent = new Intent(NotificationService.this, ShowmoreDocumentsActivity.class);
           intent.putExtra("id",split[0].toString());
           PendingIntent pIntent = PendingIntent.getActivity(NotificationService.this, (int) System.currentTimeMillis(), intent, 0);
           Notification noti = new Notification.Builder(NotificationService.this)
                   .setSmallIcon(R.drawable.logo)
                   .setContentTitle("BTT Lawyer")
                   .setContentText(split[2].toString())
                   .setContentIntent(pIntent)
                   .build();
           // Log.e("datata", split[3].toString() + "--->" + split[2].toString());

           NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
           noti.flags |= Notification.FLAG_AUTO_CANCEL;
           noti.defaults = Notification.DEFAULT_ALL;
           notificationManager.notify(NOTIFICATION_ID++, noti);

       }
   }
   }
    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        startService(new Intent(NotificationService.this,NotificationService.class));
    }
    private void connectWebSocket() {
        pref = getSharedPreferences("loginDetails", MODE_PRIVATE);
        URI uri;
        try {
            uri = new URI("ws://183.82.113.165:8085");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened----service");
                if( pref.getInt("userId", -1)!=-1)
                {
                    int userId = pref.getInt("userId", -1);
                    String data = userId + "-splspli-" + "reg";
                    mWebSocketClient.send(data);

                 }
            }
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

            @Override
            public void onMessage(String s)
            {
                final String message = s;

              //  Log.e("messageasdas",message);

                showNotification(message);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                startService(new Intent(NotificationService.this,NotificationService.class));

               // Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                startService(new Intent(NotificationService.this,NotificationService.class));
                //Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
