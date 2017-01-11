package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;

import com.VideoCalling.sample.groupchatwebrtc.adapters.TabsAdaptor;
import com.VideoCalling.sample.groupchatwebrtc.fragments.UplodedDocs;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.utils.SendNotitcation;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mancj.slideup.SlideUp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
public class DashBoardActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    RecyclerView callsList, notificationsList;
    com.github.clans.fab.FloatingActionButton newNotification;
    ProgressDialog progressDialog;
    Dialog showMessage;
    SharedPreferences prefs;
    ArrayList msgs = new ArrayList();
    ArrayList notificationSenderNames = new ArrayList();
    ArrayList notificationSentDates = new ArrayList();
    ArrayList videocallerName = new ArrayList();
    ArrayList videocallName = new ArrayList();
    ArrayList videocallDate = new ArrayList();
    public static HashMap userTypeDetailss = new HashMap();
    public static HashMap<Integer,JSONObject> immigrantProfiles = new HashMap<Integer,JSONObject>();
    ArrayList ImmigrantNames = new ArrayList();
    RelativeLayout header1,header;
    TextView userName;
    public static  HashMap solicitor=new HashMap();
    public static  HashMap barrister=new HashMap();
    ArrayList ImmigrantIds = new ArrayList();
    RecyclerViewAdapter recyclerViewAdapter;
    VideocallAdapter videoCallAdapter;
    ViewPager mViewPager;
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    // TextView show_more_notification,show_more_video_cals;
    TextView title, senderName1, msg1, sentDate1,notification_to_name;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView call, notification, logout;
    Typeface typeface;
    RelativeLayout rel;
    View slideView;
    EditText notification_edit_txt;
    SlideUp slideUp;
    ImageView sendNotification;
    com.github.clans.fab.FloatingActionButton callf;
    int userType=0;
    TextView view_msg,time_stamp;
    CardView card2;
    Animation slide_down;
    ImageView show_more_uploaded_docs, show_more_video_cals, show_more_notification,show_more_documents;
    MaterialSpinner spinner;
    public static int selectedImmigrantId;
    SwipeRefreshLayout swipeRefreshLayout;
    String selectedPersonName;
    public static int callTo1,callTo2;

    public static  String startTime,endtime;
    JSONObject notificationObject=new JSONObject();
    com.github.clans.fab.FloatingActionMenu floating_parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        callf= (FloatingActionButton) findViewById(R.id.call);
        callf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_parent.hideMenu(true);
            }
        });
        newNotification= (FloatingActionButton) findViewById(R.id.new_notification);
        floating_parent= (FloatingActionMenu) findViewById(R.id.floating_parent);
        sendNotification= (ImageView) findViewById(R.id.sendNotification);
        header1= (RelativeLayout) findViewById(R.id.header1);
        header= (RelativeLayout) findViewById(R.id.header);

        view_msg= (TextView) findViewById(R.id.view_msg);
        userName= (TextView) findViewById(R.id.userName);
        time_stamp= (TextView) findViewById(R.id.time_stamp);
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    notificationObject.put("notification", notification_edit_txt.getText().toString());
                    notificationObject.put("sentBy", prefs.getInt("userId", -1));
                    notificationObject.put("sentDate", new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"));
                    if (prefs.getInt("userType", -1) == 1) {

                        notificationObject.put("sentTo", 94);
                    } else if (prefs.getInt("userType", -1) == 2) {
                        notificationObject.put("sentTo", 95);
                    }
                    Log.e("notificationObject", notificationObject.toString());
                    notificationObject.put("sentFor", selectedImmigrantId);
                    slideUp.hideImmediately();
                    new SendNotitcation(DashBoardActivity.this, notificationObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        if (prefs.getInt("userType", -1) == 0)
        {
            floating_parent.setVisibility(View.GONE);
        }
        progressDialog.setMessage("loading...");
        slideView = findViewById(R.id.slideView);
        slideUp = new SlideUp(slideView);
        notification_edit_txt= (EditText) findViewById(R.id.notification_edit_txt);
        notification_to_name= (TextView) findViewById(R.id.notification_to_name);
        show_more_documents= (ImageView) findViewById(R.id.show_more_documents);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               // Snackbar.make(view, ImmigrantIds.get(position) + " selected", Snackbar.LENGTH_LONG).show();
                toolbar.setTitle(item+" Dashboard");
                selectedPersonName=item;
                notification_to_name.setText(item.toString());
                msgs.clear();
                notificationSenderNames.clear();
                notificationSentDates.clear();
                recyclerViewAdapter = new RecyclerViewAdapter(DashBoardActivity.this, msgs, notificationSenderNames, notificationSentDates);
                notificationsList.setHasFixedSize(true);
                notificationsList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                notificationsList.setAdapter(recyclerViewAdapter);
                selectedImmigrantId = Integer.parseInt(ImmigrantIds.get(position).toString());
                new DownloadFilesTask().execute();
                Bundle bundle = new Bundle();
                bundle.putString("yes", "no");
                UplodedDocs uplodedDocs=new UplodedDocs();
                uplodedDocs.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frameParent,uplodedDocs );
                transaction.commit();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (prefs.getInt("userType", -1) == 0)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_theam_color);
            }
        else if(prefs.getInt("userType", -1) == 1)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
            changeTheam(R.color.solicor_theam_color);
          }
        else if(prefs.getInt("userType", -1) == 2)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_theam_color);
        }

         slideUp.hideImmediately();
        toolbar.setTitle("Dashboard");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        newNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  floating_parent.performClick();
                header1.setVisibility(View.GONE);
                view_msg.setVisibility(View.GONE);

                header.setVisibility(View.VISIBLE);
                notification_edit_txt.setVisibility(View.VISIBLE);
                notification_to_name.setText(selectedPersonName);
                notification_edit_txt.requestFocus();
                slideUp.animateIn();
            }
        });
        slideUp.setSlideListener(new SlideUp.SlideListener() {


            @Override
            public void onSlideDown(float v) {
                if (v >= 100) {
                } else {

                }
                Log.e("float", v + "--");
            }

            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == View.GONE) {
                    if (prefs.getInt("userType", -1) != 0) {

                        floating_parent.setVisibility(View.VISIBLE);
                        Log.e("visss", "gone");
                    }
                } else {
                    floating_parent.setVisibility(View.INVISIBLE);
                    Log.e("visss", "visible");
                }
            }
        });
        setSupportActionBar(toolbar);
        show_more_uploaded_docs = (ImageView) findViewById(R.id.show_more_uploaded_docs);
        show_more_uploaded_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      startActivity(new Intent(DashBoardActivity.this,UploadedDocumentsActivity.class));

            }
        });
        show_more_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, ShowmoreDocumentsActivity.class));
            }
        });
        typeface = Typeface.createFromAsset(getAssets(), "QuicksandRegular.ttf");
        showMessage = new Dialog(this);
        showMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showMessage.setContentView(R.layout.dialog_notification_custom_layout);
        senderName1 = (TextView) showMessage.findViewById(R.id.senderName);
        msg1 = (TextView) showMessage.findViewById(R.id.msg);
        sentDate1 = (TextView) showMessage.findViewById(R.id.sentDate);
        show_more_notification = (ImageView) findViewById(R.id.show_more_notification);
        show_more_video_cals = (ImageView) findViewById(R.id.show_more_video_cals);
        show_more_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, NotificationActivity.class));
            }
        });
        show_more_video_cals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, VideoCallsActivity.class));
            }
        });
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        if (prefs.getInt("userType", -1) == 0)
        {
            toolbar.setTitle(prefs.getString("name", "")+" Dashboard");

            userType=1;
            selectedImmigrantId=prefs.getInt("userId", -1);
            spinner.setVisibility(View.GONE);
        }
            callsList = (RecyclerView) findViewById(R.id.myCallList);
        notificationsList = (RecyclerView) findViewById(R.id.notifcationsList);
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
        videoCallAdapter = new VideocallAdapter(this, videocallerName, videocallName, videocallDate);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(this);
        callsList.setLayoutManager(recylerViewLayoutManager);
        callsList.setHasFixedSize(true);
        callsList.setAdapter(videoCallAdapter);

        if (new NetworkCheck().isOnline(DashBoardActivity.this)) {
            userTypeDetailss.clear();

           new getDetailsById().execute();

        } else {
            Toast.makeText(DashBoardActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }
        // initTabs();
    }
    // Initiating Menu XML file (menu.xml)
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        if (prefs.getInt("userType", -1) == 0)
        {
            MenuItem barrisetr = menu.findItem(R.id.barrisetr);
            barrisetr.setVisible(false);
            MenuItem case_action = menu.findItem(R.id.case_action);
            case_action.setVisible(false);
        }
        else if(prefs.getInt("userType", -1) == 1) {
            MenuItem uploaded = menu.findItem(R.id.uploaded);
            uploaded.setVisible(false);

        }
        else if(prefs.getInt("userType", -1) == 2) {
            MenuItem uploaded = menu.findItem(R.id.uploaded);
            uploaded.setVisible(false);
            MenuItem barrisetr = menu.findItem(R.id.barrisetr);
            barrisetr.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.uploaded:
                startActivity(new Intent(DashBoardActivity.this,ClientAfterLoginActivity.class));
                return true;

            case R.id.barrisetr:
              startActivity(new Intent(DashBoardActivity.this,OpponentsActivity.class));
                return true;

            case R.id.case_action:
                startActivity(new Intent(DashBoardActivity.this,CloseCaseActivity.class));
                return true;

            case R.id.log_out:
                new AlertDialog.Builder(this)
                .setTitle("Logout")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new LogoutClass().clearSesson(DashBoardActivity.this);
                            DashBoardActivity.this.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setCancelable(false)
                    .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void initTabs() {
/*

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
*/


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

            /*for (int i = 0; i <= 2; i++) {*/
                String url = "http://35.163.24.72:8080/VedioApp/service/user/type/" + userType;
                Log.e("urlls", url);
                try {
                    postAPICall1(url, DashBoardActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
           /* }*/


            return aLong;
        }


        protected void onPostExecute(Long result) {
//            swipeRefreshLayout.setRefreshing(false);
            userType++;
          if(userType<3)
          {
              new getDetailsById().execute();
          }
            else
          {
              Bundle bundle = new Bundle();
              bundle.putString("yes", "limit");
              UplodedDocs uplodedDocs=new UplodedDocs();
              uplodedDocs.setArguments(bundle);
              FragmentManager manager = getSupportFragmentManager();
              FragmentTransaction transaction = manager.beginTransaction();
              transaction.replace(R.id.frameParent,uplodedDocs );
              transaction.commit();
          }
           if(ImmigrantNames.size()>0){
               spinner.setItems(ImmigrantNames);
           }

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
        if (response.getStatusLine().getStatusCode() == 200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("resultJson--->", resultJson);
            try {

                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    userTypeDetailss.put(jsonObject.getInt("id"), jsonObject.getString("name"));
                    if(jsonObject.getInt("userType")==0)
                    {
                        immigrantProfiles.put(jsonObject.getInt("id"),jsonObject);
                        if(ImmigrantIds.size()==0)
                        {

                            selectedImmigrantId  =jsonObject.getInt("id");
                            selectedPersonName=jsonObject.getString("name").toString();
                            runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   try {
                                       toolbar.setTitle(jsonObject.getString("name") + " Dashboard");
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }
                           });

                        }
                    ImmigrantIds.add(jsonObject.getInt("id"));
                    ImmigrantNames.add(jsonObject.getString("name"));
                    }
                    else if(jsonObject.getInt("userType")==1)
                    {
                        solicitor.put("id",jsonObject.getInt("id"));
                    }
                    else if(jsonObject.getInt("userType")==2)
                    {
                        barrister.put("id",jsonObject.getInt("id"));
                    }
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
        } else if (response.getStatusLine().getStatusCode() == 500) {
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
            Long aLong = Long.valueOf(1);


            try {
                //  postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + selectedImmigrantId + "/limit/3", DashBoardActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
    private class VideoCallLogs extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);


            try {
                //  postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
                postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + selectedImmigrantId + "/limit/3", DashBoardActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
    public void postAPICall(String strurl, final Context context) throws Exception {
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
            Log.e("notifications", resultJson);
            if (resultJson != null) {
                try {
                    msgs.clear();
                    notificationSenderNames.clear();
                    notificationSentDates.clear();
                    JSONArray jsonArray = new JSONArray(resultJson);
                    if(jsonArray.length()==0)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DashBoardActivity.this, "Notifications not available...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).toString());
                        msgs.add(jsonObject.getString("notification"));
                        //   senderNames.add(OpponentsActivity.OpponentNames.get(OpponentsActivity.OpponentIds.indexOf(jsonObject.getString("sentBy"))));
                        notificationSenderNames.add(userTypeDetailss.get(Integer.parseInt(jsonObject.getString("sentBy"))));
                        notificationSentDates.add(jsonObject.getString("sentDate"));
                    }
                    if (resultJson != null) {

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
                        else {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                    Toast.makeText(DashBoardActivity.this, "Notifications not available...", Toast.LENGTH_SHORT).show();
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

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList msgs, senderNames, sentDates;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName, msg, sentDate;

        public RecyclerViewAdapter(Context context1, ArrayList msgs, ArrayList senderNames, ArrayList sentDates) {

            this.msgs = msgs;
            this.senderNames = senderNames;
            this.sentDates = sentDates;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView senderName, msg, sentDate;
            public LinearLayout parent;

            public ViewHolder(View v) {

                super(v);

                senderName = (TextView) v.findViewById(R.id.senderName);
                msg = (TextView) v.findViewById(R.id.msg);
                sentDate = (TextView) v.findViewById(R.id.sentDate);
                parent = (LinearLayout) v.findViewById(R.id.parent);
                parent.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            header1.setVisibility(View.VISIBLE);
                            view_msg.setVisibility(View.VISIBLE);
                            header.setVisibility(View.GONE);
                            notification_edit_txt.setVisibility(View.GONE);

                            view_msg.setText(msgs.get(getAdapterPosition()).toString());
                            time_stamp.setText(sentDates.get(getAdapterPosition()).toString());
                            userName.setText(senderNames.get(getAdapterPosition()).toString());
                            slideUp.animateIn();
                            //Log.e("ok", "ok" + senderNames.get(getAdapterPosition()).toString());
                            // showMessage.show();
                            // slideUp.animateIn();

                        } catch (Exception e) {

                        }

                    }
                });

            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view1 = LayoutInflater.from(context).inflate(R.layout.notification_custom, parent, false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                holder.msg.setText(msgs.get(position).toString());
                holder.sentDate.setText(sentDates.get(position).toString());
                holder.senderName.setText(senderNames.get(position).toString());
                holder.msg.setTypeface(typeface);
                holder.sentDate.setTypeface(typeface);
                holder.senderName.setTypeface(typeface);
            } catch (Exception e) {
            }
        }

        @Override
        public int getItemCount() {

            return msgs.size();
        }
    }

    //////videos lis adapter

    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder> {

        ArrayList callerName, videoName, callDate;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName, msg, sentDate;

        public VideocallAdapter(Context context1, ArrayList callerName, ArrayList videoName, ArrayList callDate) {

            this.callerName = callerName;
            this.videoName = videoName;
            this.callDate = callDate;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView callerName, videoName, callDate;
LinearLayout share_and_down;
            public ViewHolder(View v) {

                super(v);

                callerName = (TextView) v.findViewById(R.id.callerName);
                videoName = (TextView) v.findViewById(R.id.videoName);
                callDate = (TextView) v.findViewById(R.id.callDate);
                share_and_down= (LinearLayout) v.findViewById(R.id.share_and_down);
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
            holder.share_and_down.setVisibility(View.GONE);
            holder.callerName.setText(videocallerName.get(position).toString());
            holder.videoName.setText(videocallName.get(position).toString());
            holder.callDate.setText(videocallDate.get(position).toString());
            holder.callerName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
        }

        @Override
        public int getItemCount() {

            return videocallDate.size();
        }
    }

}
