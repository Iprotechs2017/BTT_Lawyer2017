package com.VideoCalling.sample.groupchatwebrtc.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.services.NotificationService;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.helper.Utils;
import com.quickblox.sample.core.gcm.GooglePlayServicesHelper;
import com.quickblox.sample.core.ui.activity.CoreSplashActivity;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.VideoCalling.sample.groupchatwebrtc.App;
import com.VideoCalling.sample.groupchatwebrtc.R;
import com.VideoCalling.sample.groupchatwebrtc.services.CallService;
import com.VideoCalling.sample.groupchatwebrtc.services.WebService;
import com.VideoCalling.sample.groupchatwebrtc.util.QBResRequestExecutor;
import com.VideoCalling.sample.groupchatwebrtc.utils.Consts;
import com.VideoCalling.sample.groupchatwebrtc.utils.QBEntityCallbackImpl;
import com.VideoCalling.sample.groupchatwebrtc.utils.UsersUtils;
import com.quickblox.users.model.QBUser;

/**
 * Created by tereha on 12.04.16.
 */
public class SplashActivity extends CoreSplashActivity {

    private SharedPrefsHelper sharedPrefsHelper;
    SharedPreferences prefs;
    SharedPreferences.Editor editor,qbuserId;
    protected QBResRequestExecutor requestExecutor;
    public QBUser userForSave;
    protected GooglePlayServicesHelper googlePlayServicesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        googlePlayServicesHelper = new GooglePlayServicesHelper();
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        qbuserId = getSharedPreferences("QB", MODE_PRIVATE).edit();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        createSession();
        stopService(new Intent(this, NotificationService.class));
        startService(new Intent(this, NotificationService.class));

    }

    @Override
    protected String getAppName() {
        return getString(R.string.splash_app_title);
    }

    @Override
    protected void proceedToTheNextActivity() {
        LoginActivity.start(this);
        finish();
    }

    private void createSession() {
        App.getInstance().getQbResRequestExecutor().createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle params) {
              if(prefs.getString("name", null)!=null) {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {

                          startSignUpNewUser(createUserWithEnteredData());
                      }
                  });

              }
                else
              {
                  if (prefs.getInt("userType", -1) == -1) {
                      startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                  } else if(prefs.getInt("userType", 0) == 0) {
                      startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                  }
                  else if((prefs.getInt("userType", 0) == 1)||(prefs.getInt("userType", 0) == 2))
                  {
                      startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                  }
                  finish();
              }
            }

            @Override
            public void onError(QBResponseException e) {
                showSnackbarError(null, R.string.splash_create_session_error, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSession();
                    }
                });
            }
        });
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }

    private void startOpponentsActivity() {
        if (prefs.getInt("userType", -1) == -1) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        } else if(prefs.getInt("userType", 0) == 0) {
            startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
        }
        else if((prefs.getInt("userType", 0) == 1)||(prefs.getInt("userType", 0) == 2))
        {
            startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
        }
        //OpponentsActivity.start(SplashActivity.this, false);
        finish();
    }

    private void startSignUpNewUser(final QBUser newUser) {

        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        loginToChat(result);
                        qbuserId.putInt("qbUserId",result.getId());
                        qbuserId.commit();
                        if (prefs.getInt("userType", 0) != 0) {
                            startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, false);
                           /* qbuserId.putInt("qbUserId",newUser.getId());
                            qbuserId.commit();*/
                            //new LoginAsync().execute();
                        } else {

                            Toaster.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }

    public void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {
                if (deleteCurrentUser) {
                    removeAllUserData(result);
                } else {
                  //  subscribeToPushes();
                    loginToChat(result);
                    startOpponentsActivity();
                }
            }

            @Override
            public void onError(QBResponseException responseException) {

                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    public void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);

        userForSave = qbUser;
        startLoginService(qbUser);
    }

    public void removeAllUserData(final QBUser user) {
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData());
            }

            @Override
            public void onError(QBResponseException e) {
                // hideProgressDialog();
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    public QBUser createQBUserWithCurrentData(String userName, String chatRoomName) {
        QBUser qbUser = null;

        StringifyArrayList<String> userTags = new StringifyArrayList<>();
        userTags.add(chatRoomName);
        qbUser = new QBUser();
        qbUser.setFullName(userName);
        qbUser.setLogin(Utils.generateDeviceId(this));
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
        qbUser.setTags(userTags);
        return qbUser;
    }

    public QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(String.valueOf(prefs.getString("name", null)), String.valueOf(Consts.PREF_CURREN_ROOM_NAME));
    }

    public void subscribeToPushes() {
        if (googlePlayServicesHelper.checkPlayServicesAvailable(this)) {
            googlePlayServicesHelper.registerForGcm(Consts.GCM_SENDER_ID);
        }
    }

}
