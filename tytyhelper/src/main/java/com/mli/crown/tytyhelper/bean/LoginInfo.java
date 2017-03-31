package com.mli.crown.tytyhelper.bean;

import com.mli.crown.tytyhelper.tools.Config;

/**
 * Created by crown on 2017/3/22.
 */
public class LoginInfo implements iLoginInfo {

	private String mUsername;
	private String mPassword;
	private String mDesc;
	private long mTime;

	public LoginInfo(String username, String password, String desc, long time) {
		this.mUsername = username;
		this.mPassword = password;
		this.mDesc = desc;
		this.mTime = time;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String mUsername) {
		this.mUsername = mUsername;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		this.mDesc = desc;
	}

	public void resetConfigLoginInfo() {
		Config.USERNAME = getUsername();
		Config.PASSWORD = getPassword();
	}

	@Override
	public String toString() {
		return getUsername();
	}
}
