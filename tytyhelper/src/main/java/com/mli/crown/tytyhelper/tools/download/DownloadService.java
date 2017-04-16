package com.mli.crown.tytyhelper.tools.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mli.crown.tytyhelper.activity.DownListActivity;
import com.mli.crown.tytyhelper.tools.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by crown on 2017/3/23.
 */
public class DownloadService extends Service{

	public static final String DOWNLOAD_URL = "url";

	private DownListActivity mInteractionActivity;

	private Map<String, DownloadStatus> downloadList = new HashMap<>();

	private DownloadBind mBinder = new DownloadBind();
	public class DownloadBind extends Binder {
		public List<DownloadStatus> getDownlist() {
			return new ArrayList<>(downloadList.values());
		}

		public void setInteractionActivity(DownListActivity activity) {
			mInteractionActivity = activity;
		}

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null) {
			MyToast.shortShow(this, "intent is null");
			return killSelf(intent, flags, startId);
		}
		String url = intent.getStringExtra(DOWNLOAD_URL);
		if(url == null) {
			MyToast.shortShow(this, "链接获取失败");
			return killSelf(intent, flags, startId);
		}

		DownloadStatus downloadHelper;
		if(!downloadList.containsKey(url)) {
			downloadHelper = new DownloadStatus(new DownloadHelper(this, url));
			downloadList.put(url, downloadHelper);
		}else {
			downloadHelper = downloadList.get(url);
		}
		downloadHelper.mHelper.setDownlistener(downloadHelper.mUpdateListener);
		downloadHelper.mHelper.start();

		if(mInteractionActivity != null) {
			mInteractionActivity.updateList(new ArrayList<>(downloadList.values()));
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private int killSelf(Intent intent, int flags, int startId) {
		int onstart = super.onStartCommand(intent, flags, startId);
		stopSelf();
		return onstart;
	}

	@Override
	public void onDestroy() {
		for (DownloadStatus downloadHelper : downloadList.values()) {
			downloadHelper.mHelper.pauseDownload();
		}
		super.onDestroy();
	}
}
