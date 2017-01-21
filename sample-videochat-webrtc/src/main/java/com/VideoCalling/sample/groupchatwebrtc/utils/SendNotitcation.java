package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.activities.DashBoardActivity;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Harishma Velagala on 10-01-2017.
 */
public class SendNotitcation
{
    Context context;
    JSONObject jsonObject;
public SendNotitcation(Context context,JSONObject jsonObject)
{
    this.context=context;
this.jsonObject=jsonObject;
    new DownloadFilesTask().execute();
}
     class DownloadFilesTask extends AsyncTask<URL, Integer, Integer> {
         ProgressDialog progressDialog;
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             progressDialog=new ProgressDialog(context);
             progressDialog.setCancelable(false);
             progressDialog.setMessage("loading...");
             progressDialog.show();
         }

         protected Integer doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            int code = 0;

             try {
                 code=    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications", context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }


        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
             if(result==201)
            {
                Toast.makeText(context, "Notification sent succesfully...", Toast.LENGTH_SHORT).show();
                DashBoardActivity.sendNotification();
            }

            else if (result== 500) {
                 Toast.makeText(context, "Server busy try again...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public int postAPICall(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        int status = 0;
        Log.e("strurl", strurl);
        HttpPost httpPost = new HttpPost(strurl);
        if (jsonObject.toString() != null)
        {
            StringEntity entity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(entity);
        }
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        Log.e("response",response.getStatusLine().getStatusCode()+"--");
        if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
            status=response.getStatusLine().getStatusCode();
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("notifications", resultJson);
            if (resultJson != null) {
                try {
                    JSONArray jsonArray = new JSONArray(resultJson);

                } catch (Exception e) {
                }
            }
            else {
                }
        }
        else if(response.getStatusLine().getStatusCode()==204)
        {
            status=response.getStatusLine().getStatusCode();
        }

        else if (response.getStatusLine().getStatusCode() == 500) {
            status=response.getStatusLine().getStatusCode();
        }
 return status;
        //Log.e("result", resultJson);
    }
}
