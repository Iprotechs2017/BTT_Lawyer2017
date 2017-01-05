package com.VideoCalling.sample.groupchatwebrtc.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.activities.CallActivity;
import com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity;
import com.VideoCalling.sample.groupchatwebrtc.adapters.OpponentsAdapter;
import com.VideoCalling.sample.groupchatwebrtc.adapters.OpponentsFromCallAdapter;
import com.VideoCalling.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.VideoCalling.sample.groupchatwebrtc.utils.CollectionsUtils;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.PushNotificationSender;
import com.VideoCalling.sample.groupchatwebrtc.utils.SettingsUtil;
import com.VideoCalling.sample.groupchatwebrtc.utils.WebRtcSessionManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBMediaStreamManager;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static com.VideoCalling.sample.groupchatwebrtc.activities.OpponentsActivity.userTypeDetails;


/**
 * QuickBlox team
 */
public class VideoConversationFragment extends BaseConversationFragment implements QBRTCClientSessionCallbacks,Serializable, QBRTCClientVideoTracksCallbacks,
        QBRTCSessionConnectionCallbacks, CallActivity.QBRTCSessionUserCallback, OpponentsFromCallAdapter.OnAdapterEventListener,SurfaceHolder.Callback {

    private static final int DEFAULT_ROWS_COUNT = 2;
    private static final int DEFAULT_COLS_COUNT = 3;
    private static final long TOGGLE_CAMERA_DELAY = 1000;
    private static final long LOCAL_TRACk_INITIALIZE_DELAY = 500;
    private static final int RECYCLE_VIEW_PADDING = 2;
    private static final long UPDATING_USERS_DELAY = 2000;
    private static final long FULL_SCREEN_CLICK_DELAY = 1000;
    private QBRTCClient rtcClient;
    MediaRecorder recorder;
    private Camera mCamera1,mCamera2;
    SurfaceHolder holder;
    public MediaRecorder mMediaRecorder;
    private String TAG = VideoConversationFragment.class.getSimpleName();
    private ArrayList<QBUser> currentOpponentsList;
    private ToggleButton cameraToggle;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private View view;
    private boolean isVideoCall = false;
    private LinearLayout actionVideoButtonsLayout;
    private QBRTCSurfaceView remoteFullScreenVideoView;
    private QBRTCSurfaceView localVideoView;
    private CameraState cameraState = CameraState.NONE;
    private RecyclerView recyclerView;
    private SparseArray<OpponentsFromCallAdapter.ViewHolder> opponentViewHolders;
    private List<OpponentsFromCallAdapter.ViewHolder> viewHolders;
    private boolean isPeerToPeerCall;
    com.quickblox.videochat.webrtc.view.QBRTCSurfaceView hiden_local_view;
    private QBRTCVideoTrack localVideoTrack;
    private TextView connectionStatusLocal;
    public OpponentsAdapter    opponentsAdapter1;
    private Map<Integer, QBRTCVideoTrack> videoTrackMap;
    private OpponentsFromCallAdapter opponentsAdapter;
    private LocalViewOnClickListener localViewOnClickListener;
    private boolean isRemoteShown;
    private boolean headsetPlugged;
    Button conf;
    private int amountOpponents;
    private int userIDFullScreen;
    private List<QBUser> allOpponents;
    private boolean connectionEstablished;
    SharedPreferences prefs;
    private boolean previousDeviceEarPiece;
    private boolean allCallbacksInit;
    private boolean isCurrentCameraFront = true;
    private boolean isLocalVideoFullScreen;
ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        conf= (Button) view.findViewById(R.id.conf);
        conversationFragmentCallbackListener.addCurrentCallStateCallback(this);
        conf.setVisibility(View.GONE);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadUsers1();
            }
        });
        initVideoTrackSListener();
        return view;
    }

    @Override
    protected void configureOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_transparent_50));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    protected void configureActionBar() {
        actionBar = ((AppCompatActivity) getActivity()).getDelegate().getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void configureToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black_transparent_50));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    int getFragmentLayout() {

        prefs = getActivity().getSharedPreferences("loginDetails", getActivity().MODE_PRIVATE);

        return R.layout.fragment_video_conversation;
    }

    @Override
    protected void initFields() {
        super.initFields();
        localViewOnClickListener = new LocalViewOnClickListener();
        amountOpponents = opponents.size();
        allOpponents = Collections.synchronizedList(new ArrayList<QBUser>(opponents.size()));
        allOpponents.addAll(opponents);

        timerChronometer = (Chronometer) getActivity().findViewById(R.id.timer_chronometer_action_bar);

        isPeerToPeerCall = opponents.size() == 1;
        isVideoCall = (QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(currentSession.getConferenceType()));
    }

    public void setDuringCallActionBar() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(currentUser.getFullName());
        if (isPeerToPeerCall) {
            actionBar.setSubtitle(getString(R.string.opponent, opponents.get(0).getFullName()));
        } else {
            actionBar.setSubtitle(getString(R.string.opponents, amountOpponents));
        }

        actionButtonsEnabled(true);
    }

    private void initVideoTrackSListener() {
        if (currentSession != null) {
            currentSession.addVideoTrackCallbacksListener(this);
        }
    }

    private void removeVideoTrackSListener() {
        if (currentSession != null) {
            currentSession.removeVideoTrackCallbacksListener(this);
        }
    }

    @Override
    protected void actionButtonsEnabled(boolean inability) {
        super.actionButtonsEnabled(inability);
        cameraToggle.setEnabled(inability);
        // inactivate toggle buttons
        cameraToggle.setActivated(inability);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!allCallbacksInit) {
            conversationFragmentCallbackListener.addTCClientConnectionCallback(this);
            conversationFragmentCallbackListener.addRTCSessionUserCallback(this);
            allCallbacksInit = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public void initRecorder(SurfaceHolder holder) {

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        mMediaRecorder.setProfile(cpHigh);
        mMediaRecorder.setOutputFile("/mnt/sdcard/v12345.mp4");
        mMediaRecorder.setMaxDuration(50000); // 50 seconds
        mMediaRecorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        mMediaRecorder.setPreviewDisplay(holder.getSurface());
        Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void prepareRecorder() {

    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);

        opponentViewHolders = new SparseArray<>(opponents.size());
        mMediaRecorder = new MediaRecorder();

        localVideoView = (QBRTCSurfaceView) view.findViewById(R.id.hiden_local_view);
       /*hiden_local_view= (QBRTCSurfaceView) view.findViewById(R.id.hiden_local_view);
        holder = localVideoView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       initRecorder(holder);*/
      /*  holder = hiden_local_view.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/

        initCorrectSizeForLocalView();
        localVideoView.setZOrderMediaOverlay(true);

        if (!isPeerToPeerCall) {
            recyclerView = (RecyclerView) view.findViewById(R.id.grid_opponents);

            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.dimen.grid_item_divider));
            recyclerView.setHasFixedSize(true);
            final int columnsCount = defineColumnsCount();
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

//          for correct removing item in adapter
            recyclerView.setItemAnimator(null);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setGrid(columnsCount);
                    recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
        connectionStatusLocal = (TextView) view.findViewById(R.id.connectionStatusLocal);

        cameraToggle = (ToggleButton) view.findViewById(R.id.toggle_camera);
        cameraToggle.setVisibility(View.VISIBLE);

        actionVideoButtonsLayout = (LinearLayout) view.findViewById(R.id.element_set_video_buttons);

        actionButtonsEnabled(false);
    }
    private void initCorrectSizeForLocalView() {
        ViewGroup.LayoutParams params = localVideoView.getLayoutParams();
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        int screenWidthPx = displaymetrics.widthPixels;
        Log.e(TAG, "screenWidthPx " + screenWidthPx);
        params.width = (int) (screenWidthPx * 0.3);
        params.height = (params.width / 2) * 3;
        localVideoView.setLayoutParams(params);
    }

    private void setGrid(int columnsCount) {
        int gridWidth = view.getMeasuredWidth();
        Log.i(TAG, "onGlobalLayout : gridWidth=" + gridWidth + " columnsCount= " + columnsCount);
        float itemMargin = getResources().getDimension(R.dimen.grid_item_divider);
        int cellSizeWidth = defineSize(gridWidth, columnsCount, itemMargin);
        Log.i(TAG, "onGlobalLayout : cellSize=" + cellSizeWidth);
        opponentsAdapter = new OpponentsFromCallAdapter(getActivity(), currentSession, opponents, cellSizeWidth,
                (int) getResources().getDimension(R.dimen.item_height));
        opponentsAdapter.setAdapterListener(VideoConversationFragment.this);
        recyclerView.setAdapter(opponentsAdapter);
    }

    private Map<Integer, QBRTCVideoTrack> getVideoTrackMap() {
        if (videoTrackMap == null) {
            videoTrackMap = new HashMap<>();
        }
        return videoTrackMap;
    }

    private int defineSize(int measuredWidth, int columnsCount, float padding) {
        return measuredWidth / columnsCount - (int) (padding * 2) - RECYCLE_VIEW_PADDING;
    }

    private int defineRowCount() {
        int result = DEFAULT_ROWS_COUNT;
        int opponentsCount = opponents.size();
        if (opponentsCount < 3) {
            result = opponentsCount;
        }
        return result;
    }

    private int defineColumnsCount() {
        return opponents.size() - 1;
    }

    @Override
    public void onResume() {
        super.onResume();

        // If user changed camera state few times and last state was CameraState.ENABLED_FROM_USER
        // than we turn on cam, else we nothing change
        if (cameraState != CameraState.DISABLED_FROM_USER) {
            toggleCamera(true);
        }
    }

    @Override
    public void onPause() {
        // If camera state is CameraState.ENABLED_FROM_USER or CameraState.NONE
        // than we turn off cam
        if (cameraState != CameraState.DISABLED_FROM_USER) {
            toggleCamera(false);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (connectionEstablished) {
            conversationFragmentCallbackListener.removeRTCClientConnectionCallback(this);
            conversationFragmentCallbackListener.removeRTCSessionUserCallback(this);
            allCallbacksInit = false;
        } else {
            Log.e(TAG, "We are in dialing process yet!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        conversationFragmentCallbackListener.removeCurrentCallStateCallback(this);
        removeVideoTrackSListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaRecorder.reset();
        mMediaRecorder.release();


        // once the objects have been released they can't be reused
        mMediaRecorder= null;

    }

    protected void initButtonsListener() {
        super.initButtonsListener();

        cameraToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraState = isChecked ? CameraState.ENABLED_FROM_USER : CameraState.DISABLED_FROM_USER;
                toggleCamera(isChecked);
            }
        });
    }

    private void switchCamera(final MenuItem item) {
        if (currentSession == null || cameraState == CameraState.DISABLED_FROM_USER) {
            return;
        }
        final QBMediaStreamManager mediaStreamManager = currentSession.getMediaStreamManager();
        if (mediaStreamManager == null) {
            return;
        }
//      disable cameraToggle while processing switchCamera
        cameraToggle.setEnabled(false);

        mediaStreamManager.switchCameraInput(new CameraVideoCapturer.CameraSwitchHandler() {
            @Override
            public void onCameraSwitchDone(boolean b) {
                Log.e(TAG, "camera switched, bool = " + b);
                isCurrentCameraFront = b;
                updateSwitchCameraIcon(item);
                toggleCameraInternal();
            }

            @Override
            public void onCameraSwitchError(String s) {
                Log.e(TAG, "camera switch error " + s);
                cameraToggle.setEnabled(true);
            }
        });
    }

    private void updateSwitchCameraIcon(final MenuItem item) {
        if (isCurrentCameraFront) {
            Log.e(TAG, "CameraFront now!");
            item.setIcon(R.drawable.ic_camera_front);
        } else {
            Log.e(TAG, "CameraRear now!");
            item.setIcon(R.drawable.ic_camera_rear);
        }
    }

    private void toggleCameraInternal() {
        Log.e(TAG, "Camera was switched!");
        updateVideoView(isLocalVideoFullScreen ? remoteFullScreenVideoView : localVideoView, isCurrentCameraFront);
        toggleCamera(true);
    }

    private void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    private void toggleCamera(boolean isNeedEnableCam) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            conversationFragmentCallbackListener.onSetVideoEnabled(isNeedEnableCam);
        }
        if (connectionEstablished && !cameraToggle.isEnabled()) {
            cameraToggle.setEnabled(true);
        }
    }

    private void setOpponentsVisibility(int visibility) {
        for (OpponentsFromCallAdapter.ViewHolder RTCView : getAllOpponentsView()) {
            RTCView.getOpponentView().setVisibility(visibility);
        }
    }

    ////////////////////////////  callbacks from QBRTCClientVideoTracksCallbacks ///////////////////
    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, final QBRTCVideoTrack videoTrack) {
        Log.e(TAG, "onLocalVideoTrackReceive() run");
        localVideoTrack = videoTrack;
        isLocalVideoFullScreen = true;

        if (remoteFullScreenVideoView != null) {
            fillVideoView(remoteFullScreenVideoView, localVideoTrack, false);
        }

        if (isPeerToPeerCall) {
            if (remoteFullScreenVideoView != null) {
                return;
            }
            Log.e(TAG, "onLocalVideoTrackReceive init localView");
            remoteFullScreenVideoView = (QBRTCSurfaceView) view.findViewById(R.id.remote_video_view);
            remoteFullScreenVideoView.setOnClickListener(localViewOnClickListener);

            if (localVideoTrack != null) {
                fillVideoView(remoteFullScreenVideoView, localVideoTrack, false);


            }
        }
        //in other case localVideoView hasn't been inflated yet. Will set track while OnBindLastViewHolder
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession session, final QBRTCVideoTrack videoTrack, final Integer userID) {
        Log.e(TAG, "onRemoteVideoTrackReceive for opponent= " + userID);

        fillVideoView(localVideoView, localVideoTrack, false);
        isLocalVideoFullScreen = false;


        if (isPeerToPeerCall) {
            setDuringCallActionBar();
            if (remoteFullScreenVideoView == null) {
                remoteFullScreenVideoView = (QBRTCSurfaceView) view.findViewById(R.id.remote_video_view);
            }

            fillVideoView(remoteFullScreenVideoView, videoTrack, true);
            updateVideoView(remoteFullScreenVideoView, false);
        } else {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRemoteViewMultiCall(userID, videoTrack);
                }
            }, LOCAL_TRACk_INITIALIZE_DELAY);
        }
    }
    /////////////////////////////////////////    end    ////////////////////////////////////////////

    //last opponent view is bind
    @Override
    public void OnBindLastViewHolder(final OpponentsFromCallAdapter.ViewHolder holder, final int position) {
        Log.i(TAG, "OnBindLastViewHolder position=" + position);
        if (!isVideoCall) {
            return;
        }
        if (isPeerToPeerCall) {

        } else {
            //on group call we postpone initialization of VideoView due to set it on Gui renderer.
            // Refer to RTCGlVIew
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (remoteFullScreenVideoView != null) {
                        return;
                    }
                    setOpponentsVisibility(View.GONE);
                    Log.i(TAG, "OnBindLastViewHolder init localView");
                    remoteFullScreenVideoView = (QBRTCSurfaceView) view.findViewById(R.id.remote_video_view);
                    remoteFullScreenVideoView.setOnClickListener(localViewOnClickListener);
                    if (localVideoTrack != null) {
                        Log.e(TAG, "OnBindLastViewHolder.fillVideoView localVideoTrack");
                        fillVideoView(isLocalVideoFullScreen ? remoteFullScreenVideoView : localVideoView, localVideoTrack, false);
                    }
                }
            }, LOCAL_TRACk_INITIALIZE_DELAY);
        }
    }


    @Override
    public void onItemClick(int position) {
        int userId = opponentsAdapter.getItem(position);
        Log.e(TAG, "USer onItemClick= " + userId);
        if (!getVideoTrackMap().containsKey(userId) ||
                currentSession.getPeerChannel(userId).getState().ordinal() == QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CLOSED.ordinal()) {
            return;
        }

        replaceUsersInAdapter(position);

        updateViewHolders(position);

        swapUsersFullscreenToPreview(userId);
    }

    private void replaceUsersInAdapter(int position) {
        for (QBUser qbUser : allOpponents) {
            if (qbUser.getId() == userIDFullScreen) {
                opponentsAdapter.replaceUsers(position, qbUser);
                break;
            }
        }
    }

    private void updateViewHolders(int position) {
        View childView = recyclerView.getChildAt(position);
        OpponentsFromCallAdapter.ViewHolder childViewHolder = (OpponentsFromCallAdapter.ViewHolder) recyclerView.getChildViewHolder(childView);
        viewHolders.set(position, childViewHolder);
    }

    @SuppressWarnings("ConstantConditions")
    private void swapUsersFullscreenToPreview(int userId) {
//      get opponentVideoTrack - opponent's video track from recyclerView
        QBRTCVideoTrack opponentVideoTrack = getVideoTrackMap().get(userId);

//      get mainVideoTrack - opponent's video track from full screen
        QBRTCVideoTrack mainVideoTrack = getVideoTrackMap().get(userIDFullScreen);

        QBRTCSurfaceView remoteVideoView = findHolder(userId).getOpponentView();

        if (mainVideoTrack != null) {
            fillVideoView(0, remoteVideoView, mainVideoTrack);
            Log.e(TAG, "_remoteVideoView enabled");
        }
        if (opponentVideoTrack != null) {
            fillVideoView(userId, remoteFullScreenVideoView, opponentVideoTrack);
            Log.e(TAG, "fullscreen enabled");
        }
    }

    private void setLocalVideoView(int userId, QBRTCVideoTrack videoTrack) {
        if (remoteFullScreenVideoView == null) {
            Log.e(TAG, "setLocalVideoView VideoView = null");
            remoteFullScreenVideoView = (QBRTCSurfaceView) view.findViewById(R.id.remote_video_view);
        }

        fillVideoView(userId, remoteFullScreenVideoView, videoTrack);
    }

    private void setRemoteViewMultiCall(int userID, QBRTCVideoTrack videoTrack) {
        Log.e(TAG, "setRemoteViewMultiCall fillVideoView");
        final OpponentsFromCallAdapter.ViewHolder itemHolder = getViewHolderForOpponent(userID);
        if (itemHolder == null) {
            Log.e(TAG, "itemHolder == null - true");
            return;
        }
        final QBRTCSurfaceView remoteVideoView = itemHolder.getOpponentView();

        getVideoTrackMap().put(userID, videoTrack);

        if (remoteVideoView != null) {
            remoteVideoView.setZOrderMediaOverlay(true);
            updateVideoView(remoteVideoView, false);

            Log.e(TAG, "onRemoteVideoTrackReceive fillVideoView");
            if (isRemoteShown) {
                Log.e(TAG, "USer onRemoteVideoTrackReceive = " + userID);
                fillVideoView(remoteVideoView, videoTrack, true);
            } else {
                isRemoteShown = true;
                opponentsAdapter.removeItem(itemHolder.getAdapterPosition());
                setDuringCallActionBar();
                setRecyclerViewVisibleState();
                setOpponentsVisibility(View.VISIBLE);
                fillVideoView(userID, remoteFullScreenVideoView, videoTrack);
                updateVideoView(remoteFullScreenVideoView, false);
            }
        }
    }

    private void setRecyclerViewVisibleState() {
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int) getResources().getDimension(R.dimen.item_height);
        recyclerView.setLayoutParams(params);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private OpponentsFromCallAdapter.ViewHolder getViewHolderForOpponent(Integer userID) {
        OpponentsFromCallAdapter.ViewHolder holder = opponentViewHolders.get(userID);
        if (holder == null) {
            holder = findHolder(userID);
            if (holder != null) {
                opponentViewHolders.append(userID, holder);
            }
        }
        return holder;
    }

    private List<OpponentsFromCallAdapter.ViewHolder> getAllOpponentsView() {
        if (viewHolders != null) {
            return viewHolders;
        }
        int childCount = recyclerView.getChildCount();
        viewHolders = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View childView = recyclerView.getChildAt(i);
            OpponentsFromCallAdapter.ViewHolder childViewHolder = (OpponentsFromCallAdapter.ViewHolder) recyclerView.getChildViewHolder(childView);
            viewHolders.add(childViewHolder);
        }
        return viewHolders;
    }

    private OpponentsFromCallAdapter.ViewHolder findHolder(Integer userID) {
        if (viewHolders == null) {
            Log.e(TAG, "viewHolders == null");
            return null;
        }
        for (OpponentsFromCallAdapter.ViewHolder childViewHolder : viewHolders) {
            Log.e(TAG, "getViewForOpponent holder user id is : " + childViewHolder.getUserId());
            if (userID.equals(childViewHolder.getUserId())) {
                return childViewHolder;
            }
        }
        return null;
    }

    private void fillVideoView(QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack, boolean remoteRenderer) {
        videoTrack.removeRenderer(videoTrack.getRenderer());
        videoTrack.addRenderer(new VideoRenderer(videoView));

        if (!remoteRenderer) {
            updateVideoView(videoView, isCurrentCameraFront);
        }

    }


    /**
     * @param userId set userId if it from fullscreen videoTrack
     */
    private void fillVideoView(int userId, QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack) {
        if (userId != 0) {
            userIDFullScreen = userId;
        }
        fillVideoView(videoView, videoTrack, true);
    }

    protected void updateVideoView(SurfaceViewRenderer surfaceViewRenderer, boolean mirror) {
        updateVideoView(surfaceViewRenderer, mirror, RendererCommon.ScalingType.SCALE_ASPECT_FILL);
    }

    protected void updateVideoView(SurfaceViewRenderer surfaceViewRenderer, boolean mirror, RendererCommon.ScalingType scalingType) {
        Log.i(TAG, "updateVideoView mirror:" + mirror + ", scalingType = " + scalingType);
        surfaceViewRenderer.setScalingType(scalingType);
        surfaceViewRenderer.setMirror(mirror);
        surfaceViewRenderer.requestLayout();

    }

    private void setStatusForOpponent(int userId, final String status) {
        if (isPeerToPeerCall) {
            connectionStatusLocal.setText(status);
            return;
        }

        final OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
        if (holder == null) {
            return;
        }

        holder.setStatus(status);
    }

    private void updateNameForOpponent(int userId, String newUserName) {
        OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
        if (holder == null) {
            Log.e("UPDATE_USERS", "holder == null");
            return;
        }

        Log.e("UPDATE_USERS", "holder != null");
        holder.setUserName(newUserName);
    }

    private void setProgressBarForOpponentGone(int userId) {
        if (isPeerToPeerCall) {
            return;
        }
        final OpponentsFromCallAdapter.ViewHolder holder = getViewHolderForOpponent(userId);
        if (holder == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.getProgressBar().setVisibility(View.GONE);
            }
        });
    }

    private void setBackgroundOpponentView(final Integer userId) {
        final OpponentsFromCallAdapter.ViewHolder holder = findHolder(userId);
        if (holder == null) {
            return;
        }
                if (userId != userIDFullScreen) {
            holder.getOpponentView().setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    ///////////////////////////////  QBRTCSessionConnectionCallbacks ///////////////////////////

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer userId) {
        setStatusForOpponent(userId, getString(R.string.text_status_checking));
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, final Integer userId) {
        connectionEstablished = true;
        setStatusForOpponent(userId, getString(R.string.text_status_connected));
        setProgressBarForOpponentGone(userId);
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer userId) {
        setStatusForOpponent(userId, getString(R.string.text_status_closed));
        if (!isPeerToPeerCall) {
            Log.e(TAG, "onConnectionClosedForUser videoTrackMap.remove(userId)= " + userId);
            getVideoTrackMap().remove(userId);
            setBackgroundOpponentView(userId);
        }
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        setStatusForOpponent(integer, getString(R.string.text_status_disconnected));
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        setStatusForOpponent(integer, getString(R.string.text_status_time_out));
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        setStatusForOpponent(integer, getString(R.string.text_status_failed));
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {

    }
    //////////////////////////////////   end     //////////////////////////////////////////


    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {

    }

    /////////////////// Callbacks from CallActivity.QBRTCSessionUserCallback //////////////////////
    @Override
    public void onUserNotAnswer(QBRTCSession session, Integer userId) {
        setProgressBarForOpponentGone(userId);
        try {
            setStatusForOpponent(userId, getString(R.string.text_status_no_answer));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        setStatusForOpponent(userId, getString(R.string.text_status_rejected));
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo) {
        setStatusForOpponent(userId, getString(R.string.accepted));
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {

    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession session, Integer userId) {
        setStatusForOpponent(userId, getString(R.string.text_status_hang_up));
        Log.e(TAG, "onReceiveHangUpFromUser userId= " + userId);
        if (!isPeerToPeerCall) {
            if (userId == userIDFullScreen) {
                Log.e(TAG, "setAnotherUserToFullScreen call userId= " + userId);
                setAnotherUserToFullScreen();
            }
        }
    }

    //////////////////-------dialog

    public void startLoadUsers1() {
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        QBResRequestExecutor requestExecutor= App.getInstance().getQbResRequestExecutor();

        requestExecutor.loadUsersByTag(String.valueOf(Consts.PREF_CURREN_ROOM_NAME), new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                //      hideProgressDialog();
                dbManager.saveAllUsers(result, true);
                proceedInitUsersList();

            }

            @Override
            public void onError(QBResponseException responseException) {
                progressDialog.dismiss();
                /*    hideProgressDialog();
                    showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startLoadUsers();
                        }
                    });*/
            }
        });

    }
    private void proceedInitUsersList() {
      //  conversationFragmentCallbackListener.onHangUpCurrentSession();
        Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_opponents);
        RelativeLayout bar_reg= (RelativeLayout) dialog.findViewById(R.id.bar_reg);
        de.hdodenhof.circleimageview.CircleImageView reload=(CircleImageView) dialog.findViewById(R.id.reload);
        de.hdodenhof.circleimageview.CircleImageView notifi= (CircleImageView) dialog.findViewById(R.id.show_notifications);
        de.hdodenhof.circleimageview.CircleImageView logout= (CircleImageView) dialog.findViewById(R.id.logout);
        de.hdodenhof.circleimageview.CircleImageView videoCall= (CircleImageView) dialog.findViewById(R.id.videocall);
        reload.setVisibility(View.GONE);
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationFragmentCallbackListener.onHangUpCurrentSession();
                startCall(true);
            }
        });
        notifi.setVisibility(View.GONE);
        logout.setVisibility(View.GONE);
        bar_reg.setVisibility(View.GONE);
        ListView opponentsListView= (ListView) dialog.findViewById(R.id.list_opponents);
       // Button call= (Button) dialog.findViewById(R.id.call);
       /* call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   conversationFragmentCallbackListener.onHangUpCurrentSession();
             //   startCall(true);
            }
        });*/
        currentOpponentsList = dbManager.getAllUsers();
        Log.e(TAG, "proceedInitUsersList currentOpponentsList= " + currentOpponentsList);
        SharedPrefsHelper    sharedPrefsHelper = SharedPrefsHelper.getInstance();
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            opponentsAdapter1 = new OpponentsAdapter(getActivity(), currentOpponentsList,true);
        opponentsAdapter1.setSelectedItemsCountsChangedListener(new OpponentsAdapter.SelectedItemsCountsChangedListener() {
            @Override
            public void onCountSelectedItemsChanged(int count) {

            }
        });
