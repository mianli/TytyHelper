package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.mli.crown.tytyhelper.BuildConfig;

import static android.content.pm.PackageManager.GET_ACTIVITIES;

/**
 * Created by mli on 2017/4/21.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static boolean isAccessibilitySettingsEnable(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
//		Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
//			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
//			Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
//			Log.v(TAG, "ACCESSIBILITY IS ENABLED");
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

//					Log.v(TAG, "-------------- > accessibilityService : " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
//						Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
//			Log.v(TAG, "ACCESSIBILITY IS DISABLED");
        }
        return false;
    }

    public static void openAccessibilitySetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.putExtra(Settings.ACTION_ACCESSIBILITY_SETTINGS, BuildConfig.APPLICATION_ID);
        context.startActivity(intent);
    }

    public static boolean isAPkDebug(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, GET_ACTIVITIES);
            if(info != null) {
                ApplicationInfo applicationInfo = info.applicationInfo;
                return (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)!= 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
