package com.mli.crown.tytyhelper.tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by crown on 2017/3/28.
 */
public class FileUtils {

	public static synchronized String getFilename(String url) {
		String path = new String(url);
		int separatorIndex = path.lastIndexOf("/");
		return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
	}

	public static synchronized File getDownloadFile(String url) {
		String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/0TytyHelper";
		File f = new File(destFileDir);
		if(!f.exists()) {
			f.mkdir();
		}
		return new File(destFileDir, FileUtils.getFilename(url));
	}

	public static boolean isExist(String filename) {
		File file = getFile(filename);
		return file.exists();
	}

	public static File getFile(String filename) {
		String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/0TytyHelper";
		File f = new File(destFileDir);
		if(!f.exists()) {
			f.mkdir();
		}
		File file = new File(destFileDir, filename);
		return file;
	}

	public static boolean fileExist(String url) {
		File file = FileUtils.getDownloadFile(url);
		if(file.exists()) {
			return true;
		}else {
			return false;
		}
	}

}
