package com.mli.crown.tytyhelper.tools.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by crown on 2017/3/23.
 */
public class ProgressDownloader {

	public static final String TAG = "ProgressDownloader";

	private ProgressResponseBody.ProgressListener mProgressListener;
	private String mUrl;
	private OkHttpClient mClient;
	private File mDestination;
	private Call mCall;

	public ProgressDownloader(String url, File destination, ProgressResponseBody.ProgressListener progressListener) {
		this.mUrl = url;
		this.mDestination = destination;
		this.mProgressListener = progressListener;

		mClient = getProgressClient();
	}

	private Call newCall(long startPos) {
		Request request = new Request.Builder().url(mUrl)
			.header("RANGE", "bytes=" + startPos + "-")
			.build();
		return mClient.newCall(request);
	}

	public OkHttpClient getProgressClient() {
		Interceptor interceptor = new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());
				return originalResponse.newBuilder()
					.body(new ProgressResponseBody(originalResponse.body(), mProgressListener))
					.build();
			}
		};

		return new OkHttpClient.Builder()
			.addNetworkInterceptor(interceptor)
			.build();
	}

	public void download(final long startPos) {
		mCall = newCall(startPos);
		mCall.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				mProgressListener.failure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				save(response, startPos);
			}
		});
	}

	public void pause() {
		if(mCall != null) {
			mCall.cancel();
		}
	}

	public boolean canDownload() {
		if(mCall == null || mCall.isCanceled()) {//|| !mCall.isExecuted()
			return true;
		}
		return false;
	}

	public boolean canPause() {
		if(mCall != null && mCall.isExecuted() && !mCall.isCanceled()) {
			return true;
		}
		return false;
	}

	public void finish() {
		mCall.cancel();
	}

	public boolean shouldPause() {
		return mCall != null && mCall.isExecuted();
	}

	private void save(Response response, long startPos) {
		ResponseBody body = response.body();
		InputStream in = body.byteStream();
		FileChannel fileChannel = null;
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(mDestination, "rwd");
			fileChannel = randomAccessFile.getChannel();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startPos,
				body.contentLength());
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				mappedByteBuffer.put(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				if(fileChannel != null) {
					fileChannel.close();
				}
				if(randomAccessFile != null) {
					randomAccessFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
