package com.mli.crown.tytyhelper.tools.download;

import com.mli.crown.tytyhelper.tools.Log;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by crown on 2017/3/27.
 */
public class DownloadStatus {

	public DownloadHelper mHelper;
	public String fileName;
	public long contentSize;
	public long totalSize;
	public String speed;
	public boolean done;
	public boolean failed;
	public DownloadHelper.iDownloadStatusListener mUpdateListener;

	public DownloadStatus(DownloadHelper downloadHelper) {
		setDownloadHelper(downloadHelper);
	}

	public void setDownloadHelper(DownloadHelper downloadHelper) {
		this.mHelper = downloadHelper;
		mUpdateListener = new DownloadHelper.iDownloadStatusListener() {
			@Override
			public void update(String filename, long contentLength, long totalBytes, String speed, boolean done) {
				Log.i("--------->" + filename + ":" + speed + ":" + done);
				DownloadStatus.this.fileName = filename;
				DownloadStatus.this.contentSize = contentLength;
				DownloadStatus.this.totalSize = totalBytes;
				DownloadStatus.this.speed = speed;
				DownloadStatus.this.done = done;
				DownloadStatus.this.failed = false;
			}

			@Override
			public void failure(Call call, IOException e) {
				DownloadStatus.this.done = true;
				DownloadStatus.this.failed = true;
			}
		};
	}

}
