package com.mli.crown.tytyhelper.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mli on 2017/4/24.
 */

public class DataManager {

    public static void saveValue(Context context, String filename, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(Context context, String filename, String key) {
        SharedPreferences preferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

}
