package com.mli.crown.tytyhelper.tools;

/**
 * Created by crown on 2017/3/11.
 */
public class Log {

	private static final String LTAG = "tytyHelper log:";
	public static void i(String log) {
		android.util.Log.i(LTAG, log);
	}

}
