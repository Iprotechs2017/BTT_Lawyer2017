package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.adapters.OpponentsAdapter;
import com.VideoCalling.sample.groupchatwebrtc.db.QbUsersDbManager;
import com.VideoCalling.sample.groupchatwebrtc.util.AndroidMultiPartEntity;
import com.VideoCalling.sample.groupchatwebrtc.util.FilePath;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.VideoCalling.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.PushNotificationSender;
import com.VideoCalling.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class ClientAfterLoginActivity extends BaseActivity {
    public static int solaciterId;
    ImageView logout, makeCall;
    TextView screen_title, sol_name, welcom;
    Toolbar toolbar;
    Button uplode, submit;
    ListView selected_files;
    ProgressDialog progressDialog;
    Dialog progress;
    long totalSize = 0;
    WebSocketClient mWebSocketClient;
    ProgressBar progressBar;
    ArrayList<Integer> opponentsList =new ArrayList<Integer>();
    FileAdapter fileAdapter;
    private QbUsersDbManager dbManager;
    int documentId;
    private ArrayList<QBUser> currentOpponentsList;
    ArrayList selectedFiles = new ArrayList();
    String FILE_UPLOAD_URL = "http://192.168.0.12/AndroidFileUpload/fileUpload.php";
    String url;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    SharedPreferences QbPrefs;
    ArrayList paths = new ArrayList();
    TextView info;
    private TextView txtPercentage;
    private QBUser currentUser;
    private String filePath = null;
             Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_after_login);
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        progress = new Dialog(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);
        QbPrefs=getSharedPreferences("sol_qb_id", MODE_PRIVATE);
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        info= (TextView) findViewById(R.id.info);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.file_uplode_progress_dialog);
        connectWebSocket();
        progress.setCancelable(false);
        startLoadUsers();
        progressBar = (ProgressBar) progress.findViewById(R.id.progressBar);
        txtPercentage = (TextView) progress.findViewById(R.id.txtPercentage);
        selected_files = (ListView) findViewById(R.id.selected_files);
        sol_name = (TextView) findViewById(R.id.sol_name);
        new getSolaciter().execute();
/*
        welcom = (TextView) findViewById(R.id.welcom);
        if (!prefs.getString("name", "test").equalsIgnoreCase("test")) {
            welcom.setText("Welcome " + prefs.getString("name", "test"));
        }
        screen_title = (TextView) findViewById(R.id.screen_title);*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Upload Documents");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_w));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFiles.size() > 0) {
                    new AlertDialog.Builder(ClientAfterLoginActivity.this)
                            .setMessage("Upload the selected files")
                            .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                submit.performClick();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ClientAfterLoginActivity.this.finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    }
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.immigrant_theam_color));
        changeTheam(R.color.immigrant_theam_color);
        /*ImageView show_notifications = (ImageView) findViewById(R.id.show_notifications);
        show_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientAfterLoginActivity.this, NotificationActivity.class));
            }
        });*/
        intent = new Intent();
