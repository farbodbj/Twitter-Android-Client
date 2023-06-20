package com.twitter.myapplication.Utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

final public class AndroidUtils {
    private AndroidUtils(){}


    public static void gotoFragment(FragmentManager fragmentManager, Fragment newFragment, int fragmentContainerId, Bundle bundle) {
        if(bundle != null)
            newFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(fragmentContainerId, newFragment).commit();
    }




}
