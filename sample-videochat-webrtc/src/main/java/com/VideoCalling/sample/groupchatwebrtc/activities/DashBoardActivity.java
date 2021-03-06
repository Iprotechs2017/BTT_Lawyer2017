package com.VideoCalling.sample.groupchatwebrtc.activities;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;

import com.VideoCalling.sample.groupchatwebrtc.adapters.TabsAdaptor;
import com.VideoCalling.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.VideoCalling.sample.groupchatwebrtc.fragments.UplodedDocs;
import com.VideoCalling.sample.groupchatwebrtc.services.NotificationService;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.SendNotitcation;
import com.VideoCalling.sample.groupchatwebrtc.utils.VideoLogsModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mancj.slideup.SlideUp;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.readystatesoftware.viewbadger.BadgeView;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    RecyclerView callsList, notificationsList;
    com.github.clans.fab.FloatingActionButton newNotification;
    ProgressDialog progressDialog;
    Dialog showMessage;
    public static SharedPreferences prefs;
    protected QBResRequestExecutor requestExecutor;
    ArrayList msgs = new ArrayList();
    int NOTIFICATION_ID = 1;
    public static String onResume = "no";
    ArrayList notificationSenderNames = new ArrayList();
    ArrayList notificationSentDates = new ArrayList();
    ArrayList notificationIds = new ArrayList();
    ArrayList videocallerName = new ArrayList();
    ArrayList videocallName = new ArrayList();
    ArrayList videocallDate = new ArrayList();
    public static HashMap userTypeDetailss = new HashMap();
    public static HashMap<Integer, JSONObject> immigrantProfiles = new HashMap<Integer, JSONObject>();
    public static WebSocketClient mWebSocketClient;
    ArrayList ImmigrantNames = new ArrayList();
    public static HashMap ImgMapping = new HashMap();
    RelativeLayout header1, header;
    TextView userName, notification_limit;
    public static ArrayList<VideoLogsModel> videoLogs = new ArrayList<VideoLogsModel>();
    public static HashMap solicitor = new HashMap();
    public static HashMap barrister = new HashMap();
    ArrayList ImmigrantIds = new ArrayList();
    RecyclerViewAdapter recyclerViewAdapter;
    VideocallAdapter videoCallAdapter;
    public static boolean docReload=false;
    public static String docRelode = "no";
    public static String logsRelode = "no";
    public static String notifRelode = "no";
    ViewPager mViewPager;
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    // TextView show_more_notification,show_more_video_cals;
    TextView title, senderName1, msg1, sentDate1, notification_to_name;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView call, notification, logout;
    Typeface typeface;
    RelativeLayout rel;
    View slideView;
    public static EditText notification_edit_txt;
    SlideUp slideUp;
    ImageView sendNotification, close_window, close_window_send_notification;
    com.github.clans.fab.FloatingActionButton callf;
    int userType = 0;
    TextView view_msg, time_stamp;
    CardView card2;
    Animation slide_down;
    ImageView show_more_uploaded_docs, show_more_video_cals, show_more_notification, show_more_documents;
    MaterialSpinner spinner;
    public static int selectedImmigrantId;
    SwipeRefreshLayout swipeRefreshLayout;
    String selectedPersonName;
    public static int callTo1, callTo2 = 0;
    public static String service = "no";
    public static String startTime, endtime;
    JSONObject notificationObject = new JSONObject();
    com.github.clans.fab.FloatingActionMenu floating_parent;
    public static Dialog notification_dialog;
    SharedPreferences.Editor editor;
    private QbUsersDbManager dbManager;
    int back=0;
    DB snappydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        try {
            snappydb = DBFactory.open(DashBoardActivity.this);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        editor = getSharedPreferences("loginDetails", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        notification_dialog = new Dialog(this);
        notification_dialog.setCancelable(false);
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        notification_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notification_dialog.setContentView(R.layout.notification_pop_up);
        callf = (FloatingActionButton) findViewById(R.id.call);
        callf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_parent.hideMenu(true);
            }
        });
        close_window = (ImageView) notification_dialog.findViewById(R.id.close_window);
        close_window_send_notification = (ImageView) notification_dialog.findViewById(R.id.close_window_send_notification);
        newNotification = (FloatingActionButton) findViewById(R.id.new_notification);
        floating_parent = (FloatingActionMenu) findViewById(R.id.floating_parent);
        sendNotification = (ImageView) notification_dialog.findViewById(R.id.sendNotification);
        header1 = (RelativeLayout) notification_dialog.findViewById(R.id.header1);
        header = (RelativeLayout) notification_dialog.findViewById(R.id.header);
        if (prefs.getInt("userType", -1) != 1) {

            startLoadUsers();
        }
        editor.putString("service", "stop");
        editor.apply();
        stopService(new Intent(this, NotificationService.class));
        connectWebSocket();
        view_msg = (TextView) notification_dialog.findViewById(R.id.view_msg);
        userName = (TextView) notification_dialog.findViewById(R.id.userName);
        time_stamp = (TextView) notification_dialog.findViewById(R.id.time_stamp);
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification_edit_txt.getText().toString().trim().length() > 0) {
                    try {
                        notificationObject.put("notification", notification_edit_txt.getText().toString());
                        notificationObject.put("sentBy", prefs.getInt("userId", -1));
                        notificationObject.put("sentDate", new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"));
                        notificationObject.put("sentFor", selectedImmigrantId);
                        Log.e("notificationObject", notificationObject.toString());
                        notificationObject.put("sentTo", selectedImmigrantId);
                 /*       String message = notification_edit_txt.getText().toString();
                        if (prefs.getInt("userType", -1) == 1) {
                            String data = selectedImmigrantId + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null);
                            mWebSocketClient.send(data);
                        } else if (prefs.getInt("userType", -1) == 2) {
                            String data = selectedImmigrantId + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null);
                            mWebSocketClient.send(data);
                            String data1 = Integer.parseInt(solicitor.get("id").toString()) + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null);
                            mWebSocketClient.send(data1);
                        }
                        Toast.makeText(DashBoardActivity.this, "Message has sent Succesfully...", Toast.LENGTH_SHORT).show();*/
                        new SendNotitcation(DashBoardActivity.this, notificationObject);

                        new DownloadFilesTask().execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DashBoardActivity.this, "Enter Message...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (prefs.getInt("userType", -1) == 0) {
            floating_parent.setVisibility(View.GONE);
        }
        progressDialog.setMessage("loading...");
        slideView = findViewById(R.id.slideView);
        slideUp = new SlideUp(slideView);
        notification_edit_txt = (EditText) notification_dialog.findViewById(R.id.notification_edit_txt);
        notification_to_name = (TextView) notification_dialog.findViewById(R.id.notification_to_name);
        show_more_documents = (ImageView) findViewById(R.id.show_more_documents);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //toolbar.setTitle(item + " Dashboard");
                selectedPersonName = item.toString();
                notification_to_name.setText(item.toString());
                msgs.clear();
                editor.putInt("selected", position);
                editor.putString("selectedName", selectedPersonName);
                editor.commit();
                notificationSenderNames.clear();
                /* videoCallAdapter = new VideocallAdapter(DashBoardActivity.this, videoLogs);
                LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(DashBoardActivity.this);
                callsList.setLayoutManager(recylerViewLayoutManager);
                callsList.setHasFixedSize(true);
                callsList.setAdapter(videoCallAdapter);*/
              /*  notificationSentDates.clear();
                recyclerViewAdapter = new RecyclerViewAdapter(DashBoardActivity.this, msgs, notificationSenderNames, notificationSentDates);
                notificationsList.setHasFixedSize(true);
                notificationsList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                notificationsList.setAdapter(recyclerViewAdapter);*/
                selectedImmigrantId = Integer.parseInt(ImmigrantIds.get(position).toString());
                new DownloadFilesTask().execute();
                new VideoCallLogs().execute();
                Bundle bundle = new Bundle();
                bundle.putString("yes", "no");
                bundle.putString("userId", "0");
                UplodedDocs uplodedDocs = new UplodedDocs();
                uplodedDocs.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frameParent, uplodedDocs);
                transaction.commit();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(prefs.getString("name", "") + " Dashboard");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        if (prefs.getInt("userType", -1) == 0) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
            changeTheam(R.color.immigrant_notifi_color);
        } else if (prefs.getInt("userType", -1) == 1) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.solicor_theam_color));
            changeTheam(R.color.solicitor_notifi_color);
        } else if (prefs.getInt("userType", -1) == 2) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.barrister_theam_color));
            changeTheam(R.color.barrister_notifi_color);
        }

        slideUp.hideImmediately();
        //  toolbar.setTitle("Dashboard");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        newNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header1.setVisibility(View.GONE);
                view_msg.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                notification_limit.setVisibility(View.VISIBLE);
                notification_edit_txt.setVisibility(View.VISIBLE);
                notification_to_name.setText(selectedPersonName);
                notification_dialog.show();
            }
        });

        notification_edit_txt.addTextChangedListener(textWatcher);
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
                        notification_edit_txt.setText("");
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
                startActivity(new Intent(DashBoardActivity.this, UploadedDocumentsActivity.class));

            }
        });
        show_more_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, ShowmoreDocumentsActivity.class).putExtra("id", "0"));
            }
        });
        typeface = Typeface.createFromAsset(getAssets(), "QuicksandRegular.ttf");
        showMessage = new Dialog(this);
        showMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showMessage.setContentView(R.layout.dialog_notification_custom_layout);
        notification_limit = (TextView) notification_dialog.findViewById(R.id.notification_limit);
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

        if (prefs.getInt("userType", -1) == 0) {
            toolbar.setTitle(prefs.getString("name", "") + " Dashboard");

            userType = 0;
            selectedImmigrantId = prefs.getInt("userId", -1);
            spinner.setVisibility(View.GONE);
        }
        callsList = (RecyclerView) findViewById(R.id.myCallList);
        notificationsList = (RecyclerView) findViewById(R.id.notifcationsList);
        callsList.setNestedScrollingEnabled(false);
        notificationsList.setNestedScrollingEnabled(false);
        if (new NetworkCheck().isOnline(DashBoardActivity.this)) {
            userTypeDetailss.clear();
            new getDetailsById().execute();

        } else {
            Toast.makeText(DashBoardActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }
        close_window_send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_dialog.dismiss();
                notification_edit_txt.setText("");
            }
        });
        close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slideUp.hideImmediately();
                notification_edit_txt.setText("");
                notification_dialog.dismiss();
            }
        });
        if (prefs.getInt("userType", -1) != 0) {
//            selectedImmigrantId = Integer.parseInt(ImmigrantIds.get(prefs.getInt("selected", 0)).toString());
        }
    }

    public static void sendNotification() {
        String message = notification_edit_txt.getText().toString();
        if (prefs.getInt("userType", -1) == 1) {
            String data = selectedImmigrantId + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null)+ "-splspli-" + selectedImmigrantId;
            mWebSocketClient.send(data);
        } else if (prefs.getInt("userType", -1) == 2) {
            String data = selectedImmigrantId + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null)+ "-splspli-" + selectedImmigrantId;
            mWebSocketClient.send(data);
            String data1 = Integer.parseInt(solicitor.get("id").toString()) + "-splspli-" + "notification" + "-splspli-" + message + "-splspli-" + prefs.getString("name", null)+ "-splspli-" + selectedImmigrantId;
            mWebSocketClient.send(data1);
        }
        notification_edit_txt.setText("");
        notification_dialog.dismiss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoLogs.clear();
        editor.putString("service", "start");
        editor.putInt("dashBoard", 0);
        editor.apply();
        mWebSocketClient.close();
        startService(new Intent(this, NotificationService.class));

    }


    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //after text changed
            //     Log.e("count",notification_edit_txt.length()+"--");
            //  notification_edit_txt.setVisibility(View.VISIBLE);
            notification_limit.setText(notification_edit_txt.length() + "/" + "100");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void changeTheam(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        if (prefs.getInt("userType", -1) == 0) {
            MenuItem barrisetr = menu.findItem(R.id.barrisetr);
            barrisetr.setVisible(false);
            MenuItem case_action = menu.findItem(R.id.case_action);
            case_action.setVisible(false);
        } else if (prefs.getInt("userType", -1) == 1) {
            MenuItem uploaded = menu.findItem(R.id.uploaded);
            uploaded.setVisible(false);

        } else if (prefs.getInt("userType", -1) == 2) {
            MenuItem uploaded = menu.findItem(R.id.uploaded);
            uploaded.setVisible(false);
            MenuItem barrisetr = menu.findItem(R.id.barrisetr);
            barrisetr.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.uploaded:
                startActivity(new Intent(DashBoardActivity.this, ClientAfterLoginActivity.class));
                return true;

            case R.id.barrisetr:
                startActivity(new Intent(DashBoardActivity.this, OpponentsActivity.class));
                return true;

            case R.id.case_action:
                startActivity(new Intent(DashBoardActivity.this, CloseCaseActivity.class));
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
            if(!progressDialog.isShowing()) {
                progressDialog.show();
            }
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
            if (userType < 3) {
                new getDetailsById().execute();
            } else  {
                Bundle bundle = new Bundle();
                bundle.putString("yes", "limit");
                bundle.putString("userId", "0");
                UplodedDocs uplodedDocs = new UplodedDocs();
                uplodedDocs.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frameParent, uplodedDocs);
                transaction.commit();
                new VideoCallLogs().execute();
                new DownloadFilesTask().execute();
            }
            if (ImmigrantNames.size() > 0) {
                spinner.setItems(ImmigrantNames);
                spinner.setSelectedIndex(prefs.getInt("selected", 0));
                selectedImmigrantId = Integer.parseInt(ImmigrantIds.get(prefs.getInt("selected", 0)).toString());
                selectedPersonName = ImmigrantNames.get(prefs.getInt("selected", 0)).toString();

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
                    if (jsonObject.getInt("userType") == 0) {
                        ImgMapping.put(jsonObject.getString("name").toString(), jsonObject.getInt("id"));
                        immigrantProfiles.put(jsonObject.getInt("id"), jsonObject);
                        if (ImmigrantIds.size() == 0) {
                            selectedImmigrantId = jsonObject.getInt("id");
                            selectedPersonName = jsonObject.getString("name").toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (jsonObject.getInt("userType") != 0) {
                                            ///toolbar.setTitle(jsonObject.getString("name") + " Dashboard");
                                        } else {
                                            //toolbar.setTitle(prefs.getString("name", " ") + " Dashboard");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                        ImmigrantIds.add(jsonObject.getInt("id"));
                        ImmigrantNames.add(jsonObject.getString("name"));
                    } else if (jsonObject.getInt("userType") == 1) {
                        Log.e("solicitor",jsonObject.getInt("id")+"--");
                        solicitor.put("id", jsonObject.getInt("id"));
                        solicitor.put("name", jsonObject.getString("name"));
                    } else if (jsonObject.getInt("userType") == 2) {
                        barrister.put("id", jsonObject.getInt("id"));
                        barrister.put("name", jsonObject.getString("name"));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


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
                if (prefs.getInt("userType", -1) != 0) {
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + selectedImmigrantId + "/limit/3", DashBoardActivity.this);
                } else {
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + prefs.getInt("userId", -1) + "/limit/3", DashBoardActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }

        protected void onPostExecute(Long result) {
            connectWebSocket();
        }
    }

    private class VideoCallLogs extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);


            try {

                Log.e("solid",solicitor.get("id") + "---");
                if (prefs.getInt("userType", -1) != 0) {
                    getVideoLogs("http://35.163.24.72:8080/VedioApp/service/VideoLog/getLog/callFrom/" + Integer.parseInt(solicitor.get("id") + "") + "/callTo/" + selectedImmigrantId, DashBoardActivity.this);
                } else {
                    getVideoLogs("http://35.163.24.72:8080/VedioApp/service/VideoLog/getLog/callFrom/" + Integer.parseInt(solicitor.get("id").toString()) + "/callTo/" + prefs.getInt("userId", -1), DashBoardActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
            onResume = "no";

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
        Log.e("response", response.getStatusLine().getStatusCode() + "--");
        if (response.getStatusLine().getStatusCode() == 200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            if (resultJson != null) {
                try {
                    videoLogs.clear();
                    String name;
                    JSONArray jsonArray = new JSONArray(resultJson);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                       /* if(jsonObject.getInt("callFrom")==prefs.getInt("userId",-1))
                        {
                            name="Me";

                        }
                        else
                        {
                            name=solicitor.get("name").toString();
                        }*/
                        if (jsonObject.getString("duration").split(":")[0].equalsIgnoreCase("12")) {
                            VideoLogsModel videoLogsModel = new VideoLogsModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("callFrom"),
                                    solicitor.get("name").toString(),
                                    jsonObject.getString("startTime"),
                                    jsonObject.getString("day"),
                                    userTypeDetailss.get(jsonObject.getInt("callTo1")).toString(),
                                    userTypeDetailss.get(jsonObject.getInt("callTo2")).toString(),
                                    jsonObject.getInt("callTo1"),
                                    jsonObject.getInt("callTo2"),
                                    jsonObject.getString("endTime"),
                                    "00:" + jsonObject.getString("duration").split(":")[1].toString() + ":" + jsonObject.getString("duration").split(":")[2].toString()
                            );
                            videoLogs.add(videoLogsModel);
                        } else {
                            VideoLogsModel videoLogsModel = new VideoLogsModel(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("callFrom"),
                                    solicitor.get("name").toString(),
                                    jsonObject.getString("startTime"),
                                    jsonObject.getString("day"),
                                    userTypeDetailss.get(jsonObject.getInt("callTo1")).toString(),
                                    userTypeDetailss.get(jsonObject.getInt("callTo2")).toString(),
                                    jsonObject.getInt("callTo1"),
                                    jsonObject.getInt("callTo2"),
                                    jsonObject.getString("endTime"),
                                    jsonObject.getString("duration")
                            );
                            videoLogs.add(videoLogsModel);
                        }
                        try
                        {
                            snappydb.getInt(jsonObject.getInt("id") + "");
                        }
                        catch (Exception e)
                        {
                            snappydb.putInt(jsonObject.getInt("id") + "", 0);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshVideoLogs();

                        }
                    });
                    Log.e("videoLogs", videoLogs.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                     /*       recyclerViewAdapter = new RecyclerViewAdapter(DashBoardActivity.this, msgs, notificationSenderNames, notificationSentDates);
                            notificationsList.setHasFixedSize(true);
                            notificationsList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                            notificationsList.setAdapter(recyclerViewAdapter);*/
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoLogs.clear();
                            refreshVideoLogs();
                            progressDialog.dismiss();

                        }
                    });
                }
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoLogs.clear();
                        refreshVideoLogs();
                        progressDialog.dismiss();
                        Toast.makeText(context, "Unable to get valid data from server try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (response.getStatusLine().getStatusCode() == 204) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoLogs.clear();
                    refreshVideoLogs();
                    progressDialog.dismiss();
if(!onResume.equalsIgnoreCase("yes")){
                    Toast.makeText(DashBoardActivity.this, "Video calls data not available...", Toast.LENGTH_SHORT).show();
                }
                }
            });
        } else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoLogs.clear();
                    refreshVideoLogs();

                    progressDialog.dismiss();
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Log.e("result", resultJson);
    }

    public void refreshVideoLogs() {
        try {
            Collections.reverse(videoLogs);
            videoCallAdapter = new VideocallAdapter(DashBoardActivity.this, videoLogs);
            LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(DashBoardActivity.this);
            callsList.setLayoutManager(recylerViewLayoutManager);
            callsList.setHasFixedSize(true);
            callsList.setAdapter(videoCallAdapter);
        } catch (Exception e) {

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
        Log.e("response", response.getStatusLine().getStatusCode() + "--");
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
                    clearNotification();
                    JSONArray jsonArray = new JSONArray(resultJson);
                    if (jsonArray.length() == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {

                        JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).toString());
                        msgs.add(jsonObject.getString("notification"));
                        Log.e("snappy", jsonObject.getInt("id") + "");
                        notificationIds.add(jsonObject.getInt("id"));

                        try
                {
                    snappydb.getInt(jsonObject.getInt("id") + "");
                }
                catch (Exception e)
                {
                    snappydb.putInt(jsonObject.getInt("id") + "", 0);
                }

                        //   senderNames.add(OpponentsActivity.OpponentNames.get(OpponentsActivity.OpponentIds.indexOf(jsonObject.getString("sentBy"))));
                      /* if(Integer.parseInt(jsonObject.getString("sentBy"))==prefs.getInt("userId", -1)) {
                           notificationSenderNames.add("Me");
                       }
                        else
                       {*/
                           notificationSenderNames.add(userTypeDetailss.get(Integer.parseInt(jsonObject.getString("sentBy"))));
                       //}
                        notificationSentDates.add(jsonObject.getString("sentDate"));
                    }
                    try
                    {

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                       }
                    if (resultJson != null) {

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            refreshNotifications();
                        }
                    });
                } catch (Exception e) {
                }
            } else {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearNotification();
                        refreshNotifications();
                        Toast.makeText(context, "Unable to get valid data from server try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (response.getStatusLine().getStatusCode() == 204) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clearNotification();
                    refreshNotifications();
                    Toast.makeText(DashBoardActivity.this, "Notifications not available...", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    clearNotification();
                    refreshNotifications();
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Log.e("result", resultJson);
    }

    public void clearNotification() {
        msgs.clear();
        notificationSenderNames.clear();
        notificationSentDates.clear();
        notificationIds.clear();
    }

    public void refreshNotifications() {
        recyclerViewAdapter = new RecyclerViewAdapter(DashBoardActivity.this, msgs, notificationSenderNames, notificationSentDates,notificationIds);
        notificationsList.setHasFixedSize(true);
        notificationsList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
        notificationsList.setAdapter(recyclerViewAdapter);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList msgs, senderNames, sentDates;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName, msg, sentDate;

        public RecyclerViewAdapter(Context context1, ArrayList msgs, ArrayList senderNames, ArrayList sentDates,ArrayList notificationIds) {

            this.msgs = msgs;
            this.senderNames = senderNames;
            this.sentDates = sentDates;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView senderName, msg, sentDate,notification_status;
            public LinearLayout parent;
            public ViewHolder(View v) {

                super(v);

                senderName = (TextView) v.findViewById(R.id.senderName);
                notification_status= (TextView) v.findViewById(R.id.notification_status);
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
                            notification_limit.setVisibility(View.GONE);
                            view_msg.setText(msgs.get(getAdapterPosition()).toString());
                            time_stamp.setText(sentDates.get(getAdapterPosition()).toString());
                            userName.setText(senderNames.get(getAdapterPosition()).toString());
                            notification_dialog.show();
                            snappydb.putInt(notificationIds.get(getAdapterPosition()).toString(),1);
                            recyclerViewAdapter.notifyDataSetChanged();
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
                if(snappydb.getInt(notificationIds.get(position).toString())==0)
                {
                holder.notification_status.setVisibility(View.VISIBLE);

                }
                else
                {
                    holder.notification_status.setVisibility(View.GONE);
                }
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

    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder> {

        ArrayList callerName, videoName, callDate;
        Context context;
        View view1;
        ViewHolder viewHolder1;
        TextView senderName, msg, sentDate;
        ArrayList<VideoLogsModel> videoLogs;

        public VideocallAdapter(Context context1, ArrayList<VideoLogsModel> videoLogs) {

            this.videoLogs = videoLogs;
            context = context1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView callerName, videoName, callDate,notification_status;
            LinearLayout share_and_down, parent;
            ImageView arrow;
            public ViewHolder(View v) {

                super(v);
                notification_status= (TextView) v.findViewById(R.id.notification_status);
                arrow= (ImageView) v.findViewById(R.id.arrow);
                callerName = (TextView) v.findViewById(R.id.callerName);
                videoName = (TextView) v.findViewById(R.id.videoName);
                callDate = (TextView) v.findViewById(R.id.callDate);
                share_and_down = (LinearLayout) v.findViewById(R.id.share_and_down);
                parent = (LinearLayout) v.findViewById(R.id.parent);
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
                try {
                    if(snappydb.getInt(videoLogs.get(position).getId()+"")==0)
                    {
                        holder.notification_status.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        holder.notification_status.setVisibility(View.GONE);
                    }
                } catch (SnappydbException e) {
                    e.printStackTrace();
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
                        try {
                            snappydb.putInt(videoLogs.get(position).getId()+"",1);
                        } catch (SnappydbException e) {
                            e.printStackTrace();
                        }
                        videoCallAdapter.notifyDataSetChanged();
                        startActivity(new Intent(DashBoardActivity.this, IndividualVideocallActivity.class).putExtra("position", position));
                    }
                });
            }
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

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://183.82.113.165:8089");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened");
                int userId = prefs.getInt("userId", -1);
                String data = userId + "-splspli-" + "reg";
                try {
                    mWebSocketClient.send(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.e("message Dashboard---->", message);
                createNotification(message);


            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void createNotification(String message) {
                try {
                    String[] split = message.split("-splspli-");
                    if(split.length!=6) {
                        if (split[2].toString().indexOf("documents...") < 0) {
                            if (split[2].toString().trim().length() > 0) {
                                Intent intent = new Intent(DashBoardActivity.this, NotificationActivity.class);
                                PendingIntent pIntent = PendingIntent.getActivity(DashBoardActivity.this, (int) System.currentTimeMillis(), intent, 0);
                                Notification noti = new Notification.Builder(DashBoardActivity.this)
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle("BTT Lawyer")
                                        .setContentText(split[3].toString() + ":" + split[2].toString())
                                        .setContentIntent(pIntent)
                                        .build();

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                                noti.defaults = Notification.DEFAULT_ALL;
                                notificationManager.notify(NOTIFICATION_ID++, noti);
                                new DownloadFilesTask().execute();
                            }
                        } else {

                            Intent intent = new Intent(DashBoardActivity.this, ShowmoreDocumentsActivity.class).putExtra("id", split[3].toString());
                            PendingIntent pIntent = PendingIntent.getActivity(DashBoardActivity.this, (int) System.currentTimeMillis(), intent, 0);
                            Notification noti = new Notification.Builder(DashBoardActivity.this)
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentTitle("BTT Lawyer")
                                    .setContentText(split[2].toString())
                                    .setContentIntent(pIntent)
                                    .build();

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            noti.flags |= Notification.FLAG_AUTO_CANCEL;
                            noti.defaults = Notification.DEFAULT_ALL;
                            notificationManager.notify(NOTIFICATION_ID++, noti);
                            UplodedDocs uplodedDocs = new UplodedDocs();
                            Bundle bundle = new Bundle();
                            bundle.putString("yes", "no");
                            bundle.putString("userId", "0");
                            uplodedDocs.setArguments(bundle);
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.frameParent, uplodedDocs);
                            transaction.commit();
                        }
                    }
                    else
                    {
                        new LogoutClass().clearSesson(DashBoardActivity.this);
                        DashBoardActivity.this.finish();
                        Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(DashBoardActivity.this, (int) System.currentTimeMillis(), intent, 0);
                        Notification noti = new Notification.Builder(DashBoardActivity.this)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("BTT Lawyer")
                                .setContentText(split[3].toString() + ":" + split[2].toString())
                                .setContentIntent(pIntent)
                                .build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        noti.flags |= Notification.FLAG_AUTO_CANCEL;
                        noti.defaults = Notification.DEFAULT_ALL;
                        notificationManager.notify(NOTIFICATION_ID++, noti);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b)

            {
                if(prefs.getString("service","").equalsIgnoreCase("stop")) {

                    connectWebSocket();
                }
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                if(prefs.getString("service","").equalsIgnoreCase("stop")) {

                    connectWebSocket();
                }
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void startLoadUsers() {
        requestExecutor.loadUsersByTag(String.valueOf(Consts.PREF_CURREN_ROOM_NAME), new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {

                dbManager.saveAllUsers(result, true);
                //initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                startLoadUsers();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString("service","start");
        editor.putInt("dashBoard", 0);
        editor.apply();
        mWebSocketClient.close();
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("on resume","resume");
        editor.putInt("dashBoard", 1);
        editor.putString("service", "stop");
        editor.apply();
        stopService(new Intent(this, NotificationService.class));
        connectWebSocket();
        if (docRelode.equalsIgnoreCase("yes"))
        {
            Bundle bundle = new Bundle();
            bundle.putString("yes", "no");
            bundle.putString("userId", "0");
            UplodedDocs uplodedDocs = new UplodedDocs();
            uplodedDocs.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frameParent, uplodedDocs);
            transaction.commit();
            docRelode = "no";
        }
        if (onResume.equalsIgnoreCase("yes")) {
            new DownloadFilesTask().execute();
            new VideoCallLogs().execute();
        }
if(notifRelode.equalsIgnoreCase("yes"))
{
    new DownloadFilesTask().execute();
    notifRelode="no";
}
    }
}
