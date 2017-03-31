package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by crown on 2017/3/20.
 */
public class MyToast {

	public static void shortShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void longShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

}
