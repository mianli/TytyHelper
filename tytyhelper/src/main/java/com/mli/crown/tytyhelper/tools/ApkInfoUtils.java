package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by crown on 2017/3/28.
 */
public class ApkInfoUtils {

	public static Drawable getAppIcon(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return null;
		}

		ApplicationInfo appInfo = pkgInfo.applicationInfo;
		if (Build.VERSION.SDK_INT >= 8) {
			appInfo.sourceDir = apkFilepath;
			appInfo.publicSourceDir = apkFilepath;
		}
		return pm.getApplicationIcon(appInfo);
	}

	public static CharSequence getAppLabel(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return null;
		}
		ApplicationInfo appInfo = pkgInfo.applicationInfo;
		if (Build.VERSION.SDK_INT >= 8) {
			appInfo.sourceDir = apkFilepath;
			appInfo.publicSourceDir = apkFilepath;
		}

		return pm.getApplicationLabel(appInfo);
	}

	public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pkgInfo;
	}

}
