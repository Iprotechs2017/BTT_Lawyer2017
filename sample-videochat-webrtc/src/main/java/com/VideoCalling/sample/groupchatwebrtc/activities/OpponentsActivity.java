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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.adapters.OpponentsAdapter;
import com.VideoCalling.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.VideoCalling.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.MyService;
import com.VideoCalling.sample.groupchatwebrtc.utils.PermissionsChecker;
import com.VideoCalling.sample.groupchatwebrtc.utils.PushNotificationSender;
import com.VideoCalling.sample.groupchatwebrtc.utils.UserType;
import com.VideoCalling.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.crashlytics.android.Crashlytics;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

/**
 * QuickBlox team
 */
public class OpponentsActivity extends BaseActivity {
    private static final String TAG1 = OpponentsActivity.class.getSimpleName();
    private static final long ON_ITEM_CLICK_DELAY = TimeUnit.SECONDS.toMillis(10);
    Toolbar toolbar;
    Long timeStamp;
    WebSocketClient mWebSocketClient;
    de.hdodenhof.circleimageview.CircleImageView reload;
    String videoPath = null;
    int NOTIFICATION_ID=1;
    Dialog requestDialog;
    public static List selectedArray=new ArrayList();
    RelativeLayout bar_reg;
    ArrayList<Integer> opponentsList=new ArrayList<Integer>();
    public static HashMap userTypeDetails;
    public static ArrayList OpponentNames = new ArrayList();
    public static ArrayList OpponentIds = new ArrayList();
    private OpponentsAdapter opponentsAdapter;
    private ListView opponentsListView;
    private QBUser currentUser;
    public static ArrayList<QBUser> currentOpponentsList;
    private ArrayList<QBUser> currentOpponentsList1;
    private ArrayList<QBUser> OpponentsList;
    public static ArrayList callTo=new ArrayList();
    public static String callType;
    public static int onlineUser;
    EditText msgEdt;
    Button send,close;
    String message=null;
    protected QBResRequestExecutor requestExecutor;
    private QbUsersDbManager dbManager;
    public static ArrayList<Integer> selected = new ArrayList<Integer>();
    public static ArrayList<UserType> userType = new ArrayList<UserType>();
    SharedPreferences qb;
    String callto="";
    public static String connection = "normal";
    String deleteBarister = "http://api.androidhive.info/volley/person_object.json";
    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;
    ImageView logout, makeCall, show_notifications;
    private PermissionsChecker checker;
    TextView screen_title;
    Button bar_registration;
    de.hdodenhof.circleimageview.CircleImageView notification;
    SharedPreferences prefs;
    TextView bar_name;
    private static final String TAG = "MainActivity";
    LinearLayout bar_after_login;
        HashMap baristerQB=new HashMap();
    public static de.hdodenhof.circleimageview.CircleImageView bar_remove;
    public static ImageView bar_call;
    String type="";

    RelativeLayout  bar_reg_lay,parent;
    public static String  baristerName,baristerId;


    public static void start(Context context, boolean isRunForCall) {
        Intent intent = new Intent(context, OpponentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_opponents);
        parent= (RelativeLayout) findViewById(R.id.parent);
        if (prefs.getInt("userType", -1) == 0) {
        parent.setVisibility(View.GONE);
        }
        else
        {
            parent.setVisibility(View.VISIBLE);
        }
            requestExecutor = App.getInstance().getQbResRequestExecutor();
        userTypeDetails=new HashMap();
        qb = getSharedPreferences("QB", MODE_PRIVATE);
        connectWebSocket();

        initFields();
        initDefaultActionBar();
        initUi();
        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {

                CallActivity.start(OpponentsActivity.this, true);


        }
       // scribeFromPushes();
        checker = new PermissionsChecker(getApplicationContext());
        bar_after_login = (LinearLayout) findViewById(R.id.bar_after_login);
        bar_name = (TextView) findViewById(R.id.bar_name);
        if (prefs.getInt("userType", -1) != 0) {
            new getDetailsById().execute();
            new LoginAsyncGetAll().execute();
            startLoadUsers();
        }
    }
        public static void hideBarrister()
        {
            bar_call.setVisibility(View.INVISIBLE);
            bar_remove.setVisibility(View.INVISIBLE);
        }
        public static void showBarrister()
    {
        bar_call.setVisibility(View.VISIBLE);
        bar_remove.setVisibility(View.VISIBLE);
    }
        private void scribeFromPushes() {
        googlePlayServicesHelper.registerInGcmInBackground(Consts.GCM_SENDER_ID);
        }
public void callTo(String callto)
{

    if (callto.equalsIgnoreCase("sol")) {
        startCall(true);

        startPermissionsActivity(false);
    } else if (callto.equalsIgnoreCase("bar")) {
        Log.e("error", "error");
        //callTo
        if (baristerQB.get("id").toString() != null) {
            callBarister(Integer.parseInt(baristerQB.get("id").toString()), baristerQB.get("name").toString());
        }
        else
        {
            Toast.makeText(this, "Barrister not registered...", Toast.LENGTH_SHORT).show();
        }

    }
}
    @Override
    protected void onResume()
    {
        super.onResume();
        selectedArray.clear();
        showBarrister();
        initFields();
        initDefaultActionBar();
            initUi();
        connectWebSocket();
        new getDetailsById().execute();
        new LoginAsyncGetAll().execute();
        startLoadUsers();

    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(OpponentsActivity.this, true);
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        hideProgressDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_opponents);
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void initFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }
        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }

    private void startLoadUsers() {
        showProgressDialog(R.string.dlg_loading_opponents);

        requestExecutor.loadUsersByTag(String.valueOf(Consts.PREF_CURREN_ROOM_NAME), new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                hideProgressDialog();
                currentOpponentsList1=result;
                Log.e("result", result.toString());
                dbManager.saveAllUsers(result, true);
                initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
                showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadUsers();
                    }
                });
            }
        });
    }

    private void initUi() {
        opponentsListView = (ListView) findViewById(R.id.list_opponents);
        logout = (ImageView) findViewById(R.id.logout);
        makeCall = (ImageView) findViewById(R.id.videocall);

        show_notifications = (ImageView) findViewById(R.id.show_notifications);
        bar_call= (ImageView) findViewById(R.id.bar_call);
        reload= (CircleImageView) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getDetailsById().execute();
                new LoginAsyncGetAll().execute();
                startLoadUsers();
            }
        });
        bar_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection = "normal";
                callto="bar";
                try {
                    DashBoardActivity.callTo1=Integer.parseInt(userTypeDetails.get("id").toString());
                    String data=userTypeDetails.get("id").toString()+"-splspli-"+"call";
                    mWebSocketClient.send(data);
                    callTo(callto);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        show_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpponentsActivity.this, NotificationActivity.class));
            }
        });
        bar_reg = (RelativeLayout) findViewById(R.id.bar_reg_lay);
        Log.e("yesss", prefs.getInt("userType", 0) + "");
        if (prefs.getInt("userType", 0) == 1) {
           bar_reg.setVisibility(View.VISIBLE);
            makeCall.setVisibility(View.VISIBLE);
        } else {
            show_notifications.setVisibility(View.GONE);
          bar_reg.setVisibility(View.GONE);
            makeCall.setVisibility(View.GONE);
        }
        bar_reg_lay = (RelativeLayout) findViewById(R.id.req_doc_parent);
        if (prefs.getInt("userType", 0) == 2) {
           bar_reg_lay.setVisibility(View.VISIBLE);
        } else {
            bar_reg_lay.setVisibility(View.GONE);
        }
        screen_title = (TextView) findViewById(R.id.screen_title);
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

        screen_title.setText("Immigrants List");
        bar_registration = (Button) findViewById(R.id.bar_registration);
        bar_remove = (CircleImageView) findViewById(R.id.bar_remove);
        notification = (CircleImageView) findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationDialog( Integer.parseInt(userTypeDetails.get("id").toString()));
            }
        });
        bar_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpponentsActivity.this, SignupActivity.class).putExtra("type", "Barrister"));
            }
        });
        bar_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OpponentsActivity.this)
                        .setMessage("Are you sure you want to delete the barrister?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DownloadFilesTask().execute();
                                Log.e("barrister", baristerQB.get("id").toString());

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        });
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        makeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedArray.size()>0) {
                    new AlertDialog.Builder(OpponentsActivity.this)
                            .setMessage("Add barrister in the call?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    connection = "normal";
                                    callto="sol";
                                    type="group";
                                    DashBoardActivity.callTo2=Integer.parseInt(userTypeDetails.get("id").toString());

                                    try {
                                        callTo(callto);
                                    }
                                    catch (Exception e)
                                    {
                                    }
                                    startService(new Intent(OpponentsActivity.this, MyService.class));
                                    startCall(true);
                                    startPermissionsActivity(false);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    connection = "normal";
                                    callto="sol";
                                    type="single";
                                    try {
                                        callTo(callto);
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
                else if(selected.size()>1)
                {
                    Toast.makeText(OpponentsActivity.this, "please select only one immigrant...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(OpponentsActivity.this, "please select a immigrant to call...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OpponentsActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new LogoutClass().clearSesson(OpponentsActivity.this);
                                OpponentsActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setCancelable(false)
                        .show();


            }
        });
    }

    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void initUsersList() {
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
           proceedInitUsersList();
    }
    public void changeTheam(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
    private void proceedInitUsersList() {
        currentOpponentsList = currentOpponentsList1;
        Log.e("barister",currentOpponentsList.toString()+"--"+currentOpponentsList.size());
        for(int i=0;i<=currentOpponentsList.size()-1;i++)
        {
            Log.e("yeee",currentOpponentsList.get(i).getFullName().toString()+"---"+(baristerName));

            if(currentOpponentsList.get(i).getFullName().toString().equalsIgnoreCase(baristerName))
            {
                baristerQB.put("id",currentOpponentsList.get(i).getId());
                baristerQB.put("name",currentOpponentsList.get(i).getFullName());
                currentOpponentsList.remove(i);
                Log.e("baristerQB",baristerQB.toString());
            }

        }

        for(int i=0;i<=currentOpponentsList.size()-1;i++)
        {
            if(currentOpponentsList.get(i).getFullName().toString().equalsIgnoreCase(prefs.getString("name",null)))
            {
                 currentOpponentsList.remove(i);
                 Log.e("currentOpponenList--->",i+"-"+currentOpponentsList.toString()+"-"+currentOpponentsList.size());
            }

        }
        opponentsAdapter = new OpponentsAdapter(this, currentOpponentsList, false);
        opponentsAdapter.setSelectedItemsCountsChangedListener(new OpponentsAdapter.SelectedItemsCountsChangedListener() {
            @Override
            public void onCountSelectedItemsChanged(int count) {
                updateActionBar(count);
            }
        });
        opponentsListView.setAdapter(opponentsAdapter);
        Log.e("loginQb",currentOpponentsList.toString());
        if (prefs.getInt("userType", 0) == 0)
        {

            finish();

        }

    }
 private void startCall(boolean isVideoCall) {

        callType="imm";
        if (opponentsAdapter.getSelectedItems().size() > Consts.MAX_OPPONENTS_COUNT) {
            Toaster.longToast(String.format(getString(R.string.error_max_opponents_count),
                    Consts.MAX_OPPONENTS_COUNT));
            return;
        }

        Log.d(TAG, "startCall()");

        opponentsList = CollectionsUtils.getIdsSelectedOpponents(opponentsAdapter.getSelectedItems());
        if(type.equalsIgnoreCase("group")) {
            opponentsList.add(Integer.parseInt(baristerQB.get("id").toString()));
        }
            onlineUser=Integer.parseInt(opponentsList.get(0).toString());
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        QBRTCSession newQbRtcSession = null;
        if (opponentsList.size() > 0) {
            Log.e("oppenents", opponentsList.toString());
            newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

            PushNotificationSender.sendPushMessage(opponentsList, prefs.getString("name", null));
            SharedPreferences.Editor editor = getSharedPreferences("ids", MODE_PRIVATE).edit();
            Log.e("conferenceType = ", opponentsList.toString() + "<--->" + prefs.getString("name", null));
            CallActivity.start(this, false);
        } else {
            Toast.makeText(this, "Please select opponents to start call...", Toast.LENGTH_SHORT).show();
        }

    }

    public void callBarister(int qbId,String qbName)
{
    callType="bar";
    ArrayList<Integer> opponentsList =new ArrayList<Integer>();
    opponentsList.add(qbId);
    onlineUser=Integer.parseInt(opponentsList.get(0).toString());
    QBRTCTypes.QBConferenceType conferenceType = true
            ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
            : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

    QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
    QBRTCSession newQbRtcSession = null;
    if (opponentsList.size() > 0) {
        Log.e("oppenents", opponentsList.toString());
        newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, qbName);

        CallActivity.start(this, false);

    } else {
        Toast.makeText(this, "Please select opponents to start call...", Toast.LENGTH_SHORT).show();
    }
}
    private void initActionBarWithSelectedUsers(int countSelectedUsers) {
        setActionBarTitle(String.format(getString(
                countSelectedUsers > 1
                        ? R.string.tile_many_users_selected
                        : R.string.title_one_user_selected),
                countSelectedUsers));
    }

    private void updateActionBar(int countSelectedUsers) {
        if (countSelectedUsers < 1) {
            initDefaultActionBar();
        } else {
            removeActionbarSubTitle();
            initActionBarWithSelectedUsers(countSelectedUsers);
        }

        invalidateOptionsMenu();
    }




    public void postAPICall(final Context context) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userTypeDetails.get("id").toString());
        Log.e("userType",userTypeDetails.get("id").toString());
        HttpDeleteWithBody request = new HttpDeleteWithBody("http://35.163.24.72:8080/VedioApp/service/user/");
        request.addHeader("Content-Type", "application/json; charset=UTF-8");
        request.addHeader("Accept", "application/json");
       request.setEntity(new StringEntity(jsonObject.toString()));
        HttpClient httpClient = new MyHttpClient(context);
        HttpResponse response = httpClient.execute(request);
        Log.e("response",response.getStatusLine().getStatusCode()+"--");
        if(response.getStatusLine().getStatusCode()==200) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userTypeDetails.clear();
                    Toast.makeText(context, "Barrister deleted successfully", Toast.LENGTH_SHORT).show();
                    initFields();
                    initDefaultActionBar();
                    initUi();
                    new getDetailsById().execute();
                    new LoginAsyncGetAll().execute();

                }
            });
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }

            try {
                Log.e("response", resultJson.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(response.getStatusLine().getStatusCode()==500)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }
    }

    public void  deleteBarister()
    {
        Log.e("connection","connection");
        URL url = null;
        try {
            url = new URL("http://35.163.24.72:8080/VedioApp/service/user/2");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty(
                    "Content-Type", "application/json" );
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private class LoginAsync1 extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OpponentsActivity.this);
            progressDialog.setMessage("loading...");
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);


                  //  postAPICall(OpponentsActivity.this);
                    deleteBarister();


            return aLong;
        }


        protected void onPostExecute(Long result) {
            progressDialog.dismiss();
        }
    }
    private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {
            String count = null;
            try {
                postAPICall(OpponentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return count;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
startLoadUsers();
        }
    }
    public void postAPICall(String strurl, final Context context) throws Exception {
        strurl = strurl.replace(" ", "%20");
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        Log.e("status code---->",response.getStatusLine().getStatusCode()+"");
        if(response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("json opponents", resultJson);
            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                JSONObject jsonObject;
                OpponentIds.clear();
                OpponentNames.clear();
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    OpponentNames.add(jsonObject.getString("name"));
                    OpponentIds.add(jsonObject.getInt("id"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
///                    Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else if(response.getStatusLine().getStatusCode()==500)
        {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("json opponents", resultJson);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgressDialog();
                    Toast.makeText(context, "server busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgressDialog();
                    Toast.makeText(context, "data not available...", Toast.LENGTH_SHORT).show();
                }
            });
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
        if(response.getStatusLine().getStatusCode()==200)
        {
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";
        while ((line = reader.readLine()) != null) {
            resultJson += line;
        }

            try {
Log.e("resultJson",resultJson);

                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                /*UserType userTypeObj = new UserType(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("email"),
                        jsonObject.getString("registeredBy"),
                        jsonObject.getString("userType"));
                userType.add(userTypeObj);*/
                    userTypeDetails.clear();
                    userTypeDetails.put("id", jsonObject.getInt("id"));
                    userTypeDetails.put("name", jsonObject.getString("name"));
                    userTypeDetails.put("email", jsonObject.getString("email"));
                    userTypeDetails.put("registeredBy", jsonObject.getInt("registeredBy"));
                    userTypeDetails.put("userType", jsonObject.getInt("userType"));
                    Log.e("userTypeDetails", userTypeDetails.get("name").toString());
                    baristerName = userTypeDetails.get("name").toString();


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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (userTypeDetails.size() != 0) {

                        bar_registration.setVisibility(View.GONE);
                        bar_after_login.setVisibility(View.VISIBLE);
                        bar_name.setText(userTypeDetails.get("name").toString());
                    } else {
                        baristerName="";
                        bar_name.setText(baristerName);
                        bar_registration.setVisibility(View.VISIBLE);
                        bar_after_login.setVisibility(View.GONE);
                    }
                }
            });
        }
        else if(response.getStatusLine().getStatusCode()==500)

        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "server is busy please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else

        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "data not available", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private class LoginAsyncGetAll extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);

            try {
                postAPICall("http://35.163.24.72:8080/VedioApp/service/user/type/0", OpponentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {

        }
    }

    private class getDetailsById extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            String type;

            if (prefs.getInt("userType", 0) == 1) {
                type = "2";
            } else {
                type = "1";
            }
            String url = "http://35.163.24.72:8080/VedioApp/service/user/type/" + type;
            Log.e("urlls",url);
            try {
                postAPICall1(url, OpponentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new getDetailsById().execute();
                    }
                });

            }
            return aLong;
        }


        protected void onPostExecute(Long result) {


        }
    }
    public void notificationDialog(final int userId)
    {
        Log.e("userId",userId+"--");

        requestDialog=new Dialog(this);
        requestDialog.setCancelable(false);
        requestDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        requestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestDialog.setContentView(R.layout.send_notification);
        msgEdt= (EditText) requestDialog.findViewById(R.id.message);
        send= (Button) requestDialog.findViewById(R.id.send1);
        close= (Button) requestDialog.findViewById(R.id.close);

        requestDialog.show();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgEdt.getText().toString().trim().length()>0) {

                    message = msgEdt.getText().toString();
                    String data=userId+"-splspli-"+"notification"+"-splspli-"+msgEdt.getText().toString()+"-splspli-"+prefs.getString("name",null);
                    mWebSocketClient.send(data);
                    msgEdt.setText("");
                    close.performClick();
                    Toast.makeText(OpponentsActivity.this, "Message has sent Succesfully...", Toast.LENGTH_SHORT).show();
                    new SendNotificationAcync().execute();
                }
                else
                {
                    Toast.makeText(OpponentsActivity.this, "Please Enter message...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
            }
        });
    }
    public class SendNotificationAcync extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject jsonObject=new JSONObject();
            SharedPreferences prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
            try {
                jsonObject.put("notification",message);
                jsonObject.put("sentBy",prefs.getInt("userId",0));
                jsonObject.put("sentDate", DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                jsonObject.put("sentTo",Integer.parseInt(userTypeDetails.get("id").toString()));
                Log.e("jsonObject",jsonObject.toString());
                // jsonObject.put("sentTo",(OpponentsActivity.OpponentNames.indexOf(QbUsersDbManager.opponentsNames.get(selectedPerson))));
                Log.e("jsonObject",jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                sendNotificationAPICall("http://35.163.24.72:8080/VedioApp/service/notifications",jsonObject.toString(), OpponentsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {




        }
    }
    public void sendNotificationAPICall(String strurl, String jsonString, final Context context) throws Exception
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
        if(response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            try {
                JSONObject jsonObject = new JSONObject(resultJson);
            } catch (Exception e) {
                e.printStackTrace();


            }
            Log.e("resultJson", resultJson);
        }
        else if (response.getStatusLine().getStatusCode()==500)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {

        }

    }
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://183.82.113.165:8085");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened");
                int userId=prefs.getInt("userId",-1);
                String data=userId+"-splspli-"+"reg";
                mWebSocketClient.send(data);
            }

            @Override
            public void onMessage(String s)
            {
                final String message = s;
                Log.e("message",message);
                createNotification(message);
            }
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void createNotification(String message) {
                try {
                    // Log.e("data", message);
                    String[] split = message.split("-splspli-");
                    //  Log.e("datata", split[3].toString() + "<--->" + split[2].toString());
                    Intent intent = new Intent(OpponentsActivity.this, NotificationActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(OpponentsActivity.this, (int) System.currentTimeMillis(), intent, 0);
                    Notification noti = new Notification.Builder(OpponentsActivity.this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("BTT Lawyer")
                            .setContentText(split[3].toString() + ":" + split[2].toString())
                            .setContentIntent(pIntent)
                            .build();
                    // Log.e("datata", split[3].toString() + "--->" + split[2].toString());

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    noti.defaults = Notification.DEFAULT_ALL;
                    notificationManager.notify(NOTIFICATION_ID++, noti);
                    //Log.e("datata", split[3].toString() + ":" + split[2].toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}