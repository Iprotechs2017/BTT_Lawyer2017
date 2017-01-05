package com.VideoCalling.sample.groupchatwebrtc.services.gcm;

import com.quickblox.sample.core.gcm.CoreGcmPushInstanceIDService;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;

public class GcmPushInstanceIDService extends CoreGcmPushInstanceIDService {
    @Override
    protected String getSenderId() {
        return Consts.GCM_SENDER_ID;
    }
}
