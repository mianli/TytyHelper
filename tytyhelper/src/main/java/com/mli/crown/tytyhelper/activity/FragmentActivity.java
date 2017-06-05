package com.mli.crown.tytyhelper.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mli.crown.tytyhelper.fragment.FragmentHelper;
import com.mli.crown.tytyhelper.tools.MyToast;

/**
 * Created by crown on 2017/6/5.
 */

public class FragmentActivity extends AppCompatActivity {

    private static final String CURRENT_SHOWING_TAG = "currentShowingTag";
    private FragmentHelper mFragmentHelper;

    @SuppressWarnings("unchecked")
    protected <VIEW_TYPE extends Fragment> VIEW_TYPE restoreFragment(String tag) {
        VIEW_TYPE fragment = (VIEW_TYPE) getSupportFragmentManager().findFragmentByTag(tag);
        mFragmentHelper.putFragment(fragment, tag);
        return fragment;
    }

    protected void initFragmentHelper(@IdRes int id) {
        mFragmentHelper = new FragmentHelper(id);
    }

    protected Fragment showFragment(Fragment fragment, Class cls) {
        if(fragment == null) {
            try {
                fragment = (Fragment) cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(fragment != null) {
            showFragment(fragment, cls.getSimpleName());
        }
        return fragment;
    }

    protected Fragment getCurrentShowingFragment(Bundle saveInstanceState) {
        MyToast.shortShow(this, getCurrentShowingTag(saveInstanceState));
        return getSupportFragmentManager().findFragmentByTag(getCurrentShowingTag(saveInstanceState));
    }

    protected String getCurrentShowingTag(Bundle saveInstanceState) {
        return saveInstanceState.getString(CURRENT_SHOWING_TAG);
    }

    protected void showFragment(Fragment fragment, @NonNull String tag) {
        mFragmentHelper.showFragment(getSupportFragmentManager().beginTransaction(), fragment, tag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_SHOWING_TAG, mFragmentHelper.getShowingTag());
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    protected <VIEW_TYPE> VIEW_TYPE findView(int id) {
        return (VIEW_TYPE) findViewById(id);
    }

}
