package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.VideoCalling.sample.groupchatwebrtc.R;

public class IndividualVideocallActivity extends Activity {
    int selectedVideoLog;
    ImageView close_window;
    TextView callName,callerName,participents,day,startTime,endTime,duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_videocall);
        this.setFinishOnTouchOutside(false);//----->this wont allow dialog to close when clicked out side
        close_window= (ImageView) findViewById(R.id.close_window);
        close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndividualVideocallActivity.this.finish();
            }
        });
        selectedVideoLog= getIntent().getIntExtra("position",-1);
        callerName= (TextView) findViewById(R.id.callerName);
        callerName.setText(":"+DashBoardActivity.videoLogs.get(selectedVideoLog).getCallFromName().toString());
        callName= (TextView) findViewById(R.id.callName);
        participents= (TextView) findViewById(R.id.participents);
        if(DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo1Name().toString().equalsIgnoreCase(DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo2Name()))
        {
            participents.setText(":" +DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo1Name().toString());
            callName.setText(DashBoardActivity.videoLogs.get(selectedVideoLog).getCallFromName().toString()+"&"+DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo1Name().toString());
        }
        else
        {
            participents.setText(":" +DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo1Name().toString()+","+DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo2Name().toString());
            callName.setText(DashBoardActivity.videoLogs.get(selectedVideoLog).getCallFromName().toString()+","+DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo1Name().toString()+"&"+DashBoardActivity.videoLogs.get(selectedVideoLog).getCallTo2Name().toString());
        }

        day= (TextView) findViewById(R.id.day);
        day.setText(":" + DashBoardActivity.videoLogs.get(selectedVideoLog).getDay() + "");
        startTime= (TextView) findViewById(R.id.startTime);
        startTime.setText(":"+DashBoardActivity.videoLogs.get(selectedVideoLog).getStartTime() + "");
        endTime= (TextView) findViewById(R.id.endTime);
        endTime.setText(":"+DashBoardActivity.videoLogs.get(selectedVideoLog).getEndTime()+"");
        duration= (TextView) findViewById(R.id.duration);
        duration.setText(":"+DashBoardActivity.videoLogs.get(selectedVideoLog).getDuration().split(" ")[0]+"");


    }
}
