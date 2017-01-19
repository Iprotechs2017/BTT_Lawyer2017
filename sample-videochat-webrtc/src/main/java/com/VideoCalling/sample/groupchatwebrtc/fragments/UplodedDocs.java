package com.VideoCalling.sample.groupchatwebrtc.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.DashBoardActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentDetails;
import com.VideoCalling.sample.groupchatwebrtc.utils.DocumentsDetailsWithIds;
import com.VideoCalling.sample.groupchatwebrtc.utils.ShareFile;
import com.VideoCalling.sample.groupchatwebrtc.utils.fileDownload;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Harishma Velagala on 01-01-2017.
 */
public class UplodedDocs  extends Fragment {

    RecyclerView recycler_view;
    ArrayList uploaded_doc_names=new ArrayList();
    ArrayList uploaded_doc_dates=new ArrayList();
    ArrayList uploaded_doc_sender=new ArrayList();
    VideocallAdapter   documentsAdapter;
    SharedPreferences prefs;
    int userId;
    VideocallAdapter videoCallAdapter;
    ArrayList<DocumentsDetailsWithIds> DocumentsDetailsWithIdsArraylist=new ArrayList<DocumentsDetailsWithIds>();
    ArrayList<DocumentDetails> DocumentDetailsArray=new ArrayList<DocumentDetails>();
    int documentId;
    int userId1;
    ArrayList videocallerName = new ArrayList();
    ArrayList videocallName = new ArrayList();
    ArrayList videocallDate = new ArrayList();
    String limitornot;
    Typeface typeface;

ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        limitornot = getArguments().getString("yes");
        userId1=Integer.parseInt(getArguments().getString("userId"));
        View view= inflater.inflate(R.layout.docs_custom_tabs_layout, container, false);
        progressDialog=new ProgressDialog(getActivity());
        if(limitornot.equalsIgnoreCase("full")) {
            progressDialog.setCancelable(false);
        }

