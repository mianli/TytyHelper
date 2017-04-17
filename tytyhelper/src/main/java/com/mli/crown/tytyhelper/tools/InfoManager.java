package com.mli.crown.tytyhelper.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;

/**
 * Created by mli on 2017/4/17.
 */

public class InfoManager {

    private static final String FILE_NAME = "LoginInfo";
    private static final String NAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String DESC_KEY = "desc";

    public static void saveInfo(Context context, SimpleLoginInfo info) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME_KEY, info.getUsername());
        editor.putString(PASSWORD_KEY, info.getPasswrod());
        editor.putString(DESC_KEY, info.getDesc());
        editor.commit();
    }

    public static SimpleLoginInfo getInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String username = preferences.getString(NAME_KEY, null);
        String password = preferences.getString(PASSWORD_KEY, null);
        String desc = preferences.getString(DESC_KEY, null);
        return new SimpleLoginInfo(username, password, desc);
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
