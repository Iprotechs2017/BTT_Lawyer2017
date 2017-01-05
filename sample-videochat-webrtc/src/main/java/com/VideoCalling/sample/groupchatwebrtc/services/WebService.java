package com.VideoCalling.sample.groupchatwebrtc.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.activities.ClientAfterLoginActivity;
import com.VideoCalling.sample.groupchatwebrtc.util.AndroidMultiPartEntity;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by rajesh on 22/11/16.
 */
public class WebService extends Service {

    String tag = "TestService";
    String status=null;
String file_path;
    @Override
    public void onCreate() {
        super.onCreate();
   //     Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);
       try {
           file_path = intent.getStringExtra("path");
           //  Log.e("service in",file_path);
           UploadVideo web = new UploadVideo();
           web.execute();
       }
       catch (Exception e)
       {

       }
           return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(this, "killed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    private class UploadVideo extends AsyncTask<URL, Integer, Integer> {
      int docId;
        protected Integer doInBackground(URL... urls) {
            Long totalSize = null;
            try {
                docId=postAPICall(file_path, WebService.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return docId;
        }


        protected void onPostExecute(Integer result) {
            if(result!=0) {
                Toast.makeText(WebService.this, "file uploded...!", Toast.LENGTH_SHORT).show();
                stopService(new Intent(WebService.this, WebService.class));
            }
            stopService(new Intent(WebService.this, WebService.class));
        }
    }
    public int postAPICall(String strurl, Context context) throws Exception
    {
        int docId = 0;
        SharedPreferences prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        String ex=file_path.split("/")[file_path.split("/").length - 1].toString();
       Log.e("file_path",file_path);
        //String url="http://35.163.24.72:8080/VedioApp/service/uploadfile/upload/name/"+ex+"/type/"+file_path.toString().substring(file_path.lastIndexOf("."))+"/uploadedby/"+prefs.getInt("userId",-1)+"/uploadedto/"+prefs.getInt("userId",-1);
     //   String url="http://35.163.24.72:8080/VedioApp/service/uploadfile/upload/name/"+ex+"/type/"+file_path.toString().substring(file_path.lastIndexOf("."))+"/uploadedby/30/uploadedto/"+prefs.getInt("userId",-1);
        String url="http://35.163.24.72:8080/VedioApp/service/uploadfile/upload/name/"+ex.replaceAll("\\s+","").split("\\.")[0]+"/type/"+file_path.toString().substring(file_path.lastIndexOf("."))+"/uploadedby/"+prefs.getInt("userId",-1)+"/uploadedto/"+prefs.getInt("userId",-1);

        strurl = url.replace(" ", "%20");
        Log.e("url",url);
        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                new AndroidMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {
                    }
                });
        HttpPost httpPost = new HttpPost(strurl);
        File sourceFile = new File(file_path);
        sourceFile.getName();
        entity.addPart("uploadFile", new FileBody(sourceFile));
        httpPost.setEntity(entity);
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient( context );
        response = httpClient.execute(httpPost);
        if(response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            docId=Integer.parseInt(reader.readLine().toString());

        }
        else
        {

        }
        return docId;
    }
}