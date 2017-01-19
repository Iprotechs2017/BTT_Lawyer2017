package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.utils.UserType;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.helper.Utils;
import com.quickblox.sample.core.gcm.GooglePlayServicesHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.util.AppController;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.QBEntityCallbackImpl;
import com.VideoCalling.sample.groupchatwebrtc.utils.UsersUtils;
import com.VideoCalling.sample.groupchatwebrtc.utils.ValidationUtils;
import com.quickblox.users.model.QBUser;

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
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
Toolbar toolbar;
String TAG="response";
    String tag_json_obj = "json_obj_req";
    EditText name,mobiileNumber,emialId,password,confirmPassword;
    Spinner gender,userType;
    RadioGroup radiobutton_group;
    Button btn_signup;
    SharedPreferences.Editor editor;
    ProgressDialog pDialog;
    LinearLayout action_layout;
    List<String> genderArray =  new ArrayList<String>();
    List<String> userTypeArray =  new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView link_login,screen_titile,sol_name;
    JsonObjectRequest jsonObjReq;
    JSONObject signupResponse;
    protected GooglePlayServicesHelper googlePlayServicesHelper;
    String selectedUserType;
    String room="ipro20";
    public QBUser userForSave;
    JSONObject params;
    RelativeLayout radio_par;
    RadioButton immigrant;
    protected QBResRequestExecutor requestExecutor;
    String signupUrl="http://35.163.24.72:8080/VedioApp/service/user";
    ProgressDialog progressDialog;
    String regType="";
    int soli_id,bar_id=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(getIntent().getStringExtra("type")!=null)
        {
            regType=getIntent().getStringExtra("type").toString();
        }
Log.e("regType",regType+"--");
        initViews();
        new  getSolaciter().execute();
        new getBarrister().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void initViews()
    {

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        googlePlayServicesHelper = new GooglePlayServicesHelper();
        immigrant= (RadioButton) findViewById(R.id.lawyer);
        link_login= (TextView) findViewById(R.id.link_login);
        sol_name= (TextView) findViewById(R.id.sol_name);
        radio_par= (RelativeLayout) findViewById(R.id.radio_par);
        if(regType.equalsIgnoreCase("Barrister"))
    {
        link_login.setVisibility(View.GONE);
        immigrant.setVisibility(View.GONE);
        radio_par.setVisibility(View.GONE);
    }
        else
        {
            link_login.setVisibility(View.VISIBLE);
            radio_par.setVisibility(View.VISIBLE);
            immigrant.setVisibility(View.VISIBLE);
        }
        pDialog = new ProgressDialog(this);
        editor = getSharedPreferences("loginDetails", MODE_PRIVATE).edit();
        pDialog.setMessage("Loading...");
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        name= (EditText) findViewById(R.id.input_name);
        emialId= (EditText) findViewById(R.id.input_email);
        radiobutton_group= (RadioGroup) findViewById(R.id.radiobutton_group);
        password= (EditText) findViewById(R.id.input_password);
        mobiileNumber= (EditText) findViewById(R.id.number);
        btn_signup= (Button) findViewById(R.id.btn_signup);
/*        toolbar= (Toolbar) findViewById(R.id.toolbar);
        action_layout= (LinearLayout) findViewById(R.id.action_layout);
        action_layout.setVisibility(View.GONE);
        screen_titile= (TextView) findViewById(R.id.screen_title);
        screen_titile.setText("Signup");
        setSupportActionBar(toolbar);*/
        link_login= (TextView) findViewById(R.id.link_login);
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));

            }
        });
         btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validations();
            }
        });

    }

    public void validations()
    {
        if(ValidationUtils.isUserNameValid(this, name))
        {

        if(ValidationUtils.emailValidator(this,emialId))
        {
            if(ValidationUtils.isRoomNameValid(this, password))
            {

                if( radiobutton_group.getCheckedRadioButtonId()==-1 && !regType.equalsIgnoreCase("Barrister"))
                {
                    Toast.makeText(this, "Select user type", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    RadioButton selectedUserTypeB = (RadioButton) findViewById(radiobutton_group.getCheckedRadioButtonId());
                   if(!regType.equalsIgnoreCase("Barrister")) {
                       selectedUserType = selectedUserTypeB.getText().toString();
                       Log.e("selectedUserType", selectedUserType);
                   }
                       params=new JSONObject();
                    try {
                        params.put("name", name.getText().toString());
                        params.put("email", emialId.getText().toString());
                        params.put("password", password.getText().toString());
                        params.put("registeredBy", soli_id);
                        params.put("phoneNumber",mobiileNumber.getText().toString());
                        if(regType.equalsIgnoreCase("Barrister"))
                        {
                            params.put("userType",2);
                        }
                        else {
                            if(selectedUserType.equalsIgnoreCase("Immigrant")) {
                                params.put("userType",0);
                            }
                            else
                            {
                                params.put("userType",2);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(new NetworkCheck().isOnline(SignupActivity.this)) {

                        if(bar_id!=-1 && selectedUserType.equalsIgnoreCase("Barrister")) {
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(SignupActivity.this, "Barrister already exist...", Toast.LENGTH_SHORT).show();
    }
});
                        }
                        else
                        {
                            new LoginAsync().execute();
                        }
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                    }





                }
            }
        }

        }


    }
    public QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(name.getText().toString(),
                String.valueOf(Consts.PREF_CURREN_ROOM_NAME));
    }
    public String getCurrentDeviceId() {
        return Utils.generateDeviceId(this);
    }
    public QBUser createQBUserWithCurrentData(String userName, String chatRoomName) {
       StringifyArrayList<String> userTags = new StringifyArrayList<>();
        userTags.add(chatRoomName);

        QBUser qbUser = new QBUser();
        qbUser.setFullName(userName);
        qbUser.setLogin(getCurrentDeviceId());
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
        qbUser.setTags(userTags);
        return qbUser;
    }
    private void startSignUpNewUser(final QBUser newUser) {
        progressDialog.setMessage("loading...");
        progressDialog.show();
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        loginToChat(result);
                        new LoginAsync().execute();

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, true);
                        } else {


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
    public void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {
                if (deleteCurrentUser) {
                    removeAllUserData(result);
                } else {
                    subscribeToPushes();

                }
            }

            @Override
            public void onError(QBResponseException responseException) {
                progressDialog.dismiss();
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

    public void removeAllUserData(final QBUser user) {
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData());
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    public void setSpinner(Context context,Spinner spinner, List<String> spinnerData)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.my_spinner_style,spinnerData) {

            public View getView(int position, View convertView,ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);

                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);

                ((TextView) v).setGravity(Gravity.CENTER);

                return v;

            }

        };
        spinner.setAdapter(adapter);
    }

    /*----quickblox--------*/


    /*------------------ApI call for login----------------------*/

    public void postAPICall(String strurl, String jsonString, final Context context) throws Exception
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
Log.e("log","logggg");

        HttpResponse response = null;

            HttpClient httpClient = new MyHttpClient(context);
            response = httpClient.execute(httpPost);
        Log.e("yes",response.getStatusLine().getStatusCode()+"");
        if(response.getStatusLine().getStatusCode()==201 || response.getStatusLine().getStatusCode()==200) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            int code = response.getStatusLine().getStatusCode();
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("resultJson",resultJson);
            JSONObject jsonObject = new JSONObject(resultJson);
            Log.e("yes",jsonObject.getInt("id")+"");
