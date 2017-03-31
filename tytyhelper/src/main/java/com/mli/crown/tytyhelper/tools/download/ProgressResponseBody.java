package com.mli.crown.tytyhelper.tools.download;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by crown on 2017/3/23.
 */
public class ProgressResponseBody extends ResponseBody {

	public interface ProgressListener {
		void onPreExcute(long contentLength);
		void update(long totalBytes, boolean done);
		void failure(Call call, IOException e);
	}

	private ResponseBody responseBody = null;
	private ProgressListener progressListener = null;
	private BufferedSource bufferedSource;

	public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
		this.responseBody = responseBody;
		this.progressListener = progressListener;
		if(progressListener != null) {
			progressListener.onPreExcute(contentLength());
		}
	}

	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() {
		if(bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytes = 0L;
			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				long bytesRead = super.read(sink, byteCount);
				totalBytes += bytesRead != -1 ? bytesRead : 0;
				if(progressListener != null) {
					progressListener.update(totalBytes, bytesRead == -1);
				}
				return bytesRead;
			}
		};
	}

}
