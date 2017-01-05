package com.VideoCalling.sample.groupchatwebrtc.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.activities.LoginActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class APIServices extends Service {
    int mStartMode;
    WebSocketClient mWebSocketClient;
    /** interface for clients that bind */
    IBinder mBinder;
    SharedPreferences pref;
    /** indicates whether onRebind should be used */
    boolean mAllowRebind;
    public APIServices() {
    }

    public void startApiCalls()
    {
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                writeLogs("testing logs");
                Log.e("service ","running");
                String url = "http://35.163.24.72:8080/VedioApp/service/user/type/0";
                Log.e("urlls",url);
                try {
                    postAPICall1(url, APIServices.this);
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/user/type/0", APIServices.this);
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/user/login", APIServices.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 9000*60*60);
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                Log.e("service ","running");
                Log.e("service ","running");
                String url = "http://35.163.24.72:8080/VedioApp/service/user/type/0";
                Log.e("urlls",url);
                try {
                    postAPICall1(url, APIServices.this);
                    postAPICall1("http://35.163.24.72:8080/VedioApp/service/user/type/0", APIServices.this);
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/user/login", APIServices.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        },0,5000);


    }
    public void postAPICall1(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";
        while ((line = reader.readLine()) != null) {
            resultJson += line;
        }
        Log.e("barrister result:",resultJson);
        writeLogs("barrister result:"+resultJson);



            /*JSONArray jsonArray = new JSONArray(resultJson);
            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                *//*UserType userTypeObj = new UserType(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("email"),
                        jsonObject.getString("registeredBy"),
                        jsonObject.getString("userType"));
                userType.add(userTypeObj);*//*
                userTypeDetails.clear();
                userTypeDetails.put("id",jsonObject.getInt("id"));
                userTypeDetails.put("name",jsonObject.getString("name"));
                userTypeDetails.put("email",jsonObject.getString("email"));
                userTypeDetails.put("registeredBy",jsonObject.getInt("registeredBy"));
                userTypeDetails.put("userType",jsonObject.getInt("userType"));
                Log.e("userTypeDetails", userTypeDetails.get("name").toString());
                baristerName=userTypeDetails.get("name").toString();
*/






    }
    public void postAPICall(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";
        while ((line = reader.readLine()) != null) {
            resultJson += line;
        }
        Log.e("opponents Result", resultJson);
        writeLogs("opponents Result:"+resultJson);


    }
    public void postAPICall2(String strurl,  final Context context) throws Exception
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("email","admin@iprotechs.com");

            jsonObject.put("password","admin");

        } catch (JSONException e) {
            e.printStackTrace();
        }
     String jsonString=jsonObject.toString();
        strurl = strurl.replace(" ", "%20");

        HttpPost httpPost = new HttpPost(strurl);
        if (jsonString != null)
        {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient( context );
        response = httpClient.execute(httpPost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";

        while ((line = reader.readLine()) != null)
        {
            resultJson += line;
        }
        Log.e("status",response.getStatusLine().getStatusCode()+"");

        int code=response.getStatusLine().getStatusCode();
        Log.e("Login result",resultJson);




    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
       // connectWebSocket();
        startApiCalls();
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

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        startService(new Intent(APIServices.this,APIServices.class));
    }
    public void writeLogs(String logs)
    {
        String simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());

        File  file = null;
        try {
            File newFolder = new File(Environment.getExternalStorageDirectory(), "LogsFolder");
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            try {
                file = new File(newFolder, "logs" + ".txt");
                file.createNewFile();
            } catch (Exception ex) {
                System.out.println("ex: " + ex);
            }
        } catch (Exception e) {
            System.out.println("e: " + e);
        }
        String path = Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS) + "/logs.txt";
        //File file = new File(path);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file, true), 1024);
            out.write(new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date())+":--->"+logs);
            out.newLine();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
