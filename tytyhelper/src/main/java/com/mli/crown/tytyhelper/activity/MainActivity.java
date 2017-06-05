package com.mli.crown.tytyhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

	private static final String CURRENT_SHOWING_TAG = "currentShowingTag";
	private DrawerLayout mDrawlayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mDrawerContainer;
	private Toolbar toolbar;

	private View mOpenDrawerView;

	private HistoryFragment mHistoryFragment;
	private AddUserFragment mAdUserFragment;
	private WebFragment mWebFragment;
	private DownloadFragment mDownloadFragment;

	private FragmentHelper mFragmentHelper;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

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
//			if(mHistoryFragment == null) {
//				showFragment(mHistoryFragment = new HistoryFragment());
//			}
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

		if(savedInstanceState == null) {
			initFragments();
		}else {
			mFragmentHelper = new FragmentHelper(R.id.main_content_frame);
			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			if(fragments != null) {
				for (Fragment fragment : fragments) {
					if(fragment instanceof HistoryFragment) {
						mHistoryFragment = (HistoryFragment) fragment;
					}else if(fragment instanceof AddUserFragment){
						mAdUserFragment = (AddUserFragment) fragment;
					}else if(fragment instanceof WebFragment) {
						mWebFragment = (WebFragment) fragment;
					}else if(fragment instanceof DownloadFragment) {
						mDownloadFragment = (DownloadFragment) fragment;
					}
				}
			}
			mFragmentHelper.putFragment(mHistoryFragment, HistoryFragment.class);
			mFragmentHelper.putFragment(mAdUserFragment, AddUserFragment.class);
			mFragmentHelper.putFragment(mWebFragment, WebFragment.class);
			mFragmentHelper.putFragment(mDownloadFragment, DownloadFragment.class);
			String currentShowingTag = savedInstanceState.getString(CURRENT_SHOWING_TAG);
			if(currentShowingTag != null) {
				showFragment(mFragmentHelper.getFragmentByTag(currentShowingTag),
						currentShowingTag);
			}else {
				showFragment(mHistoryFragment, mHistoryFragment.getClass().getSimpleName());
			}
		}

	}

	private void initFragments() {
		mFragmentHelper = new FragmentHelper(R.id.main_content_frame);
		mHistoryFragment = new HistoryFragment();
		mAdUserFragment = new AddUserFragment();
		mWebFragment = new WebFragment();
		mDownloadFragment = new DownloadFragment();

		showFragment(mHistoryFragment, HistoryFragment.class.getSimpleName());
	}

	public void showDownload(View view) {
		showFragment(mDownloadFragment, DownloadFragment.class);
	}

	public void showAddUser(View view) {
		showFragment(mAdUserFragment, AddUserFragment.class);
	}

	public void showHistory(View view) {
		showFragment(mHistoryFragment, HistoryFragment.class);
	}

	public void showWebpage(View view) {
		showFragment(mWebFragment, WebFragment.class);
	}

	public void showFragment(Fragment fragment, Class cls) {
		if(fragment == null) {
			try {
				fragment = (Fragment) cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(fragment != null) {
			showFragment(fragment, cls.getSimpleName());
			mDrawlayout.closeDrawer(mDrawerContainer);
		}
	}

	public void checkAccessibilityEnable(View view) {
		if(!Utils.isAccessibilitySettingsEnable(this)) {
			MyToast.longShowCenter(this, "请打开辅助功能");
			Utils.openAccessibilitySetting(this);
		}else {
			MyToast.shortShow(this, "辅助功能已打开");
		}
	}

	public void showFragment(Fragment fragment, @NonNull String tag) {
		mFragmentHelper.showFragment(getSupportFragmentManager().beginTransaction(), fragment, tag);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(CURRENT_SHOWING_TAG, mFragmentHelper.getShowingTag());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		mDrawlayout.removeDrawerListener(mDrawerToggle);
		super.onDestroy();
	}

	@SuppressWarnings("unchecked")
	private <VIEW_TYPE> VIEW_TYPE findView(int id) {
		return (VIEW_TYPE) findViewById(id);
	}


	public static class BroardCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

			}
		}
	}

}