if(OpponentsActivity.callType.equalsIgnoreCase("imm"))
{
    Log.e("type",userTypeDetails.get("name").toString());
    for(int i=0;i<=currentOpponentsList.size()-1;i++)
    {


        if(!currentOpponentsList.get(i).getFullName().equalsIgnoreCase(userTypeDetails.get("name").toString()))
        {

            currentOpponentsList.remove(i);
//            Log.e("data",currentOpponentsList.get(i).getFullName()+"---"+(userTypeDetails.get("name").toString()));
        }
    }
}
        else
{
    for(int i=0;i<=currentOpponentsList.size()-1;i++)
    {


        if(currentOpponentsList.get(i).getFullName().equalsIgnoreCase(userTypeDetails.get("name").toString()))
        {

            currentOpponentsList.remove(i);
         //   Log.e("data",currentOpponentsList.get(i).getFullName()+"---"+(userTypeDetails.get("name").toString()));
        }
    }
}
        opponentsListView.setAdapter(opponentsAdapter1);
        progressDialog.dismiss();
        dialog.show();
      //  conversationFragmentCallbackListener.onHangUpCurrentSession();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            conversationFragmentCallbackListener = (ConversationFragmentCallbackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConversationFragmentCallbackListener");
        }
    }

    private void startCall(boolean isVideoCall) {
        if (opponentsAdapter1.getSelectedItems().size() > Consts.MAX_OPPONENTS_COUNT) {
            Toaster.longToast(String.format(getString(R.string.error_max_opponents_count),
                    Consts.MAX_OPPONENTS_COUNT));
            return;
        }

        Log.e(TAG, "startCall()");
        ArrayList<Integer> opponentsList = CollectionsUtils.getIdsSelectedOpponents(opponentsAdapter1.getSelectedItems());
        opponentsList.add(OpponentsActivity.onlineUser);
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getActivity());

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

        WebRtcSessionManager.getInstance(getActivity()).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());
        OpponentsActivity.connection="reconnect";
      CallActivity.start(getActivity(),false);
        getActivity().finish();
    }


    //////////////////////////////////   end     //////////////////////////////////////////

    private void setAnotherUserToFullScreen() {
        if (opponentsAdapter.getOpponents().isEmpty()) {
            return;
        }
        int userId = opponentsAdapter.getItem(0);
//      get opponentVideoTrack - opponent's video track from recyclerView
        QBRTCVideoTrack opponentVideoTrack = getVideoTrackMap().get(userId);
        if (opponentVideoTrack == null) {
            Log.e(TAG, "setAnotherUserToFullScreen opponentVideoTrack == null");
            return;
        }

        fillVideoView(userId, remoteFullScreenVideoView, opponentVideoTrack);
        Log.e(TAG, "fullscreen enabled");

        OpponentsFromCallAdapter.ViewHolder itemHolder = findHolder(userId);
        if (itemHolder != null) {
            opponentsAdapter.removeItem(itemHolder.getAdapterPosition());
            itemHolder.getOpponentView().release();
            Log.e(TAG, "onConnectionClosedForUser opponentsAdapter.removeItem= " + userId);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.conversation_fragment, menu);
        MenuItem item = menu.findItem(R.id.invite);
        if (prefs.getInt("userType", -1) == 1) {
            item.setVisible(true);
        }
        else
        {
            item.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_switch:
                Log.e("Conversation", "camera_switch");
                switchCamera(item);
                return true;
            case R.id.invite:
                Log.e("Conversation", "invite");
                //startLoadUsers1();
                startLoadUsers1();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        updateAllOpponentsList(newUsers);
        Log.e("UPDATE_USERS", "updateOpponentsList(), newUsers = " + newUsers);
        runUpdateUsersNames(newUsers);
    }

    private void updateAllOpponentsList(ArrayList<QBUser> newUsers) {

        for (int i = 0; i < allOpponents.size(); i++) {
            for (QBUser updatedUser : newUsers) {
                if (updatedUser.equals(allOpponents.get(i))) {
                    allOpponents.set(i, updatedUser);
                }
            }
        }
    }

    private void runUpdateUsersNames(final ArrayList<QBUser> newUsers) {
        //need delayed for synchronization with recycler view initialization
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (QBUser user : newUsers) {
                    Log.e("UPDATE_USERS", "foreach, user = " + user.getFullName());
                    updateNameForOpponent(user.getId(), user.getFullName());
                }
            }
        }, UPDATING_USERS_DELAY);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
        try {
            mCamera1 = Camera.open();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = mCamera1.getParameters();

        param.setPreviewSize(352, 288);
        mCamera1.setParameters(param);
        try {
            mCamera1.setPreviewDisplay(holder);
            mCamera1.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // once the objects have been released they can't be reused


    }

    private enum CameraState {
        NONE,
        DISABLED_FROM_USER,
        ENABLED_FROM_USER
    }


    class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public DividerItemDecoration(@NonNull Context context, @DimenRes int dimensionDivider) {
            this.space = context.getResources().getDimensionPixelSize(dimensionDivider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(space, space, space, space);
        }
    }

    class LocalViewOnClickListener implements View.OnClickListener {
        private long lastFullScreenClickTime = 0L;

        @Override
        public void onClick(View v) {
            if ((SystemClock.uptimeMillis() - lastFullScreenClickTime) < FULL_SCREEN_CLICK_DELAY) {
                return;
            }
            lastFullScreenClickTime = SystemClock.uptimeMillis();

            if (connectionEstablished) {
                setFullScreenOnOff();
            }
        }

        private void setFullScreenOnOff() {
            if (actionBar.isShowing()) {
                hideToolBarAndButtons();
            } else {
                showToolBarAndButtons();
            }
        }

        private void hideToolBarAndButtons() {
            actionBar.hide();

            localVideoView.setVisibility(View.INVISIBLE);

            actionVideoButtonsLayout.setVisibility(View.GONE);

            if (!isPeerToPeerCall) {
                shiftBottomListOpponents();
            }
        }

        private void showToolBarAndButtons() {
            actionBar.show();

            localVideoView.setVisibility(View.VISIBLE);

            actionVideoButtonsLayout.setVisibility(View.VISIBLE);

            if (!isPeerToPeerCall) {
                shiftMarginListOpponents();
            }
        }

        private void shiftBottomListOpponents() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0, 0, 0, 0);

            recyclerView.setLayoutParams(params);
        }

        private void shiftMarginListOpponents() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margin_common));

            recyclerView.setLayoutParams(params);

        }

    }

}


