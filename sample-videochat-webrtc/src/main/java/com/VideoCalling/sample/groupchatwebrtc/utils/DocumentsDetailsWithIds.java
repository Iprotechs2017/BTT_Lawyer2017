package com.VideoCalling.sample.groupchatwebrtc.utils;

/**
 * Created by rajesh on 26/11/16.
 */

public class DocumentsDetailsWithIds {
    int id,documentId,sharedBy,docOwner,sharedTo;
    String sharedDate;

    public DocumentsDetailsWithIds(int id,int documentId,int sharedBy,int docOwner,int sharedTo,String sharedDate)
                {


                    this.id=id;
                    this.documentId=documentId;
                    this.sharedBy=sharedBy;
                    this.docOwner=docOwner;
                    this.sharedTo=sharedTo;
                    this.sharedDate=sharedDate;




    }

    public int getId() {
        return id;
    }

    public String getSharedDate() {
        return sharedDate;
    }

    public int getDocOwner() {
        return docOwner;
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getSharedBy() {
        return sharedBy;
    }

    public int getSharedTo() {
        return sharedTo;
    }

}
