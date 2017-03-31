package com.mli.crown.tytyhelper.tools;

/**
 * Created by crown on 2017/3/21.
 */
public class Config {

	// FIXME: 2017/3/29 放入application或者换做其他方式进行缓存
	public static String USERNAME;
	public static String PASSWORD;
	public static String DESC;

	public static final String THIRD_PART_APP_NAME = "com.lingshi.inst.kids";
	private static final String PREFIX_PACKAGE = "com.lingshi.tyty.inst";
	public static final String LOGIN_ACTIVITY = PREFIX_PACKAGE + ".activity.UserLoginRegistActivity";
	public static final String MAIN_ACTIVITY = PREFIX_PACKAGE + ".activity.MainActivity";
	public static final String USER_PROFILE_ACTIVITY = "com.lingshi.tyty.inst.ui.user.mine"
		+ ".StuProfileActivity";
	public static final String TEACHER_PROFILE_ACTIVITY = "com.lingshi.tyty.inst.activity"
		+".MyProfileActivity";
	public static final String DIALOG = "com.lingshi.tyty.common.customView.MDialog";
	public static final String SPLASH_ACTIVITY = "com.lingshi.inst.kids.activity.SplashActivity";

}
