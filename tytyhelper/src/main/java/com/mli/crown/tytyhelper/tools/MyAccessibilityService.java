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
	private boolean mLoginStyle;

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
				listenerLogin();
			}else if(className.equals(Config.MAIN_ACTIVITY)) {
					listenerMainWillLogout();//有用户登录信息，表明要登出
			}else if(className.equals(Config.USER_PROFILE_ACTIVITY)) {
				listenerStuLogout();
			}else if(className.equals(Config.TEACHER_PROFILE_ACTIVITY)) {
				listenerTeacherLogout();
			}else if(className.equals(Config.DIALOG)) {
				listenerLogout();//有用户登录信息，表明要登出
			}else if(className.equals(Config.SPLASH_ACTIVITY)) {
			}else if(className.contains("lingshi") && className.contains("activity")){
				backToMainWillLogint();
			}
		}
	}

	@Override
	public void onInterrupt() {

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
		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> headViews = accessibilityNodeInfo.
			findAccessibilityNodeInfosByViewId(PACKAGE_ID + "mian_user_head");
		if(headViews != null) {
			for (AccessibilityNodeInfo info : headViews) {
				info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		}
	}

	private void listenerLogout() {
		if(!isLogoutAction()) {
			return;
		}
		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> logoutViews = accessibilityNodeInfo.
			findAccessibilityNodeInfosByText("是否退出当前用户？");
		if(logoutViews != null) {
			List<AccessibilityNodeInfo> logoutYesBtns = accessibilityNodeInfo.
				findAccessibilityNodeInfosByViewId(PACKAGE_ID + "dialog_btn2");
			if(logoutYesBtns != null) {
				for (AccessibilityNodeInfo info : logoutYesBtns) {
					info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				}
			}
		}
	}

	private void listenerStuLogout() {
		if(!isLogoutAction()) {
			return;
		}
		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> headViews = accessibilityNodeInfo.
			findAccessibilityNodeInfosByViewId(PACKAGE_ID + "stu_profile_logout_btn");
		if(headViews != null) {
			for (AccessibilityNodeInfo info : headViews) {
				info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		}
	}

	private void listenerTeacherLogout() {
		if(!isLogoutAction()) {
			return;
		}
		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> headViews = accessibilityNodeInfo.
			findAccessibilityNodeInfosByViewId(PACKAGE_ID + "user_info_logout");
		if(headViews != null) {
			for (AccessibilityNodeInfo info : headViews) {
				info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		}
	}

	private void listenerLogin() {

		SimpleLoginInfo loginInfo = InfoManager.getInfo(this);

		AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
		List<AccessibilityNodeInfo> editTextList0 = accessibilityNodeInfo.
			findAccessibilityNodeInfosByViewId(PACKAGE_ID + "input_username_edittext");
		if(editTextList0 != null) {

			for (AccessibilityNodeInfo info : editTextList0) {
				setText(info, loginInfo.getUsername());

				break;
			}

			List<AccessibilityNodeInfo> editTextList1 = accessibilityNodeInfo.
				findAccessibilityNodeInfosByViewId(PACKAGE_ID + "input_password_edittext");
			if(editTextList1 != null) {
				for (final AccessibilityNodeInfo info : editTextList1) {
					setText(info, loginInfo.getPasswrod());
					break;
				}
			}

			List<AccessibilityNodeInfo> btnList = accessibilityNodeInfo.
				findAccessibilityNodeInfosByViewId(PACKAGE_ID + "login_btn");
			if(btnList != null) {
				for (AccessibilityNodeInfo info : btnList) {
					if(!TextUtils.isEmpty(loginInfo.getUsername())
						&& !TextUtils.isEmpty(loginInfo.getPasswrod())) {
						info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}

					EntryDbHelper dbHelper = new EntryDbHelper(this);
					dbHelper.saveInfo(loginInfo.getUsername(), loginInfo.getPasswrod(), loginInfo.getDesc());

					InfoManager.clear(this);
					break;
				}
			}
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

	private boolean isLogoutAction() {
		SimpleLoginInfo loginInfo = InfoManager.getInfo(this);
		if(TextUtils.isEmpty(loginInfo.getUsername()) || TextUtils.isEmpty(loginInfo.getPasswrod())) {
			return false;
		}else {
			return true;
		}
	}

}
