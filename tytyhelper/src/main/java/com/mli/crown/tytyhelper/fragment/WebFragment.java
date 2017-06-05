package com.mli.crown.tytyhelper.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.MainActivity;
import com.mli.crown.tytyhelper.tools.ApkInfoUtils;
import com.mli.crown.tytyhelper.tools.DataManager;
import com.mli.crown.tytyhelper.tools.FileUtils;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.PropertyLoader;
import com.mli.crown.tytyhelper.tools.Utils;
import com.mli.crown.tytyhelper.tools.download.DownloadHelper;
import com.mli.crown.tytyhelper.tools.download.DownloadService;
import com.mli.crown.tytyhelper.tools.download.DownloadStatus;
import com.mli.crown.tytyhelper.tools.download.iDownItemStatusListener;
import com.mli.crown.tytyhelper.tools.download.iGetUpdateListListener;
import com.mli.crown.tytyhelper.tools.iResultListener;

import java.io.File;
import java.util.List;

/**
 * Created by mli on 2017/4/22.
 */

public class WebFragment extends Fragment implements iGetUpdateListListener {

    public static final String FRAGMENT_TAG = "WebFragment";
    private static final String FILE_NAME = "downloadApk";
    private static final String APK_NAME = "apk";

    private DownloadService.DownloadBind mBinder;
    private WebView mWebview;
    private View mView;
    private Handler mHandler;

    private RemoteViews mRemoteViews;

    private NotificationCompat.Builder mBuilder;

    private long mPreSize;

    private ViewHolder mViewHolder;

    class ViewHolder {
        TextView mFileNameTv;
        TextView mSpeedTv;
        ProgressBar mProgressBar;
        Button mControlBtn;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final String url = PropertyLoader.getPropertyText(getActivity(), 1, "webpage:");

