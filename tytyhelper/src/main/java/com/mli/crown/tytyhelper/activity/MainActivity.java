package com.mli.crown.tytyhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.Utils;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawlayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mDrawerContainer;
	private Toolbar toolbar;

	private View mOpenDrawerView;

	private HistoryFragment mHistoryFragment;
	private AddUserFragment mAdUserFragment;
	private DownloadFragment mDownloadFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.main_toolbar);
		toolbar.setTitle(getResources().getString(R.string.app_name));
		toolbar.setTitleTextColor(getResources().getColor(R.color.white));
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch(item.getItemId()){
					case R.id.clear_login_history:
						if(mHistoryFragment != null) {
							mHistoryFragment.clearHistory();
						}
						break;
					default:
						break;
				}
				return false;
			}
		});
		if(getSupportActionBar() != null) {
			setSupportActionBar(toolbar);
//			getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		toolbar.inflateMenu(R.menu.menu_main);

		mOpenDrawerView = findView(R.id.main_show_drawer);
		mOpenDrawerView.setVisibility(View.GONE);
		mOpenDrawerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawlayout.openDrawer(mDrawerContainer);
			}
		});

		mDrawlayout = (DrawerLayout) findViewById(R.id.main_drawlayout);
//		mDrawlayout.setDrawerShadow(R.drawable.notication, GravityCompat.START);
		mDrawerContainer = findViewById(R.id.main_drawer_container);
		if(savedInstanceState == null) {
			if(mHistoryFragment == null) {
				showFragment(mHistoryFragment = new HistoryFragment());
			}
		}

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawlayout, toolbar, 0, 0) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				mOpenDrawerView.setEnabled(false);
			}
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

				mOpenDrawerView.setEnabled(true);

				if(mHistoryFragment != null && mHistoryFragment.isVisible()) {
					toolbar.getMenu().clear();
						toolbar.inflateMenu(R.menu.menu_main);
				}else if(toolbar.getMenu() != null) {
					toolbar.getMenu().clear();
				}
			}
		};
		mDrawerToggle.syncState();
		mDrawlayout.addDrawerListener(mDrawerToggle);

	}

	public void showDownload(View view) {
		if(mDownloadFragment == null) {
			mDownloadFragment = new DownloadFragment();
		}
		showFragment(mDownloadFragment);
		mDrawlayout.closeDrawer(mDrawerContainer);
	}

	public void showAddUser(View view) {
		if(mAdUserFragment == null) {
			mAdUserFragment = new AddUserFragment();
		}
		showFragment(mAdUserFragment);
		mDrawlayout.closeDrawer(mDrawerContainer);
	}

	public void showHistory(View view) {
		if(mHistoryFragment == null) {
			mHistoryFragment = new HistoryFragment();
		}
		showFragment(mHistoryFragment);
		mDrawlayout.closeDrawer(mDrawerContainer);
	}

	public void checkAccessibilityEnable(View view) {
		if(!Utils.isAccessibilitySettingsEnable(this)) {
			MyToast.longShow(this, "请打开辅助功能");
			Utils.openAccessibilitySetting(this);
		}
	}

	public void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_content_frame, fragment).commit();
	}

	public static class BroardCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

			}
		}
	}

	@Override
	protected void onDestroy() {
		mDrawlayout.removeDrawerListener(mDrawerToggle);
		super.onDestroy();
	}

	private <VIEW_TYPE> VIEW_TYPE findView(int id) {
		return (VIEW_TYPE) findViewById(id);
	}

}
