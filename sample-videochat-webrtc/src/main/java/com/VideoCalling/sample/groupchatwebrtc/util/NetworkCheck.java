package com.VideoCalling.sample.groupchatwebrtc.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by rajesh on 21/11/16.
 */

public class NetworkCheck {
    public  NetworkCheck()
    {

    }
    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
