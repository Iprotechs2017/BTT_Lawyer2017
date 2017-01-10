package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.services.NotificationService;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Harishma Velagala on 10-01-2017.
 */
public class CloseCaseStatus {
    Context context;
    JSONObject userDetails;
    Activity activity;
    public CloseCaseStatus(Context context,JSONObject userDetails,Activity activity)
    {
        this.context=context;
        this.activity=activity;
        this.userDetails=userDetails;
        new UpdateCase().execute();
    }
    public int postAPICall(String strurl, String jsonString, final Context context) throws Exception
    {
         int code=0;
        strurl = strurl.replace(" ", "%20");

        HttpPut httpPost = new HttpPut(strurl);
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
        code=response.getStatusLine().getStatusCode();
        Log.e("status code --->", response.getStatusLine().getStatusCode() + "---");


        return code;

    }
    public class UpdateCase extends AsyncTask<URL, Integer, Integer> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Integer doInBackground(URL... urls) {
           int code=0;
            try {
                Log.e("userDetails",userDetails.toString());
                code=postAPICall("http://35.163.24.72:8080/VedioApp/service/user",userDetails.toString(), context);
                } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
progressDialog.dismiss();
            try {
                if(result == 200) {
                    new AlertDialog.Builder(context)
                            .setMessage("Case status updated succesfully")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.finish();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                if(result==500)
                {
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
                else if(result==401) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
