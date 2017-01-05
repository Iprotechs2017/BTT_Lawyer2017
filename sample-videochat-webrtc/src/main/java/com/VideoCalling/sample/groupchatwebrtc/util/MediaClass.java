package com.VideoCalling.sample.groupchatwebrtc.util;

/**
 * Created by rajesh on 17/11/16.
 */

public class MediaClass {
    String url;
    String name;
    int Id;
    String mediaType;
    String sharedByName;
    String sharedById;
    String sharedDate;
    public MediaClass(String url,String name,String mediaType,int Id,String sharedByName,String sharedById,String sharedDate)
    {
        this.url=url;
        this.name=name;
        this.Id=Id;
        this.mediaType=mediaType;
        this.sharedById=sharedById;
        this.sharedByName=sharedByName;
        this.sharedDate=sharedDate;
    }

    public int getId() {
        return Id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getSharedById() {
        return sharedById;
    }

    public String getSharedByName() {
        return sharedByName;
    }

    public String getSharedDate() {
        return sharedDate;
    }
}
