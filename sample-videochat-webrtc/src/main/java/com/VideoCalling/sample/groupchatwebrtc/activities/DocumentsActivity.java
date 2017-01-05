package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentDetails;
import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentsDetailsWithIds;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.util.AppController;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MediaClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.UsersUtils;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends BaseActivity {
RecyclerView recycler_view;
    DocumentsAdapter documentsAdapter;
    ImageView logout,makeCall,show_notifications;
    TextView screen_title;
    Toolbar toolbar;
    private QBUser currentUser;
    int selectedPosition;
    int documentId;
    int selectedDocument;
    SharedPreferences prefs;
    int ownerId;
    String type;
    String fileName;
    ProgressDialog progressDialogCommon;
    String downlodeDocName;
    File openfile;
    //ArrayList<MediaClass> resonse=new ArrayList<MediaClass>();
    ArrayList<DocumentDetails> DocumentDetailsArray=new ArrayList<DocumentDetails>();
    ArrayList<DocumentsDetailsWithIds> DocumentsDetailsWithIdsArraylist=new ArrayList<DocumentsDetailsWithIds>();
    ArrayList baristerQB=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docements);
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        screen_title= (TextView) findViewById(R.id.screen_title);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        screen_title.setText("Shared Documents");
        progressDialogCommon=new ProgressDialog(this);
        progressDialogCommon.setMessage("loading...");
        progressDialogCommon.setCancelable(false);
        currentUser = sharedPrefsHelper.getQbUser();
        logout= (ImageView) findViewById(R.id.logout);
        makeCall= (ImageView) findViewById(R.id.videocall);
        show_notifications = (ImageView) findViewById(R.id.show_notifications);
        show_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentsActivity.this, NotificationActivity.class));
            }
        });
        makeCall.setVisibility(View.GONE);
        ownerId=Integer.parseInt(getIntent().getExtras().getString("id"));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DocumentsActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               new LogoutClass().clearSesson(DocumentsActivity.this);
                DocumentsActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setCancelable(false)
                        .show();


            }
        });
        //new DownloadFileFromURL().execute();
       new getSharedDocs().execute();
       /* for (int i=0;i<=20;i++)
        {
            resonse.add("DoCument Name"+i);
        }
        recycler_view= (RecyclerView) findViewById(R.id.recycler_view);
        documentsAdapter=new DocumentsAdapter(resonse);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(documentsAdapter);
        documentsAdapter.notifyDataSetChanged();
*/
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.MyViewHolder> {

        public ArrayList<DocumentDetails> documentsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView doc_name;
            public ImageView viewDoc ,shareDoc,downlodeDoc;

            public MyViewHolder(View view) {
                super(view);
                doc_name = (TextView) view.findViewById(R.id.doc_name);
                viewDoc= (ImageView) view.findViewById(R.id.viewDoc);
                shareDoc= (ImageView) view.findViewById(R.id.shareDoc);
                downlodeDoc= (ImageView) view.findViewById(R.id.downlodeDoc);
            }
        }


        public DocumentsAdapter(ArrayList<DocumentDetails> documentsList) {
            this.documentsList = documentsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.doucments_custom_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
            final DocumentDetails documentDetails = documentsList.get(position);
            String docName = documentDetails.getName().toString();
            holder.doc_name.setText(docName);
                Picasso.with(DocumentsActivity.this).load(R.drawable.downlode).into(holder.downlodeDoc);
               Picasso.with(DocumentsActivity.this).load(R.drawable.share_icon).into(holder.shareDoc);
           /*
            if(documentDetails.getDocType().equalsIgnoreCase(".mp4"))
            {
               // holder.viewDoc.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.viewDoc.setVisibility(View.GONE);
            }*/
                holder.downlodeDoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedDocument = position;
                        new DownloadFileFromURL().execute();
                    }
                });

          /*  holder.viewDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
startActivity(new Intent(DocumentsActivity.this,VideoViewActivity.class).putExtra("videoPath","http://192.168.0.12/AndroidFileUpload/uploads/Jeena Jeena Flute Tutorial _ Badlapur _ by Harsh Dave.mp4"));
                    ///share to barister
                    Toast.makeText(DocumentsActivity.this, "View Documents  Button", Toast.LENGTH_SHORT).show();
                }
            });
*/
            if(prefs.getInt("userType",0)!=2)
            {
                holder.shareDoc.setVisibility(View.VISIBLE);
            }
            else {
                holder.shareDoc.setVisibility(View.GONE);
            }
            holder.shareDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition=position;//by using this we can get selected person object after api implementation
                    if(new NetworkCheck().isOnline(DocumentsActivity.this)) {
                        new  ShareDoc().execute();
                    }
                    else
                    {
                        Toast.makeText(DocumentsActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            }
            catch (Exception e)
            {

            }
        }
        @Override
        public int getItemCount() {
            return documentsList.size();
        }
    }


    private void startLogoutCommand() {
        CallService.logout(this);
    }

    private void unsubscribeFromPushes() {
        if (googlePlayServicesHelper.checkPlayServicesAvailable(this)) {

            googlePlayServicesHelper.unregisterFromGcm(Consts.GCM_SENDER_ID);
        }
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }

    private void startLoginActivity() {
        LoginActivity.start(this);
        finish();
    }
    public void postAPICall(String strurl, String jsonString, final Context context) throws Exception
    {

        strurl = strurl.replace(" ", "%20");

        HttpGet httpPost = new HttpGet(strurl);
     /*   if (jsonString != null)
        {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }*/
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
        Log.e("responseeee",resultJson);
        try {
            JSONArray jsonArray=new JSONArray(resultJson);
            if(jsonArray.length()>0)
            {
                for (int i=0;i<=jsonArray.length()-1;i++)
                {

                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    documentId=jsonObject.getInt("documentId");
                    String url="http://35.163.24.72:8080/VedioApp/service/document/getdocumentdata/id/"+documentId;
                    try {
                        postAPICall2(url, jsonObject.toString(), DocumentsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DocumentsDetailsWithIds documentsDetailsWithIds=new DocumentsDetailsWithIds(jsonObject.getInt("id"),jsonObject.getInt("documentId"),jsonObject.getInt("sharedBy"),jsonObject.getInt("docOwner"),jsonObject.getInt("sharedTo"),jsonObject.getString("sharedDate"));
                    DocumentsDetailsWithIdsArraylist.add(documentsDetailsWithIds);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
                            documentsAdapter = new DocumentsAdapter(DocumentDetailsArray);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recycler_view.setLayoutManager(mLayoutManager);
                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                            recycler_view.setAdapter(documentsAdapter);

                            documentsAdapter.notifyDataSetChanged();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                });

            }
else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "data not available...", Toast.LENGTH_SHORT).show();
                    }
                });

            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("resultJson",resultJson);
    }
    public void postAPICall1(String strurl, String jsonString, final Context context) throws Exception
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
        try {
            JSONObject jsonObject=new JSONObject(resultJson);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "File Successfully Shared...!", Toast.LENGTH_SHORT).show();
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("resultJson",resultJson);
    }
    private class getSharedDocs extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogCommon.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            String url;
            JSONObject jsonObject=new JSONObject();
            Log.e("err",OpponentsActivity.userTypeDetails.toString());
            if (prefs.getInt("userType", 0) == 1) {

              //  url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/1/sharedTo/2/docOwner/0";
                url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/"+ownerId+"/sharedTo/"+prefs.getInt("userId",0);
             } else {
               // url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/1/sharedTo/2/docOwner/0";
                url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/"+OpponentsActivity.userTypeDetails.get("id")+"/sharedTo/"+prefs.getInt("userId",0)+"/docOwner/"+ownerId;
             }
