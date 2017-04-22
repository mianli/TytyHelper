package com.mli.crown.tytyhelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;
import com.mli.crown.tytyhelper.tools.Config;
import com.mli.crown.tytyhelper.tools.InfoManager;

/**
 * Created by crown on 2017/3/21.
 */
public class AddUserFragment extends Fragment {

	private EditText mUsernameEdt, mPasswordEdt;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_adduser, container, false);
		mUsernameEdt = (EditText) view.findViewById(R.id.adduser_username_etv);
		mPasswordEdt = (EditText) view.findViewById(R.id.adduser_password_etv);

		final EditText descEdt = (EditText) view.findViewById(R.id.adduser_desc_etv);
		Button loginBtn = (Button) view.findViewById(R.id.adduser_add_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = null;
				String password = null;
				String desc = null;
				if(mUsernameEdt.getText() != null) {
					username = mUsernameEdt.getText().toString().trim();
				}
				if(mPasswordEdt.getText() != null) {
					password = mPasswordEdt.getText().toString().trim();
				}
				if(descEdt.getText() != null) {
					desc = descEdt.getText().toString();
				}else {
					desc = "";
				}

				InfoManager.saveInfo(getActivity(), new SimpleLoginInfo(username, password, desc));
				LoginHelper.getInstance(getActivity()).filterAndStartApp();
			}
		});
		return view;
	}

}
