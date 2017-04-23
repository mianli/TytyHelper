package com.mli.crown.tytyhelper.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mli on 2017/4/23.
 */

public class FragmentHelper {

    private List<Fragment> mFragments = new ArrayList<>();

    private int mContainerId;

    public FragmentHelper(int containerId) {
        this.mContainerId = containerId;
    }

    public void showFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        for (Fragment fg : mFragments) {
            fragmentTransaction.hide(fg);
        }

        if(!mFragments.contains(fragment)) {
            mFragments.add(fragment);
            fragmentTransaction.add(mContainerId, fragment);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

}
