package com.VideoCalling.sample.groupchatwebrtc.activities;

/**
 * Created by rajesh on 15/11/16.
 */

public class bkp {

    /*
    * <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_sign_in"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.VideoCalling.sample.groupchatwebrtc.activities.SignIn">
    <include layout="@layout/new_toolbar"
        android:visibility="gone"
        android:id="@+id/toolbar_layout"/>
    <TextView
        android:layout_marginTop="10dp"
        android:textColor="@color/black"
        android:textSize="20dp"
        android:textStyle="bold"
        android:id="@+id/title"
        android:layout_gravity="center"
        android:layout_below="@+id/toolbar_layout"
        android:layout_centerHorizontal="true"
        android:text="Login"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
<ScrollView

    android:layout_below="@+id/title"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
<LinearLayout

    android:layout_margin="10dp"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <com.wrapp.floatlabelededittext.FloatLabeledEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

    <EditText
        android:id="@+id/password"
        android:gravity="center"
        android:hint="EmailId"
        android:layout_marginTop="10dp"
        android:layout_width="match_parent"

        android:layout_height="40dp" />
    </com.wrapp.floatlabelededittext.FloatLabeledEditText>

    <com.wrapp.floatlabelededittext.FloatLabeledEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

    <EditText
        android:id="@+id/re_enter_password"
        android:gravity="center"
        android:hint="Password"
        android:layout_marginTop="10dp"
        android:layout_width="match_parent"

        android:layout_height="40dp" />
    </com.wrapp.floatlabelededittext.FloatLabeledEditText>
<RelativeLayout
    android:layout_width="match_parent"
    android:orientation="horizontal"
    android:layout_height="wrap_content">



    <Button
        android:id="@+id/signin"
        android:layout_marginTop="10dp"
        android:layout_width="wrap_content"
        android:text="Login"

        android:textColor="@color/white"
     android:layout_margin="10dp"
        android:background="@drawable/button_rectangle"
        android:layout_height="wrap_content" />
    <Button
        android:id="@+id/signup"
        android:layout_marginTop="10dp"
        android:layout_width="wrap_content"
        android:text="Signup"
        android:layout_alignParentRight="true"
        android:textColor="@color/white"
        android:layout_gravity="end"
        android:layout_margin="10dp"
        android:background="@drawable/button_rectangle"
        android:layout_height="wrap_content" />
</RelativeLayout>
</LinearLayout>

</ScrollView>

    <EditText
        android:id="@+id/user_name"
        style="@style/MatchWidth"
        android:visibility="gone"
        android:text="Rajeshyyy"
        android:paddingTop="@dimen/padding_top"
        android:hint="@string/hint_user_name"
        android:textSize="16sp"
        android:singleLine="true"
        />

    <EditText
        android:visibility="gone"
        android:text="ipro2016"
        android:id="@+id/chat_room_name"
        style="@style/MatchWidth"
        android:paddingTop="@dimen/padding_top"
        android:hint="@string/hint_chat_room_name"
        android:textSize="16sp"
        android:singleLine="true"
        />
</RelativeLayout>
////////////////////////////////sign up
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_signup"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    tools:context="com.VideoCalling.sample.groupchatwebrtc.activities.SignupActivity">
   <include layout="@layout/new_toolbar" android:id="@+id/toolbar_layout"/>

    <TextView
android:layout_marginTop="10dp"
        android:textColor="@color/black"
        android:textSize="20dp"
        android:textStyle="bold"
        android:id="@+id/title"
        android:layout_below="@+id/toolbar_layout"
        android:layout_centerHorizontal="true"
        android:text="Registration"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <ScrollView
        android:layout_below="@+id/title"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <LinearLayout
            android:layout_margin="10dp"
            android:layout_width="match_parent"
            android:orientation="vertical"
            android:layout_height="wrap_content">
            <EditText
                android:id="@+id/name"
                android:gravity="center"
                android:hint="Enter Your Name"
                android:textAlignment="center"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp" />
            <Spinner
                android:id="@+id/gender"
                android:layout_marginTop="10dp"
                android:gravity="center"
                style="@style/spinnerItemStyle"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp">

            </Spinner>
            <EditText
                android:id="@+id/mobile_number"
                android:gravity="center"
                android:hint="Enter Your Mobile Number"
                android:layout_marginTop="10dp"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp" />
            <EditText
                android:id="@+id/emilId"
                android:gravity="center"
                android:hint="Enter Mail-Id"
                android:layout_marginTop="10dp"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp" />
            <EditText
                android:id="@+id/password"
                android:gravity="center"
                android:hint="Enter Password"
                android:layout_marginTop="10dp"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp" />
            <EditText
                android:id="@+id/re_enter_password"
                android:gravity="center"
                android:hint="Re-enter Password"
                android:layout_marginTop="10dp"
                android:layout_width="match_parent"
                android:background="@drawable/rectangle"
                android:layout_height="40dp" />
            <Spinner
                android:id="@+id/user_type"
                android:layout_marginTop="10dp"
                android:layout_width="match_parent"
                android:gravity="center"
                android:background="@drawable/rectangle"
                android:layout_height="40dp">
            </Spinner>
            <Button
                android:id="@+id/registration"
                android:layout_marginTop="10dp"
                android:layout_width="wrap_content"
                android:text="Submit"
                android:textColor="@color/white"
                android:layout_gravity="center"
                android:background="@color/colorPrimary"
                android:layout_height="wrap_content" />
        </LinearLayout>


    </ScrollView>
</RelativeLayout>








    *
    * */

}