Log.e("url",url);
            try {
                postAPICall(url, jsonObject.toString(), DocumentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
            progressDialogCommon.dismiss();

        }
    }

    private class ShareDoc extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(DocumentsActivity.this);
            progressDialog.setMessage("loading...");
          //  progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);

            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("documentId",DocumentsDetailsWithIdsArraylist.get(selectedPosition).getDocumentId());
                jsonObject.put("sharedBy",prefs.getInt("userId",0));
                jsonObject.put("docOwner",ownerId);
                jsonObject.put("sharedDate", DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                jsonObject.put("sharedTo",OpponentsActivity.userTypeDetails.get("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
       /*     String url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/"+prefs.getInt("userId",0)+"/sharedTo/"+OpponentsActivity.userType.get(0).getId()+"/docOwner/"+ownerId+"/docId/"+1+"";
            Log.e("url",url);*/
            Log.e("jsonObject",jsonObject.toString());
            try {
                postAPICall1("http://35.163.24.72:8080/VedioApp/service/DocumentShare", jsonObject.toString(), DocumentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
           // progressDialog.dismiss();

        }
    }
    private class downlodeDocument extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(DocumentsActivity.this);
            progressDialog.setMessage("loading...");
            progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject jsonObject=new JSONObject();
String url="http://35.163.24.72:8080/VedioApp/service/downlaod/download/name/"+downlodeDocName;
            try {
                postAPICall3(url, jsonObject.toString(), DocumentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
            progressDialog.dismiss();

        }
    }
    public void postAPICall2(String strurl, String jsonString, final Context context) throws Exception
    {

        strurl = strurl.replace(" ", "%20");
Log.e("strurl",strurl);
        HttpGet httpPost = new HttpGet(strurl);
     /*   if (jsonString != null)
        {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }*/
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
        try {
           /* JSONArray jsonArray=new JSONArray(resultJson);
            for (int i=0;i<=jsonArray.length()-1;i++)
            {*/

                JSONObject jsonObject=new JSONObject(resultJson);
                DocumentDetails documentsDetailsWithIds=new DocumentDetails(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("docType"),jsonObject.getString("uploadedDate"),jsonObject.getInt("uploadedTo"));
                DocumentDetailsArray.add(documentsDetailsWithIds);
          //  }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("resultJson",resultJson);
    }
    public void postAPICall3(String strurl, String jsonString, final Context context) throws Exception
    {

        strurl = strurl.replace(" ", "%20");

        HttpGet httpPost = new HttpGet(strurl);
     /*   if (jsonString != null)
        {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }*/
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");


        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient( context );
        response = httpClient.execute(httpPost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        /*String line = null;
        String resultJson = "";
        while ((line = reader.readLine()) != null)
        {
            resultJson += line;
        }
        try {
           *//* JSONArray jsonArray=new JSONArray(resultJson);
            for (int i=0;i<=jsonArray.length()-1;i++)
            {*//*

            JSONObject jsonObject=new JSONObject(resultJson);
            DocumentDetails documentsDetailsWithIds=new DocumentDetails(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("docType"),jsonObject.getString("uploadedDate"));
            DocumentDetailsArray.add(documentsDetailsWithIds);
            //  }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }*/

        //Log.e("resultJson",resultJson);
    }
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
ProgressDialog progressDialog;
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(DocumentsActivity.this);
            progressDialog.setMessage("Downloading...");
            progressDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            String type=null;
            int count;
            try {
                type=DocumentDetailsArray.get(selectedDocument).getDocType();
                downlodeDocName="http://35.163.24.72:8080/VedioApp/service/downlaod/download123/"+DocumentDetailsArray.get(selectedDocument).getId();
               // downlodeDocName="http://192.168.0.2:8080/VedioApp/service/downlaod/download/name/"+DocumentDetailsArray.get(selectedDocument).getId();
                Log.e("downlodeDocName",downlodeDocName);
                URL url = new URL(downlodeDocName);

                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "VideoCalling");
                if(!myDirectory.exists()) {
                    myDirectory.mkdirs();
                }

                 fileName=DocumentDetailsArray.get(selectedDocument).getName();
                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/VideoCalling/"+fileName+"."+DocumentDetailsArray.get(selectedDocument).getDocType());
              openfile=  new File(Environment
                        .getExternalStorageDirectory().toString()
                        + "/VideoCalling/"+fileName+"."+DocumentDetailsArray.get(selectedDocument).getDocType());
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();
                type="downloaded";
                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                type=" not downloaded";
                Log.e("Error: ", e.getMessage());
            }

            return type;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String  downloaded) {
            // dismiss the dialog after the file was downloaded
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            if(downloaded.equalsIgnoreCase("downloaded"))
            {
                new AlertDialog.Builder(DocumentsActivity.this)
                        .setMessage("File Downloaded Successfully...")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {



                            }
                        })
                        .setNegativeButton("Open", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri_path = Uri.fromFile(openfile);
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                                        (MimeTypeMap.getFileExtensionFromUrl(openfile.getAbsolutePath()));


                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                intent.setType(mimeType);
                                intent.setDataAndType(uri_path, mimeType);
                                startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
            else
            {
                new AlertDialog.Builder(DocumentsActivity.this)
                        .setMessage("Downloading failed...")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        }

    }
    public void openFolder(String path)
    {
    }
}
