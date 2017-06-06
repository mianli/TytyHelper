package com.mli.crown.tytyhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.fragment.AddUserFragment;
import com.mli.crown.tytyhelper.fragment.DownloadFragment;
import com.mli.crown.tytyhelper.fragment.FragmentHelper;
import com.mli.crown.tytyhelper.fragment.HistoryFragment;
import com.mli.crown.tytyhelper.fragment.WebFragment;
import com.mli.crown.tytyhelper.tools.Log;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.Utils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

	private DrawerLayout mDrawlayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mDrawerContainer;
	private Toolbar toolbar;

	private View mOpenDrawerView;

	private HistoryFragment mHistoryFragment;
	private AddUserFragment mAdUserFragment;
	private WebFragment mWebFragment;
	private DownloadFragment mDownloadFragment;

	private Handler mHandler;
	private boolean mCanClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHandler = new Handler();

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

		initFragmentHelper(R.id.main_content_frame);
		if(savedInstanceState == null) {
			mHistoryFragment = (HistoryFragment) showFragment(mHistoryFragment, HistoryFragment.class);
		}else {
			mHistoryFragment = restoreFragment(HistoryFragment.class.getSimpleName());
			mAdUserFragment = restoreFragment(AddUserFragment.class.getSimpleName());
			mWebFragment = restoreFragment(WebFragment.class.getSimpleName());
			mDownloadFragment = restoreFragment(DownloadFragment.class.getSimpleName());
			showFragment(getCurrentShowingFragment(savedInstanceState), getCurrentShowingTag(savedInstanceState));
		}

	}

	public void showDownload(View view) {
		mDrawlayout.closeDrawer(mDrawerContainer);
		mDownloadFragment = (DownloadFragment) showFragment(mDownloadFragment, DownloadFragment.class);
	}

	public void showAddUser(View view) {
		mDrawlayout.closeDrawer(mDrawerContainer);
		mAdUserFragment = (AddUserFragment) showFragment(mAdUserFragment, AddUserFragment.class);
	}

	public void showHistory(View view) {
		mDrawlayout.closeDrawer(mDrawerContainer);
		mHistoryFragment = (HistoryFragment) showFragment(mHistoryFragment, HistoryFragment.class);
	}

	public void showWebpage(View view) {
		mDrawlayout.closeDrawer(mDrawerContainer);
		mWebFragment = (WebFragment) showFragment(mWebFragment, WebFragment.class);
	}

	public void checkAccessibilityEnable(View view) {
		if(!Utils.isAccessibilitySettingsEnable(this)) {
			MyToast.longShowCenter(this, "请打开辅助功能");
			Utils.openAccessibilitySetting(this);
		}else {
			MyToast.shortShow(this, "辅助功能已打开");
		}
	}

	@Override
	public void onBackPressed() {
		if(!mCanClose) {
			mCanClose = true;
			mHandler.postDelayed(mRunnable, 2000);
			MyToast.shortShow(this, "再次点击退出");
		}else {
			mHandler.removeCallbacks(mRunnable);
			super.onBackPressed();
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mCanClose = false;
		}
	};

	@Override
	protected void onDestroy() {
		mDrawlayout.removeDrawerListener(mDrawerToggle);
		super.onDestroy();
	}

	public static class BroardCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

			}
		}
	}

}
