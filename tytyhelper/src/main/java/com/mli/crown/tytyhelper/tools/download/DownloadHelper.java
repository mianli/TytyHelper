package com.mli.crown.tytyhelper.tools.download;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.fragment.DownloadFragment;
import com.mli.crown.tytyhelper.tools.FileUtils;
import com.mli.crown.tytyhelper.tools.MyToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by crown on 2017/3/23.
 */
public class DownloadHelper implements ProgressResponseBody.ProgressListener{

	public enum eStatus {
		noInit,
		noStart,
		downloading,
		pause,
		finish
	}

	private static final int PERIOD_UPDATE_NOTIFICATION = 1000;

	private Context mContext;
	private String mUrl;
	private ProgressDownloader progressDownloader;
	private long mBreakPoints;
	private File mFile;
	private long mTotalBytes;
	private long mContentLength;

	private eStatus mStatus = eStatus.noInit;

	private RemoteViews mRemoteViews;

	private NotificationCompat.Builder mBuilder;

	private Timer mTimer;

	private int mNotificationid = 100;// FIXME: 2017/3/26 用户的通知数超过这个数字应该会出现问题,概率小暂不处理

	private DownloadDbHelper mDownloadDbHelper;

	private long preSize = 0;
	private iDownloadStatusListener mDownLoadStatusListener;

	public DownloadHelper(Context context, String url) {
		this.mContext = context;
		this.mUrl = url;

		mDownloadDbHelper = new DownloadDbHelper(mContext);

		mFile = FileUtils.getDownloadFile(mUrl);
		progressDownloader = new ProgressDownloader(url, mFile, this);
	}

	public String getUrl() {
		return mUrl;
	}

	public eStatus getOriginStatus() {
		DownloadBean bean = mDownloadDbHelper.searchUrl(mUrl);
		if(bean == null) {
			return eStatus.noInit;
		}else {
			if(bean.getBreakPoints() == 0) {
				return eStatus.noStart;
			}else if(bean.getBreakPoints() < bean.getTotalLength()) {
				return eStatus.pause;
			}else{
				return eStatus.finish;
			}
		}
	}

	public eStatus getStatus() {
		return mStatus;
	}

