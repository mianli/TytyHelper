package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by crown on 2017/3/20.
 */
public class MyToast {

	public static void shortShowCenter(Context context, String text) {
		showCenter(context, text, Toast.LENGTH_SHORT);
	}

	public static void longShowCenter(Context context, String text) {
		showCenter(context, text, Toast.LENGTH_LONG);
	}

	public static void showCenter(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void shortShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void longShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

}
