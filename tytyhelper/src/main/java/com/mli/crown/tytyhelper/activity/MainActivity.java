package com.mli.crown.tytyhelper.activity;

import android.graphics.Color;
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

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawlayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mMenuView;
	private Toolbar toolbar;

	private HistoryFragment mHistoryFragment;
	private AddUserFragment mAdUserFragment;
	private DownloadFragment mDownloadFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.main_toolbar);
		toolbar.setTitle(getResources().getString(R.string.app_name));//设置Toolbar标题
		toolbar.setTitleTextColor(Color.parseColor("#000000")); //设置标题颜色
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
			getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mDrawlayout = (DrawerLayout) findViewById(R.id.main_drawlayout);
		mDrawlayout.setDrawerShadow(R.drawable.notication, GravityCompat.START);
		mMenuView = findViewById(R.id.main_menu_view);
		if(savedInstanceState == null) {
			if(mHistoryFragment == null) {
				showFragment(mHistoryFragment = new HistoryFragment());
			}
		}

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawlayout, toolbar, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if(mHistoryFragment != null && mHistoryFragment.isVisible()) {
					toolbar.inflateMenu(R.menu.menu_main);
				}else if(toolbar.getMenu() != null) {
					toolbar.getMenu().clear();
				}
			}
		};
		mDrawerToggle.syncState();
		mDrawlayout.setDrawerListener(mDrawerToggle);
	}

	public void showDownload(View view) {
		if(mDownloadFragment == null) {
			mDownloadFragment = new DownloadFragment();
		}
		showFragment(mDownloadFragment);
		mDrawlayout.closeDrawer(mMenuView);
	}

	public void showAddUser(View view) {
		if(mAdUserFragment == null) {
			mAdUserFragment = new AddUserFragment();
		}
		showFragment(mAdUserFragment);
		mDrawlayout.closeDrawer(mMenuView);
	}

	public void showHistory(View view) {
		if(mHistoryFragment == null) {
			mHistoryFragment = new HistoryFragment();
		}
		showFragment(mHistoryFragment);
		mDrawlayout.closeDrawer(mMenuView);
	}

	public void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_content_frame, fragment).commit();
	}

}