//        screen_title.setText("Upload Documents");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1
        );
        currentUser = sharedPrefsHelper.getQbUser();
        logout = (ImageView) findViewById(R.id.logout);
        uplode = (Button) findViewById(R.id.uplode);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFiles.size() > 0) {
                    if (new NetworkCheck().isOnline(ClientAfterLoginActivity.this)) {
                        new LoginAsync().execute();
                    } else {
                        Toast.makeText(ClientAfterLoginActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClientAfterLoginActivity.this, "Please select file to uplode...!", Toast.LENGTH_SHORT).show();
                }
            }
        });
       /* makeCall = (ImageView) findViewById(R.id.videocall);
        makeCall.setVisibility(View.GONE);
        makeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall(true);
                startPermissionsActivity(false);
            }
        });
        screen_title.setText("Upload Documents");*/
        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ClientAfterLoginActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new LogoutClass().clearSesson(ClientAfterLoginActivity.this);
                                ClientAfterLoginActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });*/
        uplode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Files To Upload"), 121);
            }
        });
        fileAdapter = new FileAdapter(this, selectedFiles);
        selected_files.setAdapter(fileAdapter);
        fileAdapter.notifyDataSetChanged();
    }
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
        menuInflater.inflate(R.menu.immigrant_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.attach:
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Files To Upload"), 121);
                return true;

            case R.id.send:


                if (selectedFiles.size() > 0) {
                    if (new NetworkCheck().isOnline(ClientAfterLoginActivity.this)) {
                        new LoginAsync().execute();
                    } else {
                        Toast.makeText(ClientAfterLoginActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClientAfterLoginActivity.this, "Please select file to uplode...!", Toast.LENGTH_SHORT).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    filePath = FilePath.getPath(this, selectedImageUri);
                    Log.e("filePath", filePath);
                    if (selectedFiles.contains(filePath.split("/")[filePath.split("/").length - 1])) {
                        Toast.makeText(ClientAfterLoginActivity.this, "File Already Selected", Toast.LENGTH_SHORT).show();
                    } else {
                        String fileType=filePath.toString().substring(filePath.toString().lastIndexOf(".")+1);
                        if(fileType.equalsIgnoreCase("xlsx")
                                ||fileType.equalsIgnoreCase("xls")
                                || fileType.equalsIgnoreCase("docx")
                                || fileType.equalsIgnoreCase("doc")
                                || fileType.equalsIgnoreCase("pdf")
                                || fileType.equalsIgnoreCase("pptx")
                                || fileType.equalsIgnoreCase("ppt")
                                || fileType.equalsIgnoreCase("png")
                                || fileType.equalsIgnoreCase("jpg")
                                || fileType.equalsIgnoreCase("jpeg")
                                ) {
                            File file = new File(filePath);
                            Float dat = (Float.parseFloat(file.length() + "") / (1024)) / 1000;
                          //  Toast.makeText(ClientAfterLoginActivity.this, dat+"ss", Toast.LENGTH_SHORT).show();
                            if (dat > 5) {
                                Toast.makeText(ClientAfterLoginActivity.this, "File size shouldn't be more than 5MB ...", Toast.LENGTH_SHORT).show();
                            } else {
                                paths.add(filePath);
                                selectedFiles.add(filePath.split("/")[filePath.split("/").length - 1]);
                                fileAdapter = new FileAdapter(this, selectedFiles);
                                selected_files.setAdapter(fileAdapter);
                                fileAdapter.notifyDataSetChanged();
                                if(selectedFiles.size()>0)
                                {
                                    info.setVisibility(View.GONE);
                                }
                                else
                                {
                                    info.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                        else
                        {
                            Toast.makeText(ClientAfterLoginActivity.this, "selected file is not acceptable...", Toast.LENGTH_SHORT).show();
                        }
                                            }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(ClientAfterLoginActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    public void postAPICall(String strurl, String jsonString, Context context) throws Exception {
        fileUplode(filePath);
    }

    @Override
    protected void onDestroy() {
        DashBoardActivity.onResume="yes";
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        DashBoardActivity.onResume="yes";
        super.onBackPressed();
    }

    public void fileUplode(String filePath) {
        String ex = paths.get(0).toString().split("/")[filePath.split("/").length - 1].toString();
        Log.e("ex", ex.replaceAll("\\s+", "").split(".").length + "--" + ex);
       Log.e("paths",paths.toString());
        String type=paths.get(0).toString().substring(paths.get(0).toString().lastIndexOf(".")+1);
       Log.e("type",type);
        String urlServer = "http://35.163.24.72:8080/VedioApp/service/uploadfile/upload/name/" + ex.replaceAll("\\s+", "").split("\\.")[0] + "/type/" +type + "/uploadedby/" + prefs.getInt("userId", -1) + "/uploadedto/" + solaciterId;
        Log.e("urlServer", urlServer);
        try {

            HttpPost postRequest = new HttpPost(urlServer);
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                        }
                    });
            File sourceFile = new File(paths.get(0).toString());

            entity.addPart("uploadFile", new FileBody(sourceFile));
            //Log.e("filePath","filePath - "+filePath);
            postRequest.setEntity(entity);
            HttpClient httpclient = new MyHttpClient(ClientAfterLoginActivity.this);
            final HttpResponse response = httpclient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            final StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            Log.e("response", response.getStatusLine().getStatusCode() + "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                documentId = Integer.parseInt(sb.toString());
                                new ShareDoc().execute();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                hideProgressDialog();
                                Toast.makeText(ClientAfterLoginActivity.this, "unable to upload file try again...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    hideProgressDialog();
                    Toast.makeText(ClientAfterLoginActivity.this, "unable to upload file try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void postAPICall2(String strurl, String jsonString, final Context context) throws Exception {
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
            Log.e("name", resultJson.toString());
            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                solaciterId = jsonObject.getInt("id");
                                Log.e("name", jsonObject.getString("name").toString());
                                sol_name.setText(jsonObject.getString("name").toString());
                                if(QbPrefs.getInt("qbId",-1)==-1) {
                                    startLoadUsers();
                                }
                                else
                                {
                                    opponentsList.add(QbPrefs.getInt("qbId",-1));
                                }

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
                        Toast.makeText(context, "Unable to get solicitor details,please try again...!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void postAPICall1(String strurl, String jsonString, final Context context) throws Exception {

        strurl = strurl.replace(" ", "%20");
    Log.e("Share url",strurl);
        HttpPost httpPost = new HttpPost(strurl);
        if (jsonString != null) {
            StringEntity entity = new StringEntity(jsonString);
            httpPost.setEntity(entity);
        }
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");


        HttpResponse response = null;
        HttpClient httpClient = new MyHttpClient(context);
        response = httpClient.execute(httpPost);
        Log.e("shre", response.getStatusLine().getStatusCode() + "--");
        if (response.getStatusLine().getStatusCode() == 201) {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String resultJson = "";
            while ((line = reader.readLine()) != null) {
                resultJson += line;
            }
            Log.e("resultJson--share--->",resultJson);
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        selectedFiles.remove(0);
                        paths.remove(0);
                        fileAdapter = new FileAdapter(ClientAfterLoginActivity.this, selectedFiles);
                        selected_files.setAdapter(fileAdapter);
                        fileAdapter.notifyDataSetChanged();
                        if (selectedFiles.size() > 0 && paths.size() > 0) {
                            new LoginAsync().execute();
                        } else {
                            progressDialog.dismiss();
                            //Toast.makeText(ClientAfterLoginActivity.this, "All files are uploded successfully...!", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(ClientAfterLoginActivity.this)
                                    .setMessage("All files are uploded successfully")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                          //  new LogoutClass().clearSesson(ClientAfterLoginActivity.this);
                                            String data=DashBoardActivity.solicitor.get("id")+""+"-splspli-"+"notification"+"-splspli-"+prefs.getString("name","")+" shared the documents..."+"-splspli-"+prefs.getString("name",null);
                                            mWebSocketClient.send(data);
                                            DashBoardActivity.onResume="yes";
                                            ClientAfterLoginActivity.this.finish();
                                        }
                                    })

                                    .setCancelable(false)
                                    .show();
                        }
                       // Toast.makeText(context, "File Successfully Shared...!", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        progress.dismiss();
                        hideProgressDialog();
                        Toast.makeText(context, "server is busy try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                    hideProgressDialog();
                    progressDialog.dismiss();
                    Toast.makeText(context, "server is busy try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        JSONObject response;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(paths.get(0).toString());
                entity.addPart("image", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                response = new JSONObject(result);
                if (response.getString("message").equalsIgnoreCase("File uploaded successfully!")) {
                    Toast.makeText(ClientAfterLoginActivity.this, "File uploded successfully...", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    selectedFiles.remove(0);
                    paths.remove(0);
                    fileAdapter = new FileAdapter(ClientAfterLoginActivity.this, selectedFiles);
                    selected_files.setAdapter(fileAdapter);
                    fileAdapter.notifyDataSetChanged();
                    if (selectedFiles.size() > 0 && paths.size() > 0) {
                        Toast.makeText(ClientAfterLoginActivity.this, "All files are uploded successfully...!", Toast.LENGTH_SHORT).show();
                        new UploadFileToServer().execute();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    class FileAdapter extends ArrayAdapter {
        Context context;
        ArrayList selectedFiles;

        public FileAdapter(Context context, ArrayList selectedFiles) {
            super(context, R.layout.uplode_file_list, R.id.filename, selectedFiles);
            this.selectedFiles = selectedFiles;
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.uplode_file_list, null);
            TextView fileName = (TextView) view.findViewById(R.id.filename);
            ImageView removeFile = (ImageView) view.findViewById(R.id.remove_file);
            fileName.setText(selectedFiles.get(position).toString());
            removeFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectedFiles.remove(position);
                            paths.remove(position);
                            fileAdapter = new FileAdapter(ClientAfterLoginActivity.this, selectedFiles);
                            selected_files.setAdapter(fileAdapter);
                            fileAdapter.notifyDataSetChanged();
                            if(selectedFiles.size()>0)
                            {
                                info.setVisibility(View.GONE);
                            }
                            else
                            {
                                info.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }
            });
            return view;
        }

    }

    private class LoginAsync extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            JSONObject jsonObject = new JSONObject();
            try {
                postAPICall(url, jsonObject.toString(), ClientAfterLoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }


        protected void onPostExecute(Long result) {
        }
    }

    private class getSolaciter extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);
            JSONObject jsonObject = new JSONObject();

            try {
                postAPICall2("http://35.163.24.72:8080/VedioApp/service/user/type/1", jsonObject.toString(), ClientAfterLoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }

        protected void onPostExecute(Long result) {
        }
    }

    private class ShareDoc extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ClientAfterLoginActivity.this);
            progressDialog.setMessage("loading...");
        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("documentId", documentId);
                jsonObject.put("sharedBy", prefs.getInt("userId", 0));
                jsonObject.put("docOwner", prefs.getInt("userId", 0));
                jsonObject.put("sharedDate", DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                jsonObject.put("sharedTo", solaciterId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("jsonObject", jsonObject.toString());
            try {
                postAPICall1("http://35.163.24.72:8080/VedioApp/service/DocumentShare", jsonObject.toString(), ClientAfterLoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return aLong;
        }

        protected void onPostExecute(Long result) {

        }
    }
    //--Load quickblox users--//
    private void startLoadUsers() {
        //showProgressDialog(R.string.dlg_loading_opponents);
        requestExecutor.loadUsersByTag(String.valueOf(Consts.PREF_CURREN_ROOM_NAME), new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                hideProgressDialog();

                Log.e("result", result.toString());
                dbManager.saveAllUsers(result, true);
                //initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                startLoadUsers();
            }
        });

    }
//-- initialize users --//

    private void initUsersList() {
//      checking whether currentOpponentsList is actual, if yes - return
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }

        proceedInitUsersList();


    }
    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void proceedInitUsersList() {
/*     for(int i=0;i<=currentOpponentsList.size()-1;i++)
        {
            if(currentOpponentsList.get(i).getFullName().toString().equalsIgnoreCase(sol_name.getText().toString()))
            {
                editor = getSharedPreferences("sol_qb_id", MODE_PRIVATE).edit();
                editor.putInt("qbId",currentOpponentsList.get(i).getId());
                opponentsList.add(currentOpponentsList.get(i).getId());
            }


        }*/



    }
//--call method--//
private void startCall(boolean isVideoCall) {
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
        CallActivity.start(this, false);

    } else {
        Toast.makeText(this, "Solicitor not available...", Toast.LENGTH_SHORT).show();
    }

}
    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
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