	public static void download(Context context, String url) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(DownloadService.DOWNLOAD_URL, url);
		context.startService(intent);
	}

	public void downloadFile() {
		DownloadBean downloadBean = mDownloadDbHelper.searchUrl(mUrl);
		if (progressDownloader.canDownload()){
			if(downloadBean == null){
				if(FileUtils.fileExist(mUrl)) {//首次下载检测到文件已经存在了
					showReDownloadAlert();
					return;
				}
				startDownload();
//				MyToast.shortShowCenter(mContext, "开始下载");
			}else if(downloadBean.getBreakPoints() < downloadBean.getTotalLength()) {
				//mContentLength = 0表示还没有下载过,比如已经已经存在同一的文件在相同的路径
				//如果下载中途用户暂停并之后被用户删除文件数据，此时下载后的文件是不能使用的
				continueDownload();//如果文件存在，继续下载
//				MyToast.shortShowCenter(mContext, "继续下载");
			}else {
				if(!FileUtils.fileExist(mUrl)) {//被删除或者下载失败
//					MyToast.shortShowCenter(mContext, "文件已经被删除,已经开始重新下载");
					startDownload();
				}else {
					if(mDownLoadStatusListener != null) {
						mDownLoadStatusListener.update(getFileName(), -1, -1, null, true);
					}
				}
			}
		}else if(progressDownloader.canPause()){
			pauseDownload();
//			MyToast.shortShowCenter(mContext, "暂停下载");
		}else {
			MyToast.shortShowCenter(mContext, "这是什么状态，——啊！逻辑错误啦~");
		}
	}

	private void showReDownloadAlert() {
		//Note 当前只有DownloadActivity进行下载，如果有其他的Activity请修改该处
		new AlertDialog.Builder(DownloadFragment.mDownloadActivity)
			.setTitle("文件已经存在")
			.setMessage("是否重新下载")
			.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					FileUtils.getDownloadFile(mUrl).delete();//文件已经存在，重新下载，因为可能是没有下载成功的文件
					startDownload();
				}
			})
			.setNegativeButton("否", null)
			.show();
	}

	private void startDownload() {
		if(mDownloadDbHelper.isExist(mUrl)) {
			//文件存在会被重新下载
			mDownloadDbHelper.updateBreakPoints(mUrl, 0);
		}else {
			mDownloadDbHelper.saveDownloadInfo(mUrl, 0);
		}

		mStatus = eStatus.downloading;
		mBreakPoints = 0L;
		progressDownloader.download(mBreakPoints);
	}

	public void pauseDownload() {
		mStatus = eStatus.pause;
		mDownloadDbHelper.updateBreakPoints(mUrl, mTotalBytes);
		progressDownloader.pause();
	}

	private void continueDownload() {
		if(mUrl == null) {
			return;
		}
		mStatus = eStatus.downloading;
		DownloadBean downloadBean = mDownloadDbHelper.searchUrl(mUrl);
		if(downloadBean != null) {
			progressDownloader.download(mBreakPoints = downloadBean.getBreakPoints());
		}else {
			//重新下载
			startDownload();
		}
	}

	private String fileName;
	private String getFileName() {
		if(fileName == null) {
			fileName = FileUtils.getFilename(mUrl);
		}
		return fileName;
	}

	@Override
	public void onPreExcute(long contentLength) {
		if(this.mContentLength == 0) {
			this.mContentLength = contentLength;
			if(!FileUtils.fileExist(mUrl)) {
				mDownloadDbHelper.updateContentLength(mUrl, contentLength);
			}else {//如果文件已经存在了，后面将重新下载,提前将数据库中的开始点设置为0
				mDownloadDbHelper.updateBreakPoints(mUrl, 0);
			}
		}
	}

	@Override
	public void update(final long totalBytes, final boolean done) {
		this.mTotalBytes = totalBytes + mBreakPoints;
		if(mDownLoadStatusListener != null) {
			if(mTimer == null) {
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (DownloadHelper.this.mTotalBytes != mContentLength) {
							int speed = (int) ((DownloadHelper.this.mTotalBytes - preSize) / 1024);
							float speedM;
							String speedText;
							if (speed > 1024) {
								speedM = speed / 1024.0f;
								speedText = String.format("%.1fM/s", speedM);
							} else if(speed > 0){
								speedText = String.format("%dk/s", speed);
							}else {
								speedText = "0k/s";
							}
							preSize = DownloadHelper.this.mTotalBytes;
							mDownLoadStatusListener.update(getFileName(),
								mContentLength, DownloadHelper.this.mTotalBytes, speedText,
								mContentLength == DownloadHelper.this.mTotalBytes);
						}

					}
				}, PERIOD_UPDATE_NOTIFICATION, PERIOD_UPDATE_NOTIFICATION);
			}
		}
		if(done) {
			mStatus = eStatus.finish;
			if(mTimer != null) {
				mTimer.cancel();
				mTimer = null;
				mDownLoadStatusListener.update(getFileName(), mContentLength, DownloadHelper.this.mTotalBytes, "",
					mContentLength == DownloadHelper.this.mTotalBytes);
			}
			progressDownloader.finish();
			DownloadBean downloadBean = mDownloadDbHelper.searchUrl(mUrl);
			mDownloadDbHelper.updateBreakPoints(mUrl, downloadBean.getTotalLength());
//			stopService();
		}
	}

	@Override
	public void failure(Call call, IOException e) {
		pauseDownload();
		if(mDownLoadStatusListener != null) {
			mDownLoadStatusListener.failure(call, e);
		}
	}

	public interface iDownloadStatusListener {
		void update(String filename, long contentLength, long totalBytes, String speed, boolean done);
		void failure(Call call, IOException e);
	}

	public void setDownlistener(iDownloadStatusListener listener) {
		if(mDownLoadStatusListener == null) {
			this.mDownLoadStatusListener = listener;
		}
	}

	private long getFileSize(File file) {
		if(file.exists()) {
			try {
				FileInputStream is = new FileInputStream(file);
				long size = is.available();
				return size;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
		}
		return 0;
	}

	public void start() {
		//顺序有逻辑关系
		downloadFile();
//		sendNotification();
//		updateNotification();
	}

	private void sendNotification() {
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("");
		mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download);
		mRemoteViews.setImageViewResource(R.id.notification_download_iamgeview, R.drawable.notication);
		mRemoteViews.setProgressBar(R.id.notification_download_progressbar, 100, 0, false);
		mRemoteViews.setTextViewText(R.id.notification_download_filename, FileUtils.getFilename(mUrl));
		mRemoteViews.setTextViewText(R.id.notification_download_speed, "下载速度");
		mBuilder.setContent(mRemoteViews);

		DownloadBean bean = mDownloadDbHelper.searchUrl(mUrl);
		if(bean != null) {
			mNotificationid = (int) bean.getid();
		}

		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(mNotificationid, mBuilder.build());
//		mContext.startForeground(100, mBuilder.build());
	}

	private void updateNotification() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				int percent = (int) (1.f * mTotalBytes / mContentLength * 100);
				if (percent >= 100) {
					mRemoteViews.setTextViewText(R.id.notification_download_speed, "下载已经完成");
					mRemoteViews.setTextViewText(R.id.notification_download_filename, FileUtils.getFilename(mUrl));
//					stopForeground(false);
//					if(mTimer != null) {
					mTimer.cancel();
//					}
				} else {
					int speed = (int) ((mTotalBytes - preSize) / 1024);
					float speedM = 0;
					String speedText;
					if (speed > 1024) {
						speedM = speed / 1024.0f;
						speedText = String.format("下载速度：%.1fM/s", speedM);
					} else {
						speedText = String.format("下载速度：%dk/s", speed);
					}
					preSize = mTotalBytes;
					mRemoteViews.setTextViewText(R.id.notification_download_filename, FileUtils.getFilename(mUrl));
					mRemoteViews.setTextViewText(R.id.notification_download_speed, speedText);
				}
				mRemoteViews.setProgressBar(R.id.notification_download_progressbar, 100, percent, false);
				NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(mNotificationid, mBuilder.build());
			}
		}, PERIOD_UPDATE_NOTIFICATION, PERIOD_UPDATE_NOTIFICATION);
	}

}