        mView = inflater.inflate(R.layout.fragment_web, container, false);
        final SwipeRefreshLayout refreshLayout = Utils.findView(mView, R.id.web_swiperefresh_container);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_color));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebview.reload();
            }
        });

        mWebview = Utils.findView(mView, R.id.web_webview);
        mWebview.loadUrl(url);

        WebSettings settings = mWebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Utils.getFilename(getActivity(), url, new Utils.GetFilenameTask.iGetResultListener() {
                    @Override
                    public void getFilename(String result) {
                        if(result != null) {
                            preDownlaod(url, result);
                        }else {
//                            view.loadUrl(url);
                        }
                    }
                });
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
            }
        });

        // FIXME: 2017/4/24
        DownloadFragment.mDownloadActivity = getActivity();

        mViewHolder = new ViewHolder();
        mViewHolder.mFileNameTv = Utils.findView(mView, R.id.web_filename);
        mViewHolder.mSpeedTv = Utils.findView(mView, R.id.web_speed);
        mViewHolder.mProgressBar = Utils.findView(mView, R.id.web_progressbar);
        mViewHolder.mControlBtn = Utils.findView(mView, R.id.web_control_btn);

        final String filename = DataManager.getValue(getActivity(), FILE_NAME, APK_NAME);
        if(filename != null) {
            mViewHolder.mProgressBar.setProgress(100);
            mViewHolder.mFileNameTv.setText(filename + " 已下载");
            mViewHolder.mControlBtn.setText("安装");
            mViewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApkInfoUtils.install(getActivity(), filename);
                }
            });
        }

        Intent downIntent = new Intent(getActivity(), DownloadService.class);
        getActivity().bindService(downIntent, mConnection, Activity.BIND_AUTO_CREATE);

        mHandler = new Handler();

        return mView;
    }

    private void preDownlaod(final String url, final String filename) {
        checkNetworkState(new iResultListener<Integer>() {
            @Override
            public void getResult(Integer result) {
                if(result == 0) {
                    new AlertDialog.Builder(getActivity()).setTitle("下载").
                            setMessage("下载文件：" + filename + "?").
                            setNegativeButton("取消", null).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startDownlaod(url, filename);
                                }
                            }).show();
                }else if(result == 1){
                    new AlertDialog.Builder(getActivity()).setTitle("移动网络状态").
                            setMessage("是否在移动网络状态下进行下载？").
                            setNegativeButton("取消", null).
                            setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startDownlaod(url, filename);
                                }
                            }).show();
                }else {
                    MyToast.shortShowCenter(getActivity(), "请检查网络状态");
                }
            }
        });
    }

    private void startDownlaod(String url, String filename) {

//        File file = FileUtils.getFile(filename);
//        if(file != null) {
//            file.delete();//重新下载
//        }

        Intent downIntent = new Intent(getActivity(), DownloadService.class);
        downIntent.putExtra(DownloadService.DOWNLOAD_URL, url);
        getActivity().startService(downIntent);

        sendNotification(filename);
    }

    private void checkNetworkState(iResultListener<Integer> listener) {
        listener.getResult(Utils.getNetworkStatus(getActivity()));
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service instanceof DownloadService.DownloadBind) {
                mBinder = (DownloadService.DownloadBind) service;
                mBinder.setUpdateListListener(WebFragment.this);

                if(mBinder == null) {
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadStatus(mBinder.getDownlist());
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void updateList(List<DownloadStatus> helpers) {
        uploadStatus(helpers);
    }

    private void uploadStatus(List<DownloadStatus> downlist) {
        if(downlist != null && downlist.size() > 0) {
            final DownloadStatus mStatus = downlist.get(0);
            mStatus.setStatusListener(new iDownItemStatusListener() {
                @Override
                public void update(final DownloadStatus status) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateNotification(mStatus);
                            int percent = (int) (1.f * status.totalSize / status.contentSize * 100);

                            if(percent == 100) {
                                DataManager.saveValue(getActivity(), FILE_NAME, APK_NAME, status.fileName);

                                mViewHolder.mFileNameTv.setText(status.fileName + "下载已完成");
                                mViewHolder.mSpeedTv.setText("");
                                mViewHolder.mProgressBar.setProgress(percent);
                                mViewHolder.mControlBtn.setText("安装");
                                mViewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ApkInfoUtils.installApk(getActivity(), status.mHelper.getUrl());
                                    }
                                });
                            }else if(percent != 0){
                                mViewHolder.mFileNameTv.setText(status.fileName + "正在下载");
                                mViewHolder.mSpeedTv.setText("速度:" + status.speed);
                                mViewHolder.mControlBtn.setText(mStatus.mHelper.getStatus() == DownloadHelper.eStatus.downloading ? "暂停" : "继续");
                                mViewHolder.mProgressBar.setProgress(percent);
                                mViewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mStatus.mHelper.start();
                                    }
                                });
                            }else {
                                mViewHolder.mControlBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }

    private void sendNotification(String filename) {
        mBuilder = new NotificationCompat.Builder(getActivity());
        mBuilder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("");
        mRemoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.notification_download);
        mRemoteViews.setImageViewResource(R.id.notification_download_iamgeview, R.drawable.notication);
        mRemoteViews.setProgressBar(R.id.notification_download_progressbar, 100, 0, false);
        mRemoteViews.setTextViewText(R.id.notification_download_filename, filename);
        mRemoteViews.setTextViewText(R.id.notification_download_speed, "下载速度");
        mBuilder.setContent(mRemoteViews);

        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }

    private void updateNotification(DownloadStatus status) {

                int percent = (int) (1.f * status.totalSize / status.contentSize * 100);
                if (percent >= 100) {

                    DataManager.saveValue(getActivity(), FILE_NAME, APK_NAME, status.fileName);

                    mRemoteViews.setTextViewText(R.id.notification_download_speed, "下载已经完成");
                    mRemoteViews.setTextViewText(R.id.notification_download_filename, status.fileName);

                    NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(0);

                } else {

                    int speed = (int) ((status.totalSize - mPreSize) / 1024);
                    if(speed < 0) {
                        speed = 0;
                    }

                    float speedM = 0;
                    String speedText;
                    if (speed > 1024) {
                        speedM = speed / 1024.0f;
                        speedText = String.format("下载速度：%.1fM/s", speedM);
                    } else {
                        speedText = String.format("下载速度：%dk/s", speed);
                    }
                    mPreSize = status.totalSize;
                    mRemoteViews.setTextViewText(R.id.notification_download_filename, status.fileName);
                    mRemoteViews.setTextViewText(R.id.notification_download_speed, speedText);

                    mRemoteViews.setProgressBar(R.id.notification_download_progressbar, 100, percent, false);
                    NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, mBuilder.build());
                }
    }

}
