package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCallsActivity extends Activity {
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
    HashMap userTypeDetailss=new HashMap();
    de.hdodenhof.circleimageview.CircleImageView call,notification,logout;
    VideocallAdapter videoCallAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calls);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        call= (CircleImageView) toolbar.findViewById(R.id.videocall);
        notification= (CircleImageView) toolbar.findViewById(R.id.show_notifications);
        call.setVisibility(View.GONE);
        notification.setVisibility(View.GONE);


        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        callsList= (RecyclerView) findViewById(R.id.myCallList);
        callsList.setNestedScrollingEnabled(false);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        title= (TextView) toolbar.findViewById(R.id.screen_title);
        title.setText("VideoCallsList");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallName.add("videoCall_12_12_16");
        videocallName.add("videoCall_13_12_16");
        videocallName.add("videoCall_14_12_16");
        videocallName.add("videoCall_12_12_16");
        videocallName.add("videoCall_13_12_16");
        videocallName.add("videoCall_14_12_16");
        videocallName.add("videoCall_12_12_16");
        videocallName.add("videoCall_13_12_16");
        videocallName.add("videoCall_14_12_16");

        videocallDate.add("12-Dec-2016");
        videocallDate.add("13-Dec-2016");
        videocallDate.add("14-Dec-2016");

        videocallDate.add("12-Dec-2016");
        videocallDate.add("13-Dec-2016");
        videocallDate.add("14-Dec-2016");


        videocallDate.add("12-Dec-2016");
        videocallDate.add("13-Dec-2016");
        videocallDate.add("14-Dec-2016");
        videoCallAdapter = new VideocallAdapter(this, videocallerName,videocallName,videocallDate);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(this);
        callsList.setLayoutManager(recylerViewLayoutManager);
        callsList.setHasFixedSize(true);
        callsList.setAdapter(videoCallAdapter);

        if(new NetworkCheck().isOnline(VideoCallsActivity.this)) {
          //  new getDetailsById().execute();

        }
        else
        {
            Toast.makeText(VideoCallsActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }
    }
    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder>{

        ArrayList callerName,videoName,callDate;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName,msg,sentDate;

        public VideocallAdapter(Context context1,ArrayList callerName,ArrayList videoName,ArrayList callDate){

            this.callerName=callerName;
            this.videoName=videoName;
            this.callDate=callDate;
            context = context1;
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public  TextView callerName,videoName,callDate;

            public ViewHolder(View v){

                super(v);

                callerName = (TextView)v.findViewById(R.id.callerName);
                videoName = (TextView)v.findViewById(R.id.videoName);
                callDate = (TextView)v.findViewById(R.id.callDate);
            }
        }

        @Override
        public VideocallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            view1 = LayoutInflater.from(context).inflate(R.layout.videos_custom_layout,parent,false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){

            holder.callerName.setText(videocallerName.get(position).toString());
            holder.videoName.setText(videocallName.get(position).toString());
            holder.callDate.setText(videocallDate.get(position).toString());
        }

        @Override
        public int getItemCount(){

            return videocallDate.size();
        }
    }
}
