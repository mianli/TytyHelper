package com.mli.crown.tytyhelper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.ApkInfoUtils;
import com.mli.crown.tytyhelper.tools.FileUtils;

/**
 * Created by crown on 2017/3/28.
 */
public class ApksInfoActivity extends Activity{

	public static final String URL = "url";


	public static void start(Context context, String url) {
		Intent intent = new Intent(context, ApksInfoActivity.class);
		intent.putExtra(URL, url);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_apks_info);

		String url = getIntent().getStringExtra(URL);
		Drawable drawable = ApkInfoUtils.getAppIcon(this, FileUtils.getDownloadFile(url).getAbsolutePath());
		ImageView logoView = (ImageView) findViewById(R.id.apks_info_logo);
		logoView.setImageDrawable(drawable);

		TextView textview = (TextView) findViewById(R.id.apks_info_name);
		textview.setText(ApkInfoUtils.getAppLabel(this, FileUtils.getDownloadFile(url).getAbsolutePath())
		 + "\n" + url);
	}

}
