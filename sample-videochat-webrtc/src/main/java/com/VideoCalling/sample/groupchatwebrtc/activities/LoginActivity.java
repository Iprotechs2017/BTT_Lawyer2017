package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.VideoCalling.sample.groupchatwebrtc.services.NotificationService;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.helper.Utils;
import com.quickblox.sample.core.utils.KeyboardUtils;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.util.AppController;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.QBEntityCallbackImpl;
import com.VideoCalling.sample.groupchatwebrtc.utils.UsersUtils;
import com.VideoCalling.sample.groupchatwebrtc.utils.ValidationUtils;
import com.quickblox.users.model.QBUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tereha on 12.04.16.
 */
public class LoginActivity extends BaseActivity {

    public String TAG = LoginActivity.class.getSimpleName();
    JSONObject loginResponse;
    SharedPreferences prefs ;
    SharedPreferences.Editor editor,qbuserId;
    public EditText userNameEditText,input_email,input_password;
    public EditText chatRoomNameEditText;
    Toolbar toolbar;
    Button btn_login;
    TextView screen_title;
    LinearLayout action_layout;
    private QbUsersDbManager dbManager;
    public QBUser userForSave;
    RelativeLayout parent;
    String tag_json_obj = "json_obj_req";
    TextView link_signup;
    ProgressDialog pDialog;
    String loginApi = "http://35.163.24.72:8080/VedioApp/service/user/login";
    JsonObjectRequest jsonObjReq;
    public RequestQueue rq;
    String email,pass;
    int userCaseStatus=1;
    String room="ipro20";
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getCurrentFocus()!=null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(LoginActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (LoginActivity.this, Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(findViewById(android.R.id.content), "Please enable Microphone and Storage permissions.",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                        1);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        1);
            }

        } else {
           /* if(!getIntent().getExtras().getBoolean(Consts.EXTRA_IS_INCOMING_CALL)) {

            //    startRecording();

            }*/
        }
        setContentView(R.layout.activity_sign_in);
        getWindow().setBackgroundDrawableResource(R.drawable.sign_up_bg) ;
     //  startService(new Intent(LoginActivity.this, APIServices.class));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editor = getSharedPreferences("loginDetails", MODE_PRIVATE).edit();
        qbuserId = getSharedPreferences("QB", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        link_signup=(TextView) findViewById(R.id.link_signup);
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        /*toolbar= (Toolbar) findViewById(R.id.toolbar);
        screen_title= (TextView) findViewById(R.id.screen_title);
        screen_title.setText("Signin");*/
        rq = Volley.newRequestQueue(this);
        parent= (RelativeLayout) findViewById(R.id.parent);
        pDialog = new ProgressDialog(this);
        startLoadUsers();
        btn_login= (Button) findViewById(R.id.btn_login);
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        userNameEditText = (EditText) findViewById(R.id.input_email);
        userNameEditText.addTextChangedListener(new LoginEditTextWatcher(userNameEditText));
        chatRoomNameEditText = (EditText) findViewById(R.id.input_password);
        chatRoomNameEditText.addTextChangedListener(new LoginEditTextWatcher(chatRoomNameEditText));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userNameEditText.getText().toString();
                pass = chatRoomNameEditText.getText().toString();
                if(!isEnteredUserNameValid()) {
                   // Toast.makeText(LoginActivity.this, "Enter valid emailId...", Toast.LENGTH_SHORT).show();
                }
                else if(pass.trim().length()==0)
                {
                    Toast.makeText(LoginActivity.this, "Enter password...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (new NetworkCheck().isOnline(LoginActivity.this)) {
                        new LoginAsync().execute();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        //initUI();
      /* */
    }
    private void startLoadUsers() {
       // showProgressDialog(R.string.dlg_loading_opponents);

        requestExecutor.loadUsersByTag(String.valueOf(Consts.PREF_CURREN_ROOM_NAME), new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                dbManager.saveAllUsers(result, true);

            }

            @Override
            public void onError(QBResponseException responseException) {
                //hideProgressDialog();
                showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadUsers();
                    }
                });
            }
        });
    }
    public void chekCaseStaus(int status,JSONObject responseObject) throws JSONException {
        if(status==0)
        {
            editor.putInt("userId", responseObject.getInt("id"));
            editor.putString("name", responseObject.getString("name"));
            editor.putString("phoneNumber", responseObject.getString("phoneNumber"));
            editor.putInt("userType", responseObject.getInt("userType"));
            editor.putString("email", responseObject.getString("email"));
            editor.commit();

            //       displayUserCaseStatus("Your case is in pending status");
            stopService(new Intent(this, NotificationService.class));
            startService(new Intent(this, NotificationService.class));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    startSignUpNewUser(createUserWithEnteredData());
                }
            });
        }
        else if(status==1)
        {
            displayUserCaseStatus("Your case has approved,please contact solicitor.");
            stopService(new Intent(this, NotificationService.class));
            startService(new Intent(this, NotificationService.class));

        }
        else if(status==2)
        {
            displayUserCaseStatus("Your case has rejected,please contact solicitor.");
            stopService(new Intent(this, NotificationService.class));
            startService(new Intent(this, NotificationService.class));
        }


    }
    public void displayUserCaseStatus(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(message)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LoginActivity.this.finish();
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.root_view_login_activity);
    }

    public void initUI() {
      //  setActionBarTitle(R.string.title_login_activity);
        userNameEditText = (EditText) findViewById(R.id.input_email);
        userNameEditText.addTextChangedListener(new LoginEditTextWatcher(userNameEditText));

        chatRoomNameEditText = (EditText) findViewById(R.id.chat_room_name);
        chatRoomNameEditText.addTextChangedListener(new LoginEditTextWatcher(chatRoomNameEditText));
       /* toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));*/

      //  login= (Button) findViewById(R.id.signin);
     //   signup=(TextView) findViewById(R.id.link_signup);
       /* login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this,OpponentsActivity.class));
                if (isEnteredUserNameValid() && isEnteredRoomNameValid()) {
                    hideKeyboard();
                    startSignUpNewUser(createUserWithEnteredData());
                }
            }
        });*/
       /* signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));

            }
        });*/
        startSignUpNewUser(createUserWithEnteredData());
    }
    private void createSession() {
        App.getInstance().getQbResRequestExecutor().createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle params) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startSignUpNewUser(createUserWithEnteredData());
                    }
                });


            }

            @Override
            public void onError(QBResponseException e) {
                showSnackbarError(null, R.string.splash_create_session_error, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSession();
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_login_user_done:
              /*  if (isEnteredUserNameValid() && isEnteredRoomNameValid()) {
                    hideKeyboard();
                    startSignUpNewUser(createUserWithEnteredData());
                }*/
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isEnteredRoomNameValid() {
        return ValidationUtils.isRoomNameValid(this, chatRoomNameEditText);
    }

    public boolean isEnteredUserNameValid() {
        return ValidationUtils.emailValidator(this, userNameEditText);
    }

    public void hideKeyboard() {
        KeyboardUtils.hideKeyboard(userNameEditText);
        KeyboardUtils.hideKeyboard(chatRoomNameEditText);
    }

    private void startSignUpNewUser(final QBUser newUser) {
        /*showProgressDialog(R.string.loading);*/
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        loginToChat(result);
                       Log.e("QB-ID", result.getId()+"");
                        qbuserId.putInt("qbUserId",result.getId());
                        qbuserId.commit();
                        SharedPrefsHelper.getInstance().saveQbUser(result);
                        if (prefs.getInt("userType", -1) == -1) {
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        } else if(prefs.getInt("userType", 0) == 0) {
                            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                        }
                        else
                        {
                            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                              //  Toast.makeText(LoginActivity.this, "Enter Valid Credentials...!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, false);

                        //new LoginAsync().execute();
                        } else {
                            hideProgressDialog();
                            //hideProgressDialog();
                            Toaster.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }

    public void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);

        userForSave = qbUser;
        startLoginService(qbUser);
    }

    public void startOpponentsActivity() {
        if (prefs.getInt("userType", -1) == -1) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        } else if(prefs.getInt("userType", 0) == 0) {
            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
        }
        else
        {
            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
        }
      /*  stopService(new Intent(LoginActivity.this, NotificationService.class));
        startService(new Intent(LoginActivity.this, NotificationService.class));*/
        finish();
    }

    public void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    public QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(prefs.getString("name", null),
                String.valueOf(Consts.PREF_CURREN_ROOM_NAME));
    }

    public QBUser createQBUserWithCurrentData(String userName, String chatRoomName) {
        QBUser qbUser = null;
        /*if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(chatRoomName)) {

        }*/
        StringifyArrayList<String> userTags = new StringifyArrayList<>();
        userTags.add(chatRoomName);
        qbUser = new QBUser();
        qbUser.setFullName(userName);
        qbUser.setLogin(getCurrentDeviceId());
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
        qbUser.setTags(userTags);

        return qbUser;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            hideProgressDialog();
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);

                signInCreatedUser(userForSave, false);
            } else {
                Toaster.longToast(getString(R.string.login_chat_login_error) + errorMessage);
                userNameEditText.setText(userForSave.getFullName());
                chatRoomNameEditText.setText(userForSave.getTags().get(0));
            }
        }
    }

    public void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {
                qbuserId.putInt("qbUserId",result.getId());
                Log.e("result.getId()",result.getId()+"");
                qbuserId.commit();
                if (deleteCurrentUser) {
                    removeAllUserData(result);
                    SharedPrefsHelper.getInstance().saveQbUser(result);
                } else {
                    SharedPrefsHelper.getInstance().saveQbUser(result);
                    subscribeToPushes();
                    loginToChat(result);
                    startOpponentsActivity();
                }
                Toast.makeText(LoginActivity.this, "Login successfull...!", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    public void removeAllUserData(final QBUser user) {
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData());
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    public void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }

    public void subscribeToPushes() {
        if (googlePlayServicesHelper.checkPlayServicesAvailable(this)) {
            Log.d(TAG, "subscribeToPushes()");
            googlePlayServicesHelper.registerForGcm(Consts.GCM_SENDER_ID);
        }
    }

    public String getCurrentDeviceId() {
        return Utils.generateDeviceId(this);
    }

    public class LoginEditTextWatcher implements TextWatcher {
        public EditText editText;

        public LoginEditTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editText.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    /*------------------ApI call for login----------------------*/
    public JSONObject postAPICall(String strurl, String jsonString, final Context context) throws Exception
    {
        JSONObject responseObject=new JSONObject();
        JSONObject responseObject1 = null;

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
        Log.e("status code --  ->",response.getStatusLine().getStatusCode()+"---");
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";

        while ((line = reader.readLine()) != null)
        {
            resultJson += line;
        }

        try {
            if(response.getStatusLine().getStatusCode() == 200) {
                responseObject=new JSONObject(resultJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   hideProgressDialog();
                    }
                });
                try {


                    chekCaseStaus(responseObject.getInt("status"),responseObject);
                    /*if(responseObject.getInt("userType")==0) {

                    }
                    else {
                        stopService(new Intent(this, NotificationService.class));
                        startService(new Intent(this, NotificationService.class));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                startSignUpNewUser(createUserWithEnteredData());
                            }
                        });
                    }*/

                } catch (Exception e) {
                    hideProgressDialog();
                    e.printStackTrace();
                }

            }
            if(response.getStatusLine().getStatusCode()==500)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Snackbar snackbar = Snackbar
                                .make(parent, "server is busy try again...", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(new NetworkCheck().isOnline(LoginActivity.this)) {
                                            new LoginAsync().execute();
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                ;
                        snackbar.setDuration(Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
            else if(response.getStatusLine().getStatusCode()==401) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Toast.makeText(LoginActivity.this, "Enter valid credentials...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseObject;

    }
    public class LoginAsync extends AsyncTask<URL, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(R.string.loading);
        }

        protected JSONObject doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject  responseObject,resultObject = null;
            JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("email",email);

                jsonObject.put("password",pass);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                /*for(int i=0;i<=4;i++)
                {*/
                    resultObject=postAPICall(loginApi,jsonObject.toString(), LoginActivity.this);
                    Log.e("resultJson",resultObject.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            startService(new Intent(LoginActivity.this, NotificationService.class));

        }
    }
}
