package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentDetails;
import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentsDetailsWithIds;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadedDocumentsActivity extends AppCompatActivity {
    RecyclerView recycler_view;
    VideocallAdapter documentsAdapter;
    SharedPreferences prefs;
    ArrayList<DocumentsDetailsWithIds> DocumentsDetailsWithIdsArraylist = new ArrayList<DocumentsDetailsWithIds>();
    ArrayList<DocumentDetails> DocumentDetailsArray = new ArrayList<DocumentDetails>();
    int documentId;
    TextView title;
    Toolbar toolbar;
    RelativeLayout toolbar_parent;
    Typeface typeface;
    ProgressDialog progressDialog;
    de.hdodenhof.circleimageview.CircleImageView call,notification,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docs_custom_tabs_layout);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
       /* toolbar_parent= (RelativeLayout) findViewById(R.id.toolbar_parent);
        toolbar_parent.setVisibility(View.VISIBLE);*/
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
       /* title= (TextView) toolbar.findViewById(R.id.screen_title);
        title.setText("Uploaded Documents");
        call= (CircleImageView) toolbar.findViewById(R.id.videocall);
        logout= (CircleImageView) toolbar.findViewById(R.id.logout);
        notification= (CircleImageView) toolbar.findViewById(R.id.show_notifications);
        call.setVisibility(View.GONE);
        notification.setVisibility(View.GONE);*/
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        typeface = Typeface.createFromAsset(getAssets(), "QuicksandRegular.ttf");
        new getSharedDocs().execute();
    }

    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder> {

        Context context;
        View view1;
        ViewHolder viewHolder1;
        ArrayList<DocumentDetails> documentsList;

        public VideocallAdapter(Context context1, ArrayList<DocumentDetails> documentsList) {
            this.documentsList = documentsList;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView callerName, videoName, callDate;
            public ImageView contactImage;

            public ViewHolder(View v) {

                super(v);
                contactImage = (ImageView) v.findViewById(R.id.contactImage);
                callerName = (TextView) v.findViewById(R.id.callerName);
                videoName = (TextView) v.findViewById(R.id.videoName);
                callDate = (TextView) v.findViewById(R.id.callDate);
            }
        }

        @Override
        public VideocallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view1 = LayoutInflater.from(context).inflate(R.layout.videos_custom_layout, parent, false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DocumentDetails documentDetails = documentsList.get(position);
            String docName = documentDetails.getName().toString();
            holder.contactImage.setImageResource(R.drawable.docs);
            holder.callerName.setText(documentDetails.getUploadedTo() + "");
            holder.videoName.setText(documentDetails.getName().toString());
            holder.callDate.setText(documentDetails.getUploadedDate().toString());
            holder.callerName.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);
        }

        @Override
        public int getItemCount() {

            return documentsList.size();
        }
    }

    private class getSharedDocs extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            String url;
            JSONObject jsonObject = new JSONObject();
            url = "http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/" + prefs.getInt("userId", 0) + "/sharedTo/1";
            Log.e("url", url);
            try {
                getUploadedDocsIds(url, jsonObject.toString(), UploadedDocumentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }

        protected void onPostExecute(Long result) {
        }
    }

    public void getUploadedDocsIds(String strurl, String jsonString, final Context context) throws Exception {
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
        Log.e("responseeee", resultJson);
        try {
            JSONArray jsonArray = new JSONArray(resultJson);
            if (jsonArray.length() > 0) {
                for (int i = 0; i <= jsonArray.length() - 1; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    documentId = jsonObject.getInt("documentId");
                    String url = "http://35.163.24.72:8080/VedioApp/service/document/getdocumentdata/id/" + documentId;
                    try {
                        getDocumentDetailsById(url, jsonObject.toString(), UploadedDocumentsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DocumentsDetailsWithIds documentsDetailsWithIds = new DocumentsDetailsWithIds(jsonObject.getInt("id"), jsonObject.getInt("documentId"), jsonObject.getInt("sharedBy"), jsonObject.getInt("docOwner"), jsonObject.getInt("sharedTo"), jsonObject.getString("sharedDate"));
                    DocumentsDetailsWithIdsArraylist.add(documentsDetailsWithIds);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            documentsAdapter = new VideocallAdapter(UploadedDocumentsActivity.this, DocumentDetailsArray);
                            LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(UploadedDocumentsActivity.this);
                            recycler_view.setLayoutManager(recylerViewLayoutManager);
                            recycler_view.setHasFixedSize(true);
                            recycler_view.setAdapter(documentsAdapter);
                            progressDialog.dismiss();
                        } catch (Exception e) {

                        }
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "data not available...", Toast.LENGTH_SHORT).show();
                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("resultJson", resultJson);
    }

    public void getDocumentDetailsById(String strurl, String jsonString, final Context context) throws Exception {

        strurl = strurl.replace(" ", "%20");
        Log.e("strurl", strurl);
        HttpGet httpPost = new HttpGet(strurl);
     /*   if (jsonString != null)
        {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }*/
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
        try {
           /* JSONArray jsonArray=new JSONArray(resultJson);
            for (int i=0;i<=jsonArray.length()-1;i++)
            {*/

            JSONObject jsonObject = new JSONObject(resultJson);
            DocumentDetails documentsDetailsWithIds = new DocumentDetails(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("docType"), jsonObject.getString("uploadedDate"), DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("uploadedTo")).toString());
            DocumentDetailsArray.add(documentsDetailsWithIds);
            //  }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("resultJson", resultJson);
    }

}
