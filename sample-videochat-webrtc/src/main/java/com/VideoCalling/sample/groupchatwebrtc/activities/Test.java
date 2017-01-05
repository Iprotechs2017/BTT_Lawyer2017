package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.AndroidMultiPartEntity;
import com.VideoCalling.sample.groupchatwebrtc.util.HttpConnectionsTest;
import com.VideoCalling.sample.groupchatwebrtc.util.HttpFailureException;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "VideoCalling");

        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        Intent i=new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "VideoCalling")), "*/*");
        startActivity(i);
    }
    /*11-23 13:36:38.300 21252-22070/com.VideoCalling.sample.groupchatwebrtc E/response: uploaded
    11-23 13:36:48.109 21252-22167/com.VideoCalling.sample.groupchatwebrtc E/jsonObject111: {"name":"\/storage\/sdcard0\/WhatsApp\/Media\/WhatsApp Images\/IMG-20161123-WA0000.jpg","docType":".mp4","uploadedBy":4}
    11-23 13:36:48.109 21252-22167/com.VideoCalling.sample.groupchatwebrtc E/filePath: filePath - /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161123-WA0000.jpg
    11-23 13:36:48.553 21252-22167/com.VideoCalling.sample.groupchatwebrtc E/response: uploaded
    11-23 13:37:38.376 21252-21252/com.VideoCalling.sample.groupchatwebrtc E/filePath: /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161122-WA0001.jpg
    11-23 13:37:41.018 21252-22907/com.VideoCalling.sample.groupchatwebrtc E/jsonObject111: {"name":"\/storage\/sdcard0\/WhatsApp\/Media\/WhatsApp Images\/IMG-20161122-WA0001.jpg","docType":".mp4","uploadedBy":4}
    11-23 13:37:41.020 21252-22907/com.VideoCalling.sample.groupchatwebrtc E/filePath: filePath - /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161122-WA0001.jpg
    11-23 13:37:42.271 21252-22907/com.quickblox.sample.





     E/response: uploaded
    11-23 13:38:01.890 21252-21252/com.VideoCalling.sample.groupchatwebrtc E/filePath: /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161122-WA0005.jpg
    11-23 13:38:03.417 21252-23131/com.VideoCalling.sample.groupchatwebrtc E/jsonObject111: {"name":"\/storage\/sdcard0\/WhatsApp\/Media\/WhatsApp Images\/IMG-20161122-WA0005.jpg","docType":".mp4","uploadedBy":4}
    11-23 13:38:03.417 21252-23131/com.VideoCalling.sample.groupchatwebrtc E/filePath: filePath - /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161122-WA0005.jpg
    11-23 13:38:05.200 21252-23131/com.VideoCalling.sample.groupchatwebrtc E/response: uploaded
    11-23 13:38:34.824 21252-21252/com.VideoCalling.sample.groupchatwebrtc E/filePath: /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161119-WA0003.jpg
    11-23 13:38:36.701 21252-23459/com.VideoCalling.sample.groupchatwebrtc E/jsonObject111: {"name":"\/storage\/sdcard0\/WhatsApp\/Media\/WhatsApp Images\/IMG-20161119-WA0003.jpg","docType":".mp4","uploadedBy":4}
    11-23 13:38:36.701 21252-23459/com.VideoCalling.sample.groupchatwebrtc E/filePath: filePath - /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161119-WA0003.jpg
    11-23 13:38:37.892 21252-23459/com.VideoCalling.sample.groupchatwebrtc E/response: uploaded
    11-23 13:38:41.779 21252-23506/com.VideoCalling.sample.groupchatwebrtc E/jsonObject111: {"name":"\/storage\/sdcard0\/WhatsApp\/Media\/WhatsApp Images\/IMG-20161119-WA0003.jpg","docType":".mp4","uploadedBy":4}
    11-23 13:38:41.780 21252-23506/com.VideoCalling.sample.groupchatwebrtc E/filePath: filePath - /storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20161119-WA0003.jpg
    11-23 13:38:42.856 21252-23506/com.VideoCalling.sample.groupchatwebrtc E/response: uploaded

*/
}
