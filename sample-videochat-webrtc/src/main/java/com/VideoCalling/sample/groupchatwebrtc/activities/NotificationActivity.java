package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {
RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList msgs=new ArrayList();
    ArrayList senderNames=new ArrayList();
    ArrayList sentDates=new ArrayList();
    ProgressDialog progressDialog;
    SharedPreferences prefs;
    LinearLayout action_layout;
    HashMap userTypeDetailss=new HashMap();
    TextView title;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView call,notification,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        call= (CircleImageView) toolbar.findViewById(R.id.videocall);
        logout= (CircleImageView) toolbar.findViewById(R.id.logout);
        notification= (CircleImageView) toolbar.findViewById(R.id.show_notifications);
        call.setVisibility(View.GONE);
        notification.setVisibility(View.GONE);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");

        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);


        title= (TextView) toolbar.findViewById(R.id.screen_title);
        title.setText("Notifications");
        action_layout= (LinearLayout) toolbar.findViewById(R.id.action_layout);
        //action_layout.setVisibility(View.INVISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        if(new NetworkCheck().isOnline(NotificationActivity.this)) {

            new getDetailsById().execute();

            LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(this);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(NotificationActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to Logout?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new LogoutClass().clearSesson(NotificationActivity.this);
                                    NotificationActivity.this.finish();
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
            recyclerView.setLayoutManager(recylerViewLayoutManager);
/*
            recyclerView.setLayoutManager(recylerViewLayoutManager);

            recyclerViewAdapter = new RecyclerViewAdapter(this, msgs,senderNames,sentDates);

            recyclerView.setAdapter(recyclerViewAdapter);*/

        }
        else
        {
            Toast.makeText(NotificationActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }


        if (prefs.getInt("userType", -1) == 1) {
            notification.setVisibility(View.VISIBLE);
        }
        else
        {
            notification.setVisibility(View.GONE);
        }


    }
     class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        ArrayList msgs,senderNames,sentDates;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName,msg,sentDate;

        public RecyclerViewAdapter(Context context1,ArrayList msgs,ArrayList senderNames,ArrayList sentDates){

            this.msgs=msgs;
            this.senderNames=senderNames;
            this.sentDates=sentDates;
            context = context1;
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public  TextView senderName,msg,sentDate;

            public ViewHolder(View v){

                super(v);

                senderName = (TextView)v.findViewById(R.id.senderName);
                msg = (TextView)v.findViewById(R.id.msg);
                sentDate = (TextView)v.findViewById(R.id.sentDate);
            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            view1 = LayoutInflater.from(context).inflate(R.layout.notification_custom,parent,false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){

            holder.msg.setText(msgs.get(position).toString());
            holder.sentDate.setText(sentDates.get(position).toString());
             holder.senderName.setText(senderNames.get(position).toString());
            }

        @Override
        public int getItemCount(){

            return msgs.size();
        }
    }
    public void postAPICall(String strurl, final Context context) throws Exception
    {
        strurl = strurl.replace(" ", "%20");
        Log.e("strurl",strurl);
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient( context );
        response = httpClient.execute(httpPost);
        if(response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).toString());
                    msgs.add(jsonObject.getString("notification"));
                    //   senderNames.add(OpponentsActivity.OpponentNames.get(OpponentsActivity.OpponentIds.indexOf(jsonObject.getString("sentBy"))));
                    senderNames.add(userTypeDetailss.get(Integer.parseInt(jsonObject.getString("sentBy"))));
                    sentDates.add(jsonObject.getString("sentDate"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();


                        recyclerViewAdapter = new RecyclerViewAdapter(NotificationActivity.this, msgs, senderNames, sentDates);

                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                });
            } catch (Exception e) {
            }
        }
        else  if(response.getStatusLine().getStatusCode()==500)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Log.e("result", resultJson);
    }
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
                   Long aLong= Long.valueOf(1);


            try {
              //  postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
                postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
                        return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
    public void postAPICall1(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        if(response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("resultJson", resultJson);
            try {

                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    userTypeDetailss.put(jsonObject.getInt("id"), jsonObject.getString("name"));

                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //       Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else  if(response.getStatusLine().getStatusCode()==500)
        {
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
    }
});
        }

    //    Log.e("resultJson",userTypeDetailss.toString());

    }
    private class getDetailsById extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            String type;

            if (prefs.getInt("userType", 0) == 1) {
                type = "2";
            } else {
                type = "1";
            }
            userTypeDetailss.clear();
            for(int i=0;i<=2;i++) {
                String url = "http://35.163.24.72:8080/VedioApp/service/user/type/"+i;
                Log.e("urlls", url);
                try {
                    postAPICall1(url, NotificationActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new DownloadFilesTask().execute();
                }
            });
            return aLong;
        }


        protected void onPostExecute(Long result) {


        }
    }
}
