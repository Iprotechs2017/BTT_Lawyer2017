package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.utils.CloseCaseStatus;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class CloseCaseActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences prefs;
    Button confirm_case;
    JSONObject immigrant;
    RadioButton accept_radio,reject_radio;
    LinearLayout accept_layout,reject_layout;
    int updatedStatus;
    TextView immigrant_name;
    int NOTIFICATION_ID = 1;
    WebSocketClient mWebSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_case);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Case status");
        connectWebSocket();
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        immigrant_name= (TextView) findViewById(R.id.immigrant_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_w));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseCaseActivity.this.finish();
            }
        });
        confirm_case= (Button) findViewById(R.id.confirm_case);
        accept_layout= (LinearLayout) findViewById(R.id.accept_layout);
        reject_layout= (LinearLayout) findViewById(R.id.reject_layout);
        accept_radio= (RadioButton) findViewById(R.id.accept_radio);
        reject_radio= (RadioButton) findViewById(R.id.reject_radio);
        immigrant=DashBoardActivity.immigrantProfiles.get(DashBoardActivity.selectedImmigrantId);
        try {
            immigrant_name.setText(immigrant.getString("name")+" Immigration Case Status Form");
            if(immigrant.getInt("status")==1)
            {
                accept_radio.setText("Accepted");
                Toast.makeText(CloseCaseActivity.this, immigrant.getString("name")+" Immigration Case Accepted", Toast.LENGTH_SHORT).show();
            }
            else if(immigrant.getInt("status")==2)
            {
                reject_radio.setText("Rejected");
                Toast.makeText(CloseCaseActivity.this,immigrant.getString("name")+" Immigration Case Rejected", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(immigrant.getInt("status")==1)
            {
                confirm_case.setEnabled(false);
                reject_layout.setVisibility(View.GONE);
                accept_radio.setChecked(true);
                confirm_case.setVisibility(View.GONE);
           /*
                accept_radio.setEnabled(false);*/
            }
            else if(immigrant.getInt("status")==2)
            {
                accept_layout.setVisibility(View.GONE);
                reject_radio.setChecked(true);
                confirm_case.setVisibility(View.GONE);
                confirm_case.setEnabled(false);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        accept_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reject_radio.isChecked()) {
                    reject_radio.setChecked(false);
                }
                accept_radio.setChecked(true);
                updatedStatus = 1;


            }
        });
        accept_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reject_radio.isChecked()) {
                    reject_radio.setChecked(false);
                }
                accept_radio.setChecked(true);
                updatedStatus = 1;


            }
        });
        reject_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accept_radio.isChecked())
                {
                    accept_radio.setChecked(false);
                }
                reject_radio.setChecked(true);
                updatedStatus=2;

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


            }
        });
        Log.e("immm",DashBoardActivity.immigrantProfiles.toString());
        Log.e("immigrant", immigrant.toString());
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        confirm_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(immigrant.getInt("status")==0)
    {
        if (accept_radio.isChecked() || reject_radio.isChecked()) {
            try {
                immigrant.put("status", updatedStatus);
                Log.e("immigrant", immigrant.toString());

            } catch (JSONException e) {

                e.printStackTrace();
            }
            String cases = null;
            if(updatedStatus==1)
            {
                cases="Your case has been Accepted";
            }
            else if(updatedStatus==2)
            {
                cases="Your case has been Rejected";
            }
            String data=DashBoardActivity.selectedImmigrantId + "-splspli-" + "notification" + "-splspli-" + cases + "-splspli-" + prefs.getString("name", null)+ "-splspli-" + DashBoardActivity.selectedImmigrantId+"-splspli-"+"case" ;
            new CloseCaseStatus(CloseCaseActivity.this, immigrant, CloseCaseActivity.this,mWebSocketClient,data);
        } else {
            Toast.makeText(CloseCaseActivity.this, "select case status accept or reject...", Toast.LENGTH_SHORT).show();


        }

    }
                    else if(immigrant.getInt("status")==1)
                    {
                        Toast.makeText(CloseCaseActivity.this, immigrant.getString("name")+" Immigration Case Accepted", Toast.LENGTH_SHORT).show();
                    }
                    else if(immigrant.getInt("status")==2)
                    {
                        Toast.makeText(CloseCaseActivity.this,immigrant.getString("name")+" Immigration Case Rejected", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        if (prefs.getInt("userType", -1) == 0) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_notifi_color);
            confirm_case.setBackgroundColor((getResources().getColor(R.color.immigrant_theam_color)));

        } else if (prefs.getInt("userType", -1) == 1) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
            changeTheam(R.color.solicitor_notifi_color);
            confirm_case.setBackgroundColor((getResources().getColor(R.color.solicor_theam_color)));
        } else if (prefs.getInt("userType", -1) == 2) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_notifi_color);
            confirm_case.setBackgroundColor((getResources().getColor(R.color.barrister_theam_color)));

        }


    }
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        immigrant=null;
    }
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://183.82.113.165:8089");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened");
                int userId = prefs.getInt("userId", -1);
                String data = userId + "-splspli-" + "reg";
                try {
                    mWebSocketClient.send(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.e("message---->", message);
                createNotification(message);


            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void createNotification(String message) {
                try {
                    // Log.e("data", message);
                    String[] split = message.split("-splspli-");
                    //  Log.e("datata", split[3].toString() + "<--->" + split[2].toString());
                    if (split[2].toString().indexOf("documents...") < 0) {
                        if (split[2].toString().trim().length() > 0) {
                            Intent intent = new Intent(CloseCaseActivity.this, NotificationActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(CloseCaseActivity.this, (int) System.currentTimeMillis(), intent, 0);
                            Notification noti = new Notification.Builder(CloseCaseActivity.this)
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
                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onClose(int i, String s, boolean b)

            {
                if(prefs.getString("service","").equalsIgnoreCase("stop")) {

                    //connectWebSocket();
                }
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {

                connectWebSocket();
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
