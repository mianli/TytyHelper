package com.mli.crown.tytyhelper.bean;

/**
 * Created by crown on 2017/3/22.
 */
public class LoginInfo extends SimpleLoginInfo implements iLoginInfo {

	private long mTime;

	public LoginInfo(String username, String password, String desc, long time) {
		super(username, password, desc);
		this.mTime = time;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}

	@Override
	public String toString() {
		return null;
	}
}
