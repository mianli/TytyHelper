package com.mli.crown.tytyhelper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.ApkInfoUtils;
import com.mli.crown.tytyhelper.tools.FileUtils;
import com.mli.crown.tytyhelper.tools.Log;
import com.mli.crown.tytyhelper.tools.download.DownloadHelper;
import com.mli.crown.tytyhelper.tools.download.DownloadStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by crown on 2017/3/26.
 */
public class DownListAdapter extends BaseAdapter {

	private static final int PERIOD_TIME = 1000;
	private Handler mHandler = new Handler();
	private Context mContext;
	private List<DownloadStatus> mDownloadStatuses = new ArrayList<>();

	private boolean mDownDone;

	Timer mTimer = new Timer();

	public DownListAdapter(Context context, List<DownloadStatus> downloadStatuses) {
		setDownlaodStatuses(downloadStatuses);
		this.mContext = context;
	}

	public void notifyDataSetChanged(List<DownloadStatus> downloadStatuses) {
		setDownlaodStatuses(downloadStatuses);
		if(mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		}, new Date(), PERIOD_TIME);
	}

	private void setDownlaodStatuses(List<DownloadStatus> downloadStatuses) {
		if(downloadStatuses == null) {
			return;
		}
		mDownloadStatuses.clear();
		mDownloadStatuses.addAll(downloadStatuses);
	}

	@Override
	public int getCount() {
		return mDownloadStatuses != null ? mDownloadStatuses.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final DownloadStatus status = mDownloadStatuses.get(position);
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download, parent, false);
			viewHolder.mIconImgv = (ImageView) convertView.findViewById(R.id.item_download_apk_icon_imgv);
			viewHolder.mFileNameTv = (TextView) convertView.findViewById(R.id.item_download_filename);
			viewHolder.mSpeedTv = (TextView) convertView.findViewById(R.id.item_download_speed);
			viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.item_download_progressbar);
			viewHolder.mControlBtn = (Button) convertView.findViewById(R.id.item_download_pause);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		for (DownloadStatus downloadStatus : mDownloadStatuses) {
			mDownDone = true;
			if(!downloadStatus.done) {
				mDownDone = false;
				break;
			}
		}
		if(mDownDone) {
			if(mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
		}

//		viewHolder.mIconImgv.setTag(status.mHelper.getUrl());
		if (status.done && status.contentSize != 0) {
			viewHolder.mFileNameTv.setText(status.fileName);

			viewHolder.mProgressBar.setVisibility(View.GONE);
			viewHolder.mSpeedTv.setVisibility(View.GONE);
			viewHolder.mControlBtn.setText("安装");

			viewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mDownDone) {
						test();
					}
				}
			});
		} else if(!status.failed){
			viewHolder.mIconImgv.setVisibility(View.GONE);
			if(status.speed != null) {
				viewHolder.mSpeedTv.setText("速度:" + status.speed);
			}else {
				viewHolder.mSpeedTv.setText("");
			}
			if(status.mHelper.getStatus() == DownloadHelper.eStatus.downloading) {
				viewHolder.mControlBtn.setText("暂停");
			}else if(status.mHelper.getStatus() == DownloadHelper.eStatus.pause){
				viewHolder.mControlBtn.setText("继续");
			}else if(status.mHelper.getStatus() == DownloadHelper.eStatus.noStart) {
				viewHolder.mControlBtn.setText("开始");
			}

			viewHolder.mFileNameTv.setText(status.fileName);
			viewHolder.mProgressBar.setVisibility(View.VISIBLE);
			viewHolder.mSpeedTv.setVisibility(View.VISIBLE);
			int percent = (int) (1.f * status.totalSize / status.contentSize * 100);
			viewHolder.mProgressBar.setProgress(percent);

			viewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (status.mHelper != null) {
						status.mHelper.downloadFile();
					}
				}
			});
		}else {
			viewHolder.mFileNameTv.setText(status.mHelper.getUrl());
			viewHolder.mSpeedTv.setText("下载失败");
			viewHolder.mSpeedTv.setVisibility(View.VISIBLE);
			viewHolder.mControlBtn.setText("重试");
			viewHolder.mProgressBar.setVisibility(View.GONE);
			viewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(status.mHelper != null) {
						status.mHelper.start();
					}
				}
			});
		}

		return convertView;
	}

	private void test() {
		Intent intent = new Intent(mContext, ApksInfoActivity.class);
		intent.putExtra("u1", mDownloadStatuses.get(0).mHelper.getUrl());
		intent.putExtra("u2", mDownloadStatuses.get(1).mHelper.getUrl());
		intent.putExtra("u3", mDownloadStatuses.get(2).mHelper.getUrl());
		intent.putExtra("u4", mDownloadStatuses.get(3).mHelper.getUrl());
		intent.putExtra("u5", mDownloadStatuses.get(4).mHelper.getUrl());
		intent.putExtra("u6", mDownloadStatuses.get(5).mHelper.getUrl());
		intent.putExtra("u7", mDownloadStatuses.get(6).mHelper.getUrl());
		mContext.startActivity(intent);
	}
	class ViewHolder {
		ImageView mIconImgv;
		TextView mFileNameTv;
		TextView mSpeedTv;
		ProgressBar mProgressBar;
		Button mControlBtn;
	}

	class IconLoader {

		Map<Drawable, ImageView> mViewMap = new HashMap<>();
		Drawable mIcon;
		TextView mTextView;

		public IconLoader(ImageView imageView, TextView textView, String url) {
			Drawable apkIcon = ApkInfoUtils.getAppIcon(mContext,
				FileUtils.getDownloadFile(url).getAbsolutePath());
			if(apkIcon != null) {
				mIcon = apkIcon;
				mViewMap.put(apkIcon, imageView);
			}
			mTextView = textView;
			loadIcon();
		}

		public synchronized void loadIcon() {
			if(mViewMap.values().size() != 0) {
				try {
					mViewMap.get(mIcon).setImageDrawable(mIcon);
					mViewMap.get(mIcon).setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			CharSequence apkName = ApkInfoUtils.getAppLabel(mContext,
//				FileUtils.getDownloadFile(mUrl).getAbsolutePath());
//			if(apkName != null) {
//				mTextView.setText(apkName + "\n" + FileUtils.getFilename(mUrl) + " 下载已完成");
//			}
		}
	}
}