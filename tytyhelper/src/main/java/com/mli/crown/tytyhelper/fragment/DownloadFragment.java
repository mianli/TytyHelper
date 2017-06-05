package com.mli.crown.tytyhelper.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.DownListActivity;
import com.mli.crown.tytyhelper.activity.MainActivity;
import com.mli.crown.tytyhelper.tools.download.DownloadHelper;

/**
 * Created by crown on 2017/3/23.
 */
public class DownloadFragment extends Fragment {

	public static final String FRAGMENT_TAG = "DownloadFragment";
	public static Activity mDownloadActivity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		View view1 = view.findViewById(R.id.download_btn);
		view1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				download();
			}
		});
		mDownloadActivity = getActivity();
		return view;
	}

	public void download() {
		startActivity(new Intent(getActivity(), DownListActivity.class));
	}

	public void printStatus(View view) {
		String url1 = "http://iiiview.com/instKids/download/package/TYTY.apk";
		DownloadHelper.download(getActivity(), url1);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		mDownloadActivity = null;
	}
}
