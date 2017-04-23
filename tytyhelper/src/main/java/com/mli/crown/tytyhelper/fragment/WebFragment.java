package com.mli.crown.tytyhelper.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.PropertyLoader;
import com.mli.crown.tytyhelper.tools.Utils;
import com.mli.crown.tytyhelper.tools.download.DownloadService;
import com.mli.crown.tytyhelper.tools.iResultListener;

/**
 * Created by mli on 2017/4/22.
 */

public class WebFragment extends Fragment {

    WebView mWebview;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final String url = PropertyLoader.getPropertyText(getActivity(), 1, "webpage:");

        mView = inflater.inflate(R.layout.fragment_web, container, false);
        final SwipeRefreshLayout refreshLayout = Utils.findView(mView, R.id.web_swiperefresh_container);
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
                                    startDownlaod(url);
                                }
                            }).show();
                }else if(result == 1){
                    new AlertDialog.Builder(getActivity()).setTitle("移动网络状态").
                            setMessage("是否在移动网络状态下进行下载？").
                            setNegativeButton("取消", null).
                            setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startDownlaod(url);
                                }
                            }).show();
                }else {
                    MyToast.shortShowCenter(getActivity(), "请检查网络状态");
                }
            }
        });
    }

    private void startDownlaod(String url) {
        Intent downIntent = new Intent(getActivity(), DownloadService.class);
        downIntent.putExtra(DownloadService.DOWNLOAD_URL, url);
        getActivity().startService(downIntent);
    }

    private void checkNetworkState(iResultListener<Integer> listener) {
        listener.getResult(Utils.getNetworkStatus(getActivity()));
    }

}
