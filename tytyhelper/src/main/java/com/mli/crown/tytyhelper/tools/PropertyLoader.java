package com.mli.crown.tytyhelper.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by crown on 2017/3/11.
 */
public class PropertyLoader {

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