        recycler_view= (RecyclerView) view.findViewById(R.id.recycler_view);
        prefs = getActivity().getSharedPreferences("loginDetails", getActivity().MODE_PRIVATE);
        if (prefs.getInt("userType", -1) == 0)
        {
            userId=prefs.getInt("userId", -1);
        }
        else
        {
if(userId1==0)
{
    userId=DashBoardActivity.selectedImmigrantId;
}
            else
{
    userId=userId1;
}

        }
            typeface= Typeface.createFromAsset(getActivity().getAssets(), "QuicksandRegular.ttf");
        new getSharedDocs().execute();
        return  view;
    }
    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder>{

        Context context;
        View view1;
        ViewHolder viewHolder1;
        ArrayList<DocumentDetails> documentsList;
        public VideocallAdapter(Context context1,ArrayList<DocumentDetails> documentsList)
        {
            this.documentsList=documentsList;
            context = context1;
        }



        public  class ViewHolder extends RecyclerView.ViewHolder{

            public  TextView callerName,videoName,callDate;
            public ImageView contactImage,download_video,share_video;
            public LinearLayout parent;
            public ViewHolder(View v){

                super(v);
                contactImage= (ImageView) v.findViewById(R.id.contactImage);
                download_video=(ImageView) v.findViewById(R.id.download_video);
                callerName = (TextView)v.findViewById(R.id.callerName);
                videoName = (TextView)v.findViewById(R.id.videoName);
                callDate = (TextView)v.findViewById(R.id.callDate);
                share_video=(ImageView) v.findViewById(R.id.share_video);
                parent= (LinearLayout) v.findViewById(R.id.parent);
            }
        }

        @Override
        public VideocallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            view1 = LayoutInflater.from(context).inflate(R.layout.videos_custom_layout,parent,false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position){
            final DocumentDetails documentDetails = documentsList.get(position);
            String docName = documentDetails.getName().toString();
            holder.contactImage.setImageResource(R.drawable.pdf_icon);
            holder.callerName.setText(documentDetails.getUploadedTo() + "");
            holder.videoName.setText(documentDetails.getName().toString());
            holder.callDate.setText(documentDetails.getUploadedDate().toString());
            holder.callerName.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);
            holder.share_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ShareFile(getActivity(), DocumentsDetailsWithIdsArraylist, position);
                }
            });
            holder.download_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Ok", Toast.LENGTH_SHORT).show();
                    new fileDownload(getActivity(),DocumentDetailsArray,position);
                }
            });
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/BTTLawyer/" + documentDetails.getName().toString()+ "." + documentDetails.getDocType().toString().replace(".",""));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (file.exists()) {
                        try {
                            Uri uri_path = Uri.fromFile(file);
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                                    (MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setType(mimeType);
                            intent.setDataAndType(uri_path, mimeType);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, documentDetails.getDocType().toString() + " file supported app not installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        new fileDownload(getActivity(),DocumentDetailsArray,position);

                    }
                }
            });
        }

        @Override
        public int getItemCount(){

            return documentsList.size();
        }
    }

    private class getSharedDocs extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {


            super.onPreExecute();

            progressDialog.setMessage("loading...");
            progressDialog.show();
        }
        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            String url;
            try
            {
                JSONObject jsonObject=new JSONObject();
                if(limitornot.equalsIgnoreCase("full"))
                {
                    url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/get/sharedBy/"+userId+"/sharedTo/"+Integer.parseInt(DashBoardActivity.solicitor.get("id").toString());
                }
                else
                {
                    url="http://35.163.24.72:8080/VedioApp/service/DocumentShare/getlimit/sharedBy/"+userId+"/sharedTo/"+Integer.parseInt(DashBoardActivity.solicitor.get("id").toString())+"/limit/3";
                }

                Log.e("url", url);
                try {
                    getUploadedDocsIds(url, jsonObject.toString(), getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {

            }

            return aLong;
        }
        protected void onPostExecute(Long result) {
            progressDialog.dismiss();
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
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                String resultJson = "";
                while ((line = reader.readLine()) != null) {
                    resultJson += line;
                }
                Log.e("responseeee", resultJson);
                Log.e(" documents statusCode", response.getStatusLine().getStatusCode()+"---");

                JSONArray jsonArray = new JSONArray(resultJson);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        documentId = jsonObject.getInt("documentId");
                        String url = "http://35.163.24.72:8080/VedioApp/service/document/getdocumentdata/id/" + documentId;
                        try {
                            getDocumentDetailsById(url, jsonObject.toString(), getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DocumentsDetailsWithIds documentsDetailsWithIds = new DocumentsDetailsWithIds(jsonObject.getInt("id"), jsonObject.getInt("documentId"), jsonObject.getInt("sharedBy"), jsonObject.getInt("docOwner"), jsonObject.getInt("sharedTo"), jsonObject.getString("sharedDate"));
                        DocumentsDetailsWithIdsArraylist.add(documentsDetailsWithIds);

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(limitornot.equalsIgnoreCase("full")) {
                                    Collections.reverse(DocumentDetailsArray);
                                }
                                    documentsAdapter = new VideocallAdapter(getActivity(), DocumentDetailsArray);
                                LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(getActivity());
                                recycler_view.setLayoutManager(recylerViewLayoutManager);
                                recycler_view.setHasFixedSize(true);
                                recycler_view.setAdapter(documentsAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "data not available...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else if(response.getStatusLine().getStatusCode()==204)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Documents not available...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (response.getStatusLine().getStatusCode() == 500) {
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "server is busy try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        //    Log.e("resultJson", resultJson);
        }
    }
    public void getDocumentDetailsById(String strurl, String jsonString, final Context context) throws Exception
    {

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
        if (response.getStatusLine().getStatusCode() == 200) {
        try {
           /* JSONArray jsonArray=new JSONArray(resultJson);
            for (int i=0;i<=jsonArray.length()-1;i++)
            {*/

            JSONObject jsonObject=new JSONObject(resultJson);
            DocumentDetails documentsDetailsWithIds=new DocumentDetails(jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("docType"),jsonObject.getString("uploadedDate"), DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("uploadedTo")).toString());
            DocumentDetailsArray.add(documentsDetailsWithIds);
            //  }

        } catch (Exception e)
        {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        } else if (response.getStatusLine().getStatusCode() == 500) {
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "server is busy try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            Log.e("resultJson", resultJson);
        }
    }

}
