package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.utils.VideoLogsModel;

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
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallsActivity extends AppCompatActivity {
    RecyclerView callsList,notificationsList;
    ProgressDialog progressDialog;
    TextView title;
    SharedPreferences prefs;
    ArrayList msgs=new ArrayList();
    ArrayList notificationSenderNames=new ArrayList();
    ArrayList notificationSentDates=new ArrayList();
    ArrayList videocallerName=new ArrayList();
    ArrayList videocallName=new ArrayList();
    ArrayList videocallDate=new ArrayList();
    Typeface typeface;
    HashMap userTypeDetailss=new HashMap();
    ArrayList<VideoLogsModel> videoLogs=new ArrayList<VideoLogsModel>();
    de.hdodenhof.circleimageview.CircleImageView call,notification,logout;
    VideocallAdapter videoCallAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calls);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(true);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Video calls");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        typeface = Typeface.createFromAsset(getAssets(), "QuicksandRegular.ttf");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_w));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoCallsActivity.this.finish();
            }
        });
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        if (prefs.getInt("userType", -1) == 0)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_notifi_color);
        }
        else if(prefs.getInt("userType", -1) == 1)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
            changeTheam(R.color.solicitor_notifi_color);
        }
        else if(prefs.getInt("userType", -1) == 2)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_notifi_color);
        }
        callsList= (RecyclerView) findViewById(R.id.myCallList);
        callsList.setNestedScrollingEnabled(false);
        if(new NetworkCheck().isOnline(VideoCallsActivity.this)) {
           new VideoCallLogs().execute();

        }
        else
        {
            Toast.makeText(VideoCallsActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }


    private class VideoCallLogs extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);


            try {
                if (prefs.getInt("userType", -1) != 0) {
                    getVideoLogs("http://35.163.24.72:8080/VedioApp/service/VideoLog/getLog/callFrom/" + Integer.parseInt(DashBoardActivity.solicitor.get("id").toString()) + "/callTo/" + DashBoardActivity.selectedImmigrantId, VideoCallsActivity.this);
                }
                else
                {
                    getVideoLogs("http://35.163.24.72:8080/VedioApp/service/VideoLog/getLog/callFrom/" + Integer.parseInt(DashBoardActivity.solicitor.get("id").toString()) + "/callTo/" + prefs.getInt("userId",-1), VideoCallsActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
            progressDialog.dismiss();
        }
    }
    public void getVideoLogs(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        Log.e("strurl", strurl);
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        Log.e("response",response.getStatusLine().getStatusCode()+"--");
        if (response.getStatusLine().getStatusCode() == 200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("-----video logs", resultJson);
            Log.e("userTypeDetailss",userTypeDetailss.toString());
            if (resultJson != null) {
                try {
String name;
                    JSONArray jsonArray=new JSONArray(resultJson);
                    videoLogs.clear();
                    for (int i=0;i<=jsonArray.length()-1;i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                       /* if(jsonObject.getInt("callFrom")==prefs.getInt("userId",-1))
                        {
                            name="Me";

                        }
                        else
                        {
                            name=DashBoardActivity.solicitor.get("name").toString();
                        }*/
                        if(jsonObject.getString("duration").split(":")[0].equalsIgnoreCase("12")) {
                            VideoLogsModel videoLogsModel = new VideoLogsModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("callFrom"),
                                    DashBoardActivity.solicitor.get("name").toString(),
                                    jsonObject.getString("startTime"),
                                    jsonObject.getString("day"),
                                    DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("callTo1")).toString(),
                                    DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("callTo2")).toString(),
                                    jsonObject.getInt("callTo1"),
                                    jsonObject.getInt("callTo2"),
                                    jsonObject.getString("endTime"),
                                    "00:"+jsonObject.getString("duration").split(":")[1].toString()+":"+jsonObject.getString("duration").split(":")[2].toString()
                            );
                            videoLogs.add(videoLogsModel);
                        }
                        else
                        {
                            VideoLogsModel videoLogsModel = new VideoLogsModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("callFrom"),
                                    DashBoardActivity.solicitor.get("name").toString(),
                                    jsonObject.getString("startTime"),
                                    jsonObject.getString("day"),
                                    DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("callTo1")).toString(),
                                    DashBoardActivity.userTypeDetailss.get(jsonObject.getInt("callTo2")).toString(),
                                    jsonObject.getInt("callTo1"),
                                    jsonObject.getInt("callTo2"),
                                    jsonObject.getString("endTime"),
                                    jsonObject.getString("duration")
                            );
                            videoLogs.add(videoLogsModel);
                        }

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Collections.reverse(videoLogs);
                            videoCallAdapter = new VideocallAdapter(VideoCallsActivity.this, videoLogs);
                            LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(VideoCallsActivity.this);
                            callsList.setLayoutManager(recylerViewLayoutManager);
                            callsList.setHasFixedSize(true);
                            callsList.setAdapter(videoCallAdapter);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                }
            }
            else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Unable to get valid data from server try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else if(response.getStatusLine().getStatusCode()==204)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Toast.makeText(VideoCallsActivity.this, "Video call logs not available...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Log.e("result", resultJson);
    }
    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder> {

        ArrayList callerName, videoName, callDate;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName, msg, sentDate;
        ArrayList<VideoLogsModel> videoLogs;
        public VideocallAdapter(Context context1, ArrayList<VideoLogsModel> videoLogs) {

            this.videoLogs=videoLogs;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView callerName, videoName, callDate;
            LinearLayout share_and_down,parent;
           ImageView ic_img;
            public ViewHolder(View v) {

                super(v);
                ic_img= (ImageView) v.findViewById(R.id.ic_img);
                callerName = (TextView) v.findViewById(R.id.callerName);
                videoName = (TextView) v.findViewById(R.id.videoName);
                callDate = (TextView) v.findViewById(R.id.callDate);
                share_and_down= (LinearLayout) v.findViewById(R.id.share_and_down);
                parent= (LinearLayout) v.findViewById(R.id.parent);
            }
        }

        @Override
        public VideocallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view1 = LayoutInflater.from(context).inflate(R.layout.videos_custom_layout, parent, false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (videoLogs.size() > 0&& videoLogs.size()>position) {
                if (videoLogs.get(position).getCallTo2Name().equalsIgnoreCase(videoLogs.get(position).getCallTo1Name())) {
                    holder.videoName.setText(videoLogs.get(position).getCallFromName() + "&" + videoLogs.get(position).getCallTo2Name());
                } else {
                    holder.videoName.setText(videoLogs.get(position).getCallFromName() + "," + videoLogs.get(position).getCallTo2Name() + "&" + videoLogs.get(position).getCallTo2Name());
                }
            }
            holder.callDate.setText(videoLogs.get(position).getDay().toString());
            holder.share_and_down.setVisibility(View.GONE);
            holder.callerName.setVisibility(View.GONE);
            holder.callerName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(VideoCallsActivity.this, IndividualVideocallActivity.class).putExtra("position", position));
                }
            });
        }

        @Override
        public int getItemCount() {
            int size = 0;
            if (videoLogs.size() < 3&&videoLogs.size() != 0) {
                size = videoLogs.size();
            }
            else if(videoLogs.size() > 3)
            {
                size = 3;
            }
            else if(videoLogs.size() == 0)
            {
                size = 0;
            }
            return size;


        }
    }
}
