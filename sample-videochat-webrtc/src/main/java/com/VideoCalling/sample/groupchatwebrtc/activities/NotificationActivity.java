package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.util.LogoutClass;
import com.VideoCalling.sample.groupchatwebrtc.util.MyHttpClient;
import com.VideoCalling.sample.groupchatwebrtc.util.NetworkCheck;
import com.mancj.slideup.SlideUp;

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
    ArrayList msgs = new ArrayList();
    ArrayList senderNames = new ArrayList();
    ArrayList sentDates = new ArrayList();
    View slideView;
    ProgressDialog progressDialog;
    SharedPreferences prefs;
    LinearLayout action_layout;
    HashMap userTypeDetailss = new HashMap();
    TextView title;
    TextView userName;
    TextView view_msg, time_stamp;
    SlideUp slideUp;
    Toolbar toolbar;
    RelativeLayout header1;
    ImageView close_window_send_notification;
    Dialog notification_dialog;
    de.hdodenhof.circleimageview.CircleImageView call, notification, logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notification_dialog = new Dialog(this);
        notification_dialog.setCancelable(false);
        notification_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notification_dialog.setContentView(R.layout.notification_pop_up);
        close_window_send_notification = (ImageView) notification_dialog.findViewById(R.id.close_window);
        header1 = (RelativeLayout) notification_dialog.findViewById(R.id.header1);
        view_msg = (TextView) notification_dialog.findViewById(R.id.view_msg);
        userName = (TextView) notification_dialog.findViewById(R.id.userName);
        time_stamp = (TextView) notification_dialog.findViewById(R.id.time_stamp);
        close_window_send_notification.setVisibility(View.VISIBLE);
        header1.setVisibility(View.VISIBLE);
        view_msg.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);
        time_stamp.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_w));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationActivity.this.finish();
            }
        });

        close_window_send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_dialog.dismiss();

            }
        });
        slideView = findViewById(R.id.slideView);
        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
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
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        if (new NetworkCheck().isOnline(NotificationActivity.this)) {

            new getDetailsById().execute();

            LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(this);
            /*logout.setOnClickListener(new View.OnClickListener() {
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
            });*/
            recyclerView.setLayoutManager(recylerViewLayoutManager);
        } else {
            Toast.makeText(NotificationActivity.this, "Please check your internet or Wifi connections...!", Toast.LENGTH_SHORT).show();
        }


/*        if (prefs.getInt("userType", -1) == 1) {
            notification.setVisibility(View.VISIBLE);
        }
        else
        {
            notification.setVisibility(View.GONE);
        }*/


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
                            view_msg.setText(msgs.get(getAdapterPosition()).toString());
                            time_stamp.setText(sentDates.get(getAdapterPosition()).toString());
                            userName.setText(senderNames.get(getAdapterPosition()).toString());
                            notification_dialog.show();
                            Log.e("ok", "ok" + senderNames.get(getAdapterPosition()).toString());
                            // showMessage.show();
                            // slideUp.animateIn();

                        } catch (Exception e) {
                            e.printStackTrace();
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
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.msg.setText(msgs.get(position).toString());
            holder.sentDate.setText(sentDates.get(position).toString());
            holder.senderName.setText(senderNames.get(position).toString());
        }

        @Override
        public int getItemCount() {

            return msgs.size();
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
        if (response.getStatusLine().getStatusCode() == 200) {
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
                progressDialog.dismiss();
            }
        } else if (response.getStatusLine().getStatusCode() == 500) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Toast.makeText(context, "server is busy try again...", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (response.getStatusLine().getStatusCode() == 204) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Notifications not available...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Log.e("result", resultJson);
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Long doInBackground(URL... urls) {
            Long aLong = Long.valueOf(1);


            try {
                if (prefs.getInt("userType", -1) == 0) {

                    //  postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/"+prefs.getInt("userId",-1), NotificationActivity.this);
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + prefs.getInt("userId", -1), NotificationActivity.this);
                } else {
                    postAPICall("http://35.163.24.72:8080/VedioApp/service/notifications/getTo/userid/" + DashBoardActivity.selectedImmigrantId, NotificationActivity.this);
                }

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
        if (response.getStatusLine().getStatusCode() == 200) {
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
                        progressDialog.dismiss();
                               Toast.makeText(context, "Something went wrong try again...!", Toast.LENGTH_SHORT).show();
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

    private class getDetailsById extends AsyncTask<URL, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
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
            for (int i = 0; i <= 2; i++) {
                String url = "http://35.163.24.72:8080/VedioApp/service/user/type/" + i;
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

            progressDialog.dismiss();
        }
    }

    public void changeTheam(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
}