//long end_time = System.nanoTime();
//double difference = (end_time - start_time) / 1e6;
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        progressDialog.dismiss();
    }
});
            try {
                if (regType.equalsIgnoreCase("Barrister")) {

                } else {

      /*              editor.putInt("userId", jsonObject.getInt("id"));
                    editor.putString("name", jsonObject.getString("name"));
                    editor.putString("phoneNumber", jsonObject.getString("phoneNumber"));
                    editor.putInt("userType", jsonObject.getInt("userType"));
                    editor.putString("email", jsonObject.getString("email"));
                    editor.commit();*/
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(context)

                                .setMessage("Succesfully Registered...!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(regType.equalsIgnoreCase("Barrister")) {
                                            startActivity(new Intent(SignupActivity.this, OpponentsActivity.class));
                                        }
                                            SignupActivity.this.finish();
                                    }
                                })

                                .show();
                    }
                });

                // Toast.makeText(context, "Succesfully Registered...!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else if(response.getStatusLine().getStatusCode()==500)
        {
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        progressDialog.dismiss();
        Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
    }
});
        }
        progressDialog.dismiss();
       // Log.e("resultJson",resultJson);
    }
    public void postAPICall2(String strurl, String jsonString, final Context context) throws Exception
    {

        strurl = strurl.replace(" ", "%20");
        HttpGet httpPost = new HttpGet(strurl);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        String resultJson = "";
        while ((line = reader.readLine()) != null) {
            resultJson += line;
        }
        Log.e("name",resultJson.toString());
        try {
            JSONArray jsonArray=new JSONArray(resultJson);
            for(int i=0;i<=jsonArray.length()-1;i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(0);
            //    UserType userTypeObj = new UserType(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("email"), jsonObject.getString("registeredBy"), jsonObject.getString("userType"));
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           Log.e("name",jsonObject.getString("name").toString());
                           soli_id=jsonObject.getInt("id");
                          // sol_name.setText(jsonObject.getString("name").toString());
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               });
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
              /*  if(userType.size()!=0)
                {

                    bar_registration.setVisibility(View.GONE);
                    bar_after_login.setVisibility(View.VISIBLE);
                    bar_name.setText(userType.get(0).getName().toString());
                }
                else
                {
                    bar_registration.setVisibility(View.VISIBLE);
                    bar_after_login.setVisibility(View.GONE);
                }*/
            }
        });


    }
    public void postAPICall3(String strurl, String jsonString, final Context context) throws Exception
    {

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
            Log.e("name", resultJson.toString());
            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //    UserType userTypeObj = new UserType(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("email"), jsonObject.getString("registeredBy"), jsonObject.getString("userType"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("name", jsonObject.getString("name").toString());
                                bar_id = jsonObject.getInt("id");
                                //sol_name.setText(jsonObject.getString("name").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
              /*  if(userType.size()!=0)
                {

                    bar_registration.setVisibility(View.GONE);
                    bar_after_login.setVisibility(View.VISIBLE);
                    bar_name.setText(userType.get(0).getName().toString());
                }
                else
                {
                    bar_registration.setVisibility(View.VISIBLE);
                    bar_after_login.setVisibility(View.GONE);
                }*/
                }
            });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (regType.equalsIgnoreCase("Barrister")) {
            startActivity(new Intent(SignupActivity.this,OpponentsActivity.class));
            SignupActivity.this.finish();
        }

    }

    private class LoginAsync extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject jsonObject=new JSONObject();
Log.e("params.toString()",params.toString());
            try {
                postAPICall(signupUrl, params.toString(), SignupActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
    private class getSolaciter extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject jsonObject=new JSONObject();

            try {
                postAPICall2("http://35.163.24.72:8080/VedioApp/service/user/type/1", jsonObject.toString(), SignupActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
    private class getBarrister extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong= Long.valueOf(1);
            JSONObject jsonObject=new JSONObject();

            try {
                postAPICall3("http://35.163.24.72:8080/VedioApp/service/user/type/2", jsonObject.toString(), SignupActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }
}
