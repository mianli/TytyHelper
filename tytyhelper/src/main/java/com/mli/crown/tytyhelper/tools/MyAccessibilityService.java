package com.mli.crown.tytyhelper.tools;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;

import java.util.List;

/**
 * Created by crown on 2017/3/20.
 */
public class MyAccessibilityService extends AccessibilityService {

	private static final String PACKAGE_ID = "com.lingshi.inst.kids:id/";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		final String packageName = event.getPackageName().toString();
		final String className = event.getClassName().toString();
		if(packageName.equals(Config.THIRD_PART_APP_NAME) &&
			AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
			if(className.equals(Config.LOGIN_ACTIVITY)) {
				//登录
				listenerLogin();
			}else if(className.equals(Config.MAIN_ACTIVITY)) {
					listenerMainWillLogout();//有用户登录信息，表明要登出
			}else if(className.equals(Config.USER_PROFILE_ACTIVITY)) {
				//学生注销
				listenerStuLogout();
			}else if(className.equals(Config.TEACHER_PROFILE_ACTIVITY)) {
				//老师注销
				listenerTeacherLogout();
			}else if(className.contains(Config.COMMEN_PCKG)) {
				listenerLogout();//有用户登录信息，表明要登出
			}else if(className.equals(Config.SPLASH_ACTIVITY)) {

			}else if(className.contains("lingshi")
					&& className.contains("activity")){
//				backToMainWillLogint();
			}
		}
	}

	@Override
	public void onInterrupt() {
		MyToast.shortShowCenter(this, "辅助功能被中断,请重新开启或重启手机");
	}

	private void backToMainWillLogint() {
		if(!isLogoutAction()) {
			return;
		}
		performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
	}

	private void listenerMainWillLogout() {
		if(!isLogoutAction()) {
			return;
		}
		AccessibilityNodeInfo nodeInfo = getNodeInfoById("mian_user_head");
		if( nodeInfo != null) {
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		}
	}

	private void listenerLogout() {
		if(!isLogoutAction()) {
			return;
		}

		AccessibilityNodeInfo nodeInfo = getNodeInfoByText("退出当前用户？");
		if(nodeInfo != null) {
			AccessibilityNodeInfo logoutNodeInfo = getNodeInfoById("dialog_btn2");
			if(logoutNodeInfo != null) {
				logoutNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}
	}

	private void listenerStuLogout() {
		if(!isLogoutAction()) {
			return;
		}

		AccessibilityNodeInfo nodeInfo = getNodeInfoById("stu_profile_logout_btn");
		if( nodeInfo != null) {
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		}
	}

	private void listenerTeacherLogout() {
		if(!isLogoutAction()) {
			return;
		}

		AccessibilityNodeInfo nodeInfo = getNodeInfoById("user_info_logout");
		if( nodeInfo != null) {
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		}
	}

	private void listenerLogin() {

		SimpleLoginInfo loginInfo = InfoManager.getInfo(this);

		AccessibilityNodeInfo nodeInfoInputName = getNodeInfoById("input_username_edittext");
		if(nodeInfoInputName != null) {
			setText(nodeInfoInputName, loginInfo.getUsername());
		}

		AccessibilityNodeInfo nodeInfoInputPassword = getNodeInfoById("input_password_edittext");
		if(nodeInfoInputPassword != null) {
			setText(nodeInfoInputPassword, loginInfo.getPasswrod());
		}

		AccessibilityNodeInfo nodeInfoLogin = getNodeInfoById("login_btn");
		if(nodeInfoLogin != null) {

			if(!TextUtils.isEmpty(loginInfo.getUsername())
					&& !TextUtils.isEmpty(loginInfo.getPasswrod())) {

				nodeInfoLogin.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}

			EntryDbHelper dbHelper = new EntryDbHelper(this);
			if(!dbHelper.isExist(loginInfo)) {
				dbHelper.saveInfo(loginInfo.getUsername(), loginInfo.getPasswrod(), loginInfo.getDesc());
			}

			InfoManager.clear(this);
		}

	}

	private void setText(AccessibilityNodeInfo info, String text) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Bundle arguments = new Bundle();
			arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
			info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
		}else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			ClipData clipData = ClipData.newPlainText("data", text);
			clipboardManager.setPrimaryClip(clipData);
			info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
			info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
		}
	}

	private AccessibilityNodeInfo getNodeInfoById(String keyId) {
		return getNodeInfo(keyId, true);
	}

	private AccessibilityNodeInfo getNodeInfoByText(String keyText) {
		return getNodeInfo(keyText, false);
	}

	private AccessibilityNodeInfo getNodeInfo(String key, boolean isId) {
		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> nodeInfos =
				isId ? accessibilityNodeInfo.
				findAccessibilityNodeInfosByViewId(PACKAGE_ID + key) :
						accessibilityNodeInfo.findAccessibilityNodeInfosByText(key);
		if(nodeInfos != null) {
			for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
				return nodeInfo;
			}
		}
		return null;
	}

	private boolean isLogoutAction() {
		SimpleLoginInfo loginInfo = InfoManager.getInfo(this);
		if(TextUtils.isEmpty(loginInfo.getUsername()) || TextUtils.isEmpty(loginInfo.getPasswrod())) {
			return false;
		}else {
			return true;
		}
	}

}
