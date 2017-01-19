package com.VideoCalling.sample.groupchatwebrtc.utils;

/**
 * Created by Harishma Velagala on 11-01-2017.
 */
public class VideoLogsModel {

    int id;
    int callFrom;
  String callFromName;
    String startTime;
    String day;
    String callTo1Name;
    String callTo2Name;
    int callTo1;
    int callTo2;
    String endTime;
    String duration;
    public VideoLogsModel(int id,int callFrom,String callFromName,String startTime,String day,String callTo1Name,String callTo2Name,int callTo1,int callTo2,String endTime,String duration)
    {

        this.id=id;
        this.callFrom=callFrom;
        this.callFromName=callFromName;
        this.startTime=startTime;
        this.day=day;
        this.callTo1Name=callTo1Name;
        this.callTo2Name=callTo2Name;
        this.callTo1=callTo1;
        this.callTo2=callTo2;
        this.endTime=endTime;
        this.duration=duration;

    }

    public int getCallFrom() {
        return callFrom;
    }

    public int getCallTo1() {
        return callTo1;
    }

    public int getCallTo2() {
        return callTo2;
    }

    public int getId() {
        return id;
    }

    public String getCallFromName() {
        return callFromName;
    }

    public String getCallTo1Name() {
        return callTo1Name;
    }

    public String getCallTo2Name() {
        return callTo2Name;
    }

    public String getDay() {
        return day;
    }

    public String getDuration() {
        return duration;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setCallFrom(int callFrom) {
        this.callFrom = callFrom;
    }

    public void setCallFromName(String callFromName) {
        this.callFromName = callFromName;
    }

    public void setCallTo1(int callTo1) {
        this.callTo1 = callTo1;
    }

    public void setCallTo1Name(String callTo1Name) {
        this.callTo1Name = callTo1Name;
    }

    public void setCallTo2(int callTo2) {
        this.callTo2 = callTo2;
    }

    public void setCallTo2Name(String callTo2Name) {
        this.callTo2Name = callTo2Name;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}
