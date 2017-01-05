package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;

import com.VideoCalling.sample.groupchatwebrtc.adapters.TabsAdaptor;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.mancj.slideup.SlideUp;

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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
RecyclerView callsList,notificationsList;
    ProgressDialog progressDialog;
Dialog showMessage;
    SharedPreferences prefs;
    ArrayList msgs=new ArrayList();
    ArrayList notificationSenderNames=new ArrayList();
    ArrayList notificationSentDates=new ArrayList();
    ArrayList videocallerName=new ArrayList();
    ArrayList videocallName=new ArrayList();
    ArrayList videocallDate=new ArrayList();
    HashMap userTypeDetailss=new HashMap();
    RecyclerViewAdapter recyclerViewAdapter;
   VideocallAdapter videoCallAdapter;
    ViewPager mViewPager;
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
   // TextView show_more_notification,show_more_video_cals;
    TextView title,senderName1,msg1,sentDate1;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView call,notification,logout;
    Typeface typeface;
    RelativeLayout rel;
    View slideView;
    SlideUp slideUp;
    CardView card2;
    Animation slide_down;
    ImageView show_more_uploaded_docs,show_more_video_cals,show_more_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading...");
         slideView = findViewById(R.id.slideView);
        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();
     //   rel= (RelativeLayout) findViewById(R.id.rel);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        card2= (CardView) findViewById(R.id.card2);
        call= (CircleImageView) toolbar.findViewById(R.id.videocall);
        logout= (CircleImageView) toolbar.findViewById(R.id.logout);
        notification= (CircleImageView) toolbar.findViewById(R.id.show_notifications);
        show_more_uploaded_docs= (ImageView) findViewById(R.id.show_more_uploaded_docs);
        show_more_uploaded_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      startActivity(new Intent(DashBoardActivity.this,UploadedDocumentsActivity.class));
                slideUp.animateIn();
            }
        });
        call.setVisibility(View.GONE);
        typeface= Typeface.createFromAsset(getAssets(), "QuicksandRegular.ttf");

        notification.setVisibility(View.GONE);
        title= (TextView) toolbar.findViewById(R.id.screen_title);
        showMessage=new Dialog(this);
        showMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showMessage.setContentView(R.layout.dialog_notification_custom_layout);
        senderName1 = (TextView)showMessage.findViewById(R.id.senderName);
        msg1 = (TextView)showMessage.findViewById(R.id.msg);
        sentDate1 = (TextView)showMessage.findViewById(R.id.sentDate);

        show_more_notification= (ImageView) findViewById(R.id.show_more_notification);
        show_more_video_cals= (ImageView) findViewById(R.id.show_more_video_cals);
        show_more_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this,NotificationActivity.class));
            }
        });
        show_more_video_cals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this,VideoCallsActivity.class));
            }
        });
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        title.setText(prefs.getString("name","no")+" Dashboard");


                callsList= (RecyclerView) findViewById(R.id.myCallList);
        notificationsList= (RecyclerView) findViewById(R.id.notifcationsList);
        callsList.setNestedScrollingEnabled(false);
        notificationsList.setNestedScrollingEnabled(false);
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallerName.add("Solicitor");
        videocallName.add("videoCall_12_12_16");
        videocallName.add("videoCall_13_12_16");
        videocallName.add("videoCall_14_12_16");

        videocallDate.add("12-Dec-2016");
        videocallDate.add("13-Dec-2016");
        videocallDate.add("14-Dec-2016");
        videoCallAdapter = new VideocallAdapter(this, videocallerName,videocallName,videocallDate);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(this);
        callsList.setLayoutManager(recylerViewLayoutManager);
        callsList.setHasFixedSize(true);
        callsList.setAdapter(videoCallAdapter);

        if(new NetworkCheck().isOnline(DashBoardActivity.this)) {
            new getDetailsById().execute();

        }
        else
        {
            Toast.makeText(DashBoardActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }
        initTabs();
    }
    public void initTabs()
    {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Uploaded Documents"));
   //     tabLayout.addTab(tabLayout.newTab().setText("Download Documents"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             //   viewPager.setCurrentItem(position);
                tabLayout.getTabAt(position).select();
            }

            public void onPageSelected(int position) {
            }
        });
        //Creating our pager adapter
        TabsAdaptor adapter = new TabsAdaptor(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
                    postAPICall1(url, DashBoardActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new DownloadFilesTask().execute();
                    }
                });
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
                    progressDialog.dismiss();
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //    Log.e("resultJson",userTypeDetailss.toString());

    }
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);


            try {
                //  postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
                postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1)+"/limit/3", DashBoardActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
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
            Log.e("notifications",resultJson);
            if(resultJson!=null) {
                try {
                    msgs.clear();
                    notificationSenderNames.clear();
                    notificationSentDates.clear();
                    JSONArray jsonArray = new JSONArray(resultJson);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).toString());
                        msgs.add(jsonObject.getString("notification"));
                        //   senderNames.add(OpponentsActivity.OpponentNames.get(OpponentsActivity.OpponentIds.indexOf(jsonObject.getString("sentBy"))));
                        notificationSenderNames.add(userTypeDetailss.get(Integer.parseInt(jsonObject.getString("sentBy"))));
                        notificationSentDates.add(jsonObject.getString("sentDate"));
                    }
                    if (resultJson!=null) {

                    }
                  //  Log.e("data -->", sentDates.size() + "--" + senderNames.size() + "--" + msgs.size() + "--");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            recyclerViewAdapter = new RecyclerViewAdapter(DashBoardActivity.this, msgs, notificationSenderNames, notificationSentDates);
                            notificationsList.setHasFixedSize(true);
                            notificationsList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                            notificationsList.setAdapter(recyclerViewAdapter);
                        }
                    });
                } catch (Exception e) {
                }
            }
            else
            {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Unable to get valid data from server try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else  if(response.getStatusLine().getStatusCode()==500)
        {
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

        public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public  TextView senderName,msg,sentDate;
public LinearLayout parent;
            public ViewHolder(View v){

                super(v);

                senderName = (TextView)v.findViewById(R.id.senderName);
                msg = (TextView)v.findViewById(R.id.msg);
                sentDate = (TextView)v.findViewById(R.id.sentDate);
                parent= (LinearLayout) v.findViewById(R.id.parent);
                parent.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
try {
    msg1.setText(msgs.get(getAdapterPosition()).toString());
    sentDate1.setText(sentDates.get(getAdapterPosition()).toString());
  senderName1.setText(senderNames.get(getAdapterPosition()).toString());
    Log.e("ok", "ok" + senderNames.get(getAdapterPosition()).toString());
   // showMessage.show();
   // slideUp.animateIn();

}catch (Exception e)
{

}

                    }
                });

            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            view1 = LayoutInflater.from(context).inflate(R.layout.notification_custom,parent,false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position){
try {
    holder.msg.setText(msgs.get(position).toString());
    holder.sentDate.setText(sentDates.get(position).toString());
    holder.senderName.setText(senderNames.get(position).toString());
    holder.msg.setTypeface(typeface);
    holder.sentDate.setTypeface(typeface);
    holder.senderName.setTypeface(typeface);
} catch (Exception e)
{}
        }

        @Override
        public int getItemCount(){

            return msgs.size();
        }
    }

    //////videos lis adapter

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
            holder.callerName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
        }

        @Override
        public int getItemCount(){

            return videocallDate.size();
        }
    }

}
