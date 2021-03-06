package com.mli.crown.tytyhelper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.LoginHelper;
import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;
import com.mli.crown.tytyhelper.customview.HEditText;
import com.mli.crown.tytyhelper.tools.DataManager;
import com.mli.crown.tytyhelper.tools.InfoManager;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.Utils;

/**
 * Created by crown on 2017/3/21.
 */
public class AddUserFragment extends Fragment {

	public static final String FRAGMENT_TAG = "addUserFragment";
	private static final String FILE_NAME = "PASSWORD";
	private static final String PASSWORD = "password";

	private HEditText mUsernameEdt, mPasswordEdt;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_adduser, container, false);
		mUsernameEdt = (HEditText) view.findViewById(R.id.adduser_username_etv);
		mPasswordEdt = (HEditText) view.findViewById(R.id.adduser_password_etv);
		final EditText descEdt = (EditText) view.findViewById(R.id.adduser_desc_etv);
		Button clearBtn = (Button) view.findViewById(R.id.adduser_clear_btn);
		clearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUsernameEdt.setText("");
				mPasswordEdt.setText("");
				descEdt.setText("");
			}
		});

		final Button passwordBtn = (Button) view.findViewById(R.id.adduser_save_password_btn);
		final String password = DataManager.getValue(getActivity(), FILE_NAME, PASSWORD);
		if(password != null) {
			passwordBtn.setText("使用密码");
			passwordBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPasswordEdt.setText(password);
				}
			});
		}else {
			passwordBtn.setText("保存密码");
			passwordBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!TextUtils.isEmpty(mPasswordEdt.getText())) {
						DataManager.saveValue(getActivity(), FILE_NAME, PASSWORD, mPasswordEdt.getText().toString());
						passwordBtn.setText("使用密码");
						passwordBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final String password = DataManager.getValue(getActivity(), FILE_NAME, PASSWORD);
								mPasswordEdt.setText(password);
							}
						});
					}else {
						MyToast.shortShowCenter(getActivity(), "没有输入密码");
					}
				}
			});
		}

		Button loginBtn = (Button) view.findViewById(R.id.adduser_add_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = null;
				String password = null;
				String desc = null;
				if(TextUtils.isEmpty(mUsernameEdt.getText())) {
					mUsernameEdt.setInputFocus(true);
				}else {
					username = mUsernameEdt.getText().toString().trim();
				}
				if(TextUtils.isEmpty(mPasswordEdt.getText())) {
					if(!TextUtils.isEmpty(mUsernameEdt.getText())) {
						mPasswordEdt.setInputFocus(true);
					}
				}else {
					password = mPasswordEdt.getText().toString().trim();
				}
				if(descEdt.getText() != null) {
					desc = descEdt.getText().toString();
				}

				if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					MyToast.shortShowCenter(getActivity(), "请输入用户名和密码");
				}else {
					if(Utils.isAccessibilitySettingsEnable(getActivity())) {
						InfoManager.saveInfo(getActivity(), new SimpleLoginInfo(username, password, desc));
						LoginHelper.getInstance(getActivity()).filterAndStartApp();
					}else {
						final String finalUsername = username;
						final String finalPassword = password;
						final String finalDesc = desc;
						Utils.showOpenAccessibilitySetting(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								InfoManager.saveInfo(getActivity(), new SimpleLoginInfo(finalUsername, finalPassword, finalDesc));
								LoginHelper.getInstance(getActivity()).filterAndStartApp();
							}
						});
					}
				}
			}
		});
		return view;
	}

}
