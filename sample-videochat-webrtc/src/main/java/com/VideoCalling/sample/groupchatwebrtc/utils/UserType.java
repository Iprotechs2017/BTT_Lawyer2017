package com.VideoCalling.sample.groupchatwebrtc.utils;

/**
 * Created by rajesh on 26/11/16.
 */

public class UserType {
    int id;
    String Name;
    String email;
    String registeredBy;
    String userType;
    public UserType(int id,String Name,String email,String registeredBy,String userType)
    {
         this.id=id;
        this.Name=Name;
        this.email=email;
        this.registeredBy=registeredBy;
        this.userType=userType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Name;
    }
}
