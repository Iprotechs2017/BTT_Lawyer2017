package com.VideoCalling.sample.groupchatwebrtc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.VideoCalling.sample.groupchatwebrtc.fragments.DownloadDocs;
import com.VideoCalling.sample.groupchatwebrtc.fragments.UplodedDocs;

/**
 * Created by Harishma Velagala on 01-01-2017.
 */
public class TabsAdaptor extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public TabsAdaptor(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                UplodedDocs uplodedDocsFragments = new UplodedDocs();
                return uplodedDocsFragments;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}