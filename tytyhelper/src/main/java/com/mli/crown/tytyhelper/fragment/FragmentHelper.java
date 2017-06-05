package com.mli.crown.tytyhelper.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mli on 2017/4/23.
 */

public class FragmentHelper {

    private String mShowingTag;
    private Map<String, Fragment> mFragments = new HashMap<>();

    private int mContainerId;

    public FragmentHelper(int containerId) {
        this.mContainerId = containerId;
    }

    public void putFragment(Fragment fragment, String tag) {
        mFragments.put(tag, fragment);
    }

    public Fragment getFragmentByTag(String tag) {
        return mFragments.get(tag);
    }

    public void showFragment(FragmentTransaction fragmentTransaction, Fragment fragment, @NonNull String tag) {
        if(fragment.isVisible()) {
            return;
        }

        for (Fragment fg : mFragments.values()) {
            if(fg != null) {
                fragmentTransaction.hide(fg);
            }
        }

        if(!mFragments.containsValue(fragment) || !fragment.isAdded()) {
            mFragments.put(tag, fragment);
            fragmentTransaction.add(mContainerId, fragment, tag);
        }

        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
        mShowingTag = tag;
    }

    public String getShowingTag() {
        return mShowingTag;
    }

}
