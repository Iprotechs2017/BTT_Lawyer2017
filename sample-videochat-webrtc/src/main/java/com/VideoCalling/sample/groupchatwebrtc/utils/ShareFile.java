package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.activities.DashBoardActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Harishma Velagala on 09-01-2017.
 */
public class ShareFile {
    Context context;
    int selectedPosition;
    SharedPreferences prefs;
    ArrayList<DocumentsDetailsWithIds> DocumentsDetailsWithIdsArraylist=new ArrayList<DocumentsDetailsWithIds>();
    public ShareFile(final Context context, final ArrayList<DocumentsDetailsWithIds> DocumentsDetailsWithIdsArraylist, final int selectedPosition)
    {
this.context=context;
        this.selectedPosition=selectedPosition;
        prefs = context.getSharedPreferences("loginDetails", context.MODE_PRIVATE);
        this.DocumentsDetailsWithIdsArraylist=DocumentsDetailsWithIdsArraylist;
       new ShareDoc().execute();

    }
    class ShareDoc extends AsyncTask<URL, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("loading...");
              progressDialog.show();
        }

        protected Integer doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            int code = 0;
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("documentId",DocumentsDetailsWithIdsArraylist.get(selectedPosition).getDocumentId());
                jsonObject.put("sharedBy",prefs.getInt("userId",0));
                jsonObject.put("docOwner", DashBoardActivity.selectedImmigrantId);
                jsonObject.put("sharedDate", DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                jsonObject.put("sharedTo", DashBoardActivity.barrister.get("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
       /*     String url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/"+prefs.getInt("userId",0)+"/sharedTo/"+OpponentsActivity.userType.get(0).getId()+"/docOwner/"+ownerId+"/docId/"+1+"";
            Log.e("url",url);*/
            Log.e("jsonObject", jsonObject.toString());
            try {
                code=postAPICall1("http://35.163.24.72:8080/VedioApp/service/DocumentShare", jsonObject.toString(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }


        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if(result==500)
            {
                Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
            }
            else if(result==200 || result==201)
            {
                Toast.makeText(context, "file shared successfully...", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public int postAPICall1(String strurl, String jsonString, final Context context) throws Exception
    {

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

        Log.e("resultJson",resultJson);
    return response.getStatusLine().getStatusCode();
    }
}
