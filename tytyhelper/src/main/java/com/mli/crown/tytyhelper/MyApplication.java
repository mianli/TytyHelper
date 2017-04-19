package com.mli.crown.tytyhelper;

import android.app.Application;

import com.mli.crown.tytyhelper.tools.InfoManager;

/**
 * Created by crown on 2017/3/29.
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		InfoManager.clear(this);//每次打开清空存储的数据
	}

}
