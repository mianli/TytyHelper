package com.mli.crown.tytyhelper.tools;

import android.app.Notification;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.mli.crown.tytyhelper.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by crown on 2017/3/11.
 */
public class DownloadHelper {

	private Handler mHandler;
	private Context mContext;

	public DownloadHelper(Context context) {
		this.mContext = context;
	}

	public static DownloadHelper newInstance(Context context) {
		return new DownloadHelper(context);
	}

	public void download(final String url, final iDownloadListener listener) {

		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setContentTitle(getFileName(url) + "下载中...");
		builder.setContentText("下载");
		builder.setSmallIcon(R.drawable.ic_launcher);

		mHandler = new Handler();
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(url).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				sendFailedCallback(listener, request, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				writeFile(response, getFileName(url), listener);
			}

		});
	}

	private void writeFile(Response response, String filename, final iDownloadListener listener) {
		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;

		long completeSize = 0;
		long fileSize = 0;// = response.body().contentLength();
		try
		{
			is = response.body().byteStream();
			String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			File file = new File(destFileDir, filename);
			fos = new FileOutputStream(file);
			while ((len = is.read(buf)) != -1)
			{
				fos.write(buf, 0, len);
				completeSize += len;
				Log.i("fileSize:" + len + "下载：" + (100.f * completeSize / fileSize) + "%");
			}
			fos.flush();
			sendSuccessCallback(listener, file.getPath());
		} catch (IOException e)
		{
			Log.i(e.getMessage());
		} finally
		{
			try
			{
				if (is != null) is.close();
			} catch (IOException e)
			{
				Log.i("文件关闭失败");
			}
			try
			{
				if (fos != null) fos.close();
			} catch (IOException e)
			{
				Log.i("文件关闭失败");
			}
		}
	}

	private String getFileName(String path)
	{
		int separatorIndex = path.lastIndexOf("/");
		return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
	}

	private void sendSuccessCallback(final iDownloadListener listener, final String path) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				listener.onSuccess(path);
			}
		});
	}

	private void sendFailedCallback(final iDownloadListener listener, final Request request, final IOException e) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				listener.onFailed(request, e);
			}
		});
	}

	public interface iDownloadListener {
		void onSuccess(String path);
		void onFailed(Request request, IOException e);
	}

}
