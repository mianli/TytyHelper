package com.mli.crown.tytyhelper.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.download.DownloadService;
import com.mli.crown.tytyhelper.tools.download.DownloadStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crown on 2017/3/26.
 */
public class DownListActivity extends AppCompatActivity{

	private List<String> mDownloadList = new ArrayList<>();

	private ListView mListView;
	private DownListAdapter mAdapter;
	private List<DownloadStatus> mDownList;
	private DownloadService.DownloadBind mBinder;

	private Handler mHandler = new Handler();

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(service instanceof DownloadService.DownloadBind) {
				mBinder = (DownloadService.DownloadBind) service;
				mBinder.setInteractionActivity(DownListActivity.this);

				if(mBinder == null) {
					return;
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged(mBinder.getDownlist());
					}
				});
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	public void updateList(List<DownloadStatus> helpers) {
		mDownList = helpers;
		mAdapter.notifyDataSetChanged(mDownList);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_downlist);
		mListView = (ListView) findViewById(R.id.downlist_listview);
		mListView.setAdapter(mAdapter = new DownListAdapter(this, mDownList));

		mDownloadList.add("http://android/12378/rsjy1237.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/tsxxw12378.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/tdyy12378.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/gxwsjy12378.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/sxdswy12378.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/rspx12378.apk");
		mDownloadList.add("http://gs.51tyty.com/android/12378/bbrwy12378.apk");

		if(savedInstanceState == null) {
			Intent downIntent = new Intent(this, DownloadService.class);
			bindService(downIntent, mConnection, BIND_AUTO_CREATE);
		}
	}

	public void bindService(View view) {
		for (String url : mDownloadList) {
			Intent downIntent = new Intent(this, DownloadService.class);
			downIntent.putExtra(DownloadService.DOWNLOAD_URL, url);
			startService(downIntent);
		}
	}

	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}
