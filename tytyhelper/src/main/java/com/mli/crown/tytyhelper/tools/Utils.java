package com.mli.crown.tytyhelper.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.*;
import android.view.View;

import com.mli.crown.tytyhelper.BuildConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

    public static void showOpenAccessibilitySetting(final Context context, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle("提示").
                setMessage("辅助功能没有开启，现在开启？").
                setNegativeButton("取消", null).
                setNeutralButton("不开启", listener).
                setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.openAccessibilitySetting(context);
                    }
                }).show();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <VIEW_TYPE> VIEW_TYPE findView(View view, int id) {
        return (VIEW_TYPE) view.findViewById(id);
    }

    public static void getFilename(Context context, String url, GetFilenameTask.iGetResultListener listener) {
        String[] urls = new String[]{url};
        new GetFilenameTask(context, listener).execute(urls);
    }

    public static class GetFilenameTask extends AsyncTask<String, Void, String> {

        public interface iGetResultListener {
            void getFilename(String result);
        }

        private Context mContext;
        private iGetResultListener mLisetner;

        public GetFilenameTask(Context context, iGetResultListener listener) {
            this.mContext = context;
            this.mLisetner = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            return getFilename(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mLisetner.getFilename(result);
        }
    }

    private static String getFilename(String url) {
        String filename = null;
        URL myURL = null;
        try {
            myURL = new URL(url);
            URLConnection conn = null;
            conn = myURL.openConnection();

            conn.connect();
            if(((HttpURLConnection) conn).getResponseCode()==200){
                String file = conn.getURL().getFile();
                filename = file.substring(file.lastIndexOf('/')+1);

                return filename;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static int getNetworkStatus(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取管理器的活动网络信息对象
        NetworkInfo activeInfo = conMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()){
            // 查看网络连接类型
            boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            // wifi 连接
            if(wifiConnected){
                return 0;
            } else if(mobileConnected){ // 手机网络连接
                return 1;
            }else {
                return 2;//
            }
        }
        return 3;//
    }

}
