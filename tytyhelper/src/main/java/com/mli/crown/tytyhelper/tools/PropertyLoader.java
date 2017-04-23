package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by crown on 2017/3/11.
 */
public class PropertyLoader {

	private static final String PROPERTY_FILENAME = "property.txt";
	public static String getDownloadUrl(Context context) {
		return getStr(getInputstream(context, "property.txt"));
	}

	private static InputStream getInputstream(Context context, String fileName) {
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open(fileName);
			return is;
		} catch (IOException e) {
			Log.i("PropertyLoader", "AssetManager load file failed");
			return null;
		}
	}

	public static String getPropertyText(Context context, int line) {
		return getPropertyText(context, line, null);
	}

	public static String getPropertyText(Context context, int line, String remove) {
		InputStream is = getInputstream(context, "property.txt");
		if(is == null) {
			return null;
		}

		int finishLine = 0;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String content = null;
		try {
			while ((content = bufferedReader.readLine()) != null) {
				if(finishLine == line) {
					sb.append(content);
					break;
				}
				++finishLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String result = sb.toString();
		if(!TextUtils.isEmpty(remove)) {
			int position = sb.toString().indexOf(remove);
			if(position != -1) {
				int length = remove.length();
				result = sb.toString().substring(length, sb.toString().length());
			}
		}
		return result;
	}

	private static String getStr(InputStream is) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String content = null;
		try {
			while ((content = bufferedReader.readLine()) != null) {
				sb.append(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
