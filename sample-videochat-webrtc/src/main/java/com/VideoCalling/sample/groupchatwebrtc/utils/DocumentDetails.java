package com.VideoCalling.sample.groupchatwebrtc.utils;

/**
 * Created by rajesh on 26/11/16.
 */

public class DocumentDetails {
    int id;
    String name;
    String docType;
    String uploadedDate;
    int uploadedTo;
    public DocumentDetails(int id,
            String name,
            String docType,
            String uploadedDate,
            int uploadedTo   )
    {
        this.id=id;
        this.name=name;
        this.docType=docType;
        this.uploadedDate=uploadedDate;
        this.uploadedTo=uploadedTo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocType() {
        return docType;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }
    public int getUploadedTo()
    {
        return uploadedTo;
    }

}
