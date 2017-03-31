package com.mli.crown.tytyhelper.tools.download;

import android.provider.BaseColumns;

/**
 * Created by crown on 2017/3/23.
 */
public class DownloadBean implements BaseColumns{

	public static final String URL = "url";
	public static final String TOTAL_LENGTH = "totalLength";
	public static final String BREAK_POINTS = "breakPoints";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";

	private long mId;
	private String mUrl;
	private long mTotalLength;
	private long mBreakPoints;
	private long mStartTime;
	private long mEndTime;

	public DownloadBean(long id, String url, long totalLength, long breakPoints, long startTime, long endTime) {
		this.mId = id;
		this.mUrl = url;
		this.mTotalLength = totalLength;
		this.mBreakPoints = breakPoints;
		this.mStartTime = startTime;
		this.mEndTime = endTime;
	}

	public long getid() {
		return mId;
	}
	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public long getTotalLength() {
		return mTotalLength;
	}

	public void setTotalLength(long totalLength) {
		this.mTotalLength = totalLength;
	}

	public long getBreakPoints() {
		return mBreakPoints;
	}

	public void setBreakPoints(long breakPoints) {
		this.mBreakPoints = breakPoints;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public void setStartTime(long startTime) {
		this.mStartTime = startTime;
	}

	public long getEndTime() {
		return mEndTime;
	}

	public void setEndTime(long endTime) {
		this.mEndTime = endTime;
	}

	@Override
	public String toString() {
		return "url:" + mUrl + " tatalLength:" + mTotalLength + "breakPoints:" + mBreakPoints
			+ "startTime:" + mStartTime + "endTime:" + mEndTime;
	}
}
