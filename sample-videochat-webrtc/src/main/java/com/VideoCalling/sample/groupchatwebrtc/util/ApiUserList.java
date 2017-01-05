package com.VideoCalling.sample.groupchatwebrtc.util;

/**
 * Created by rajesh on 19/11/16.
 */

public class ApiUserList
{
    String userName;
    int userId;
    String mailId;
    public ApiUserList(String userName,String mailId,int userId)
    {
        this.userName=userName;
        this.mailId=mailId;
        this.userId=userId;

    }

    public int getUserId() {
        return userId;
    }

    public String getMailId() {
        return mailId;
    }

    public String getUserName() {
        return userName;
    }
}
