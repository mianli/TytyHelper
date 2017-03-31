package com.mli.crown.tytyhelper.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.mli.crown.tytyhelper.tools.Config;
import com.mli.crown.tytyhelper.tools.Log;
import com.mli.crown.tytyhelper.tools.MyToast;

import java.util.List;

/**
 * Created by crown on 2017/3/22.
 */
public class LoginHelper {

	public static LoginHelper getInstance(Context context) {
		return new LoginHelper(context);
	}

	private Context mContext;

	public LoginHelper(Context context) {
		this.mContext = context;
	}

	public void filterAndStartApp() {
		ResolveInfo mInfo = null;
		PackageManager manager = mContext.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> appsInfoList = manager.queryIntentActivities(mainIntent, PackageManager.GET_ACTIVITIES);
		for (ResolveInfo info : appsInfoList) {
			Log.i(info.activityInfo.packageName);
			if((info.activityInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)<=0){
				if(info.activityInfo.packageName.contains(Config.THIRD_PART_APP_NAME)) {
					mInfo = info;
					break;
				}
			}
		}
		if(mInfo == null) {
			MyToast.shortShow(mContext, "没有找到该应用，或该应用还没有被安装");
		}else {
			try{
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(mInfo.activityInfo.packageName,
					mInfo.activityInfo.name));
				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(launchIntent);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void release() {
		mContext = null;
	}

}
