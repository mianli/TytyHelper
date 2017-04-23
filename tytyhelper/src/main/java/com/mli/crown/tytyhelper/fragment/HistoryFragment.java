package com.mli.crown.tytyhelper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.LoginHelper;
import com.mli.crown.tytyhelper.activity.adapter.base.AbsListViewDataHelper;
import com.mli.crown.tytyhelper.activity.adapter.base.iAdapterItem;
import com.mli.crown.tytyhelper.activity.adapter.base.iDataReceiver;
import com.mli.crown.tytyhelper.activity.adapter.base.iReceiverData;
import com.mli.crown.tytyhelper.activity.adapter.cell.HistoryCell;
import com.mli.crown.tytyhelper.bean.LoginInfo;
import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;
import com.mli.crown.tytyhelper.customview.SwipeRefreshListView;
import com.mli.crown.tytyhelper.tools.EntryDbHelper;
import com.mli.crown.tytyhelper.tools.InfoManager;
import com.mli.crown.tytyhelper.tools.MyToast;
import com.mli.crown.tytyhelper.tools.Utils;

import java.util.List;

/**
 * Created by crown on 2017/3/22.
 */
public class HistoryFragment extends Fragment implements iAdapterItem<LoginInfo>, iReceiverData<LoginInfo> {

	private EntryDbHelper mDbHelper;

	private AbsListViewDataHelper<ListView, LoginInfo> mAdapterHelper;
	private HistoryCell mCell = new HistoryCell();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mDbHelper = new EntryDbHelper(getActivity());

		View view = inflater.inflate(R.layout.activity_history, container, false);
		SwipeRefreshListView swipeRefreshListView = (SwipeRefreshListView) view.findViewById(R.id.history_swiperefresh_listview);
		mAdapterHelper = new AbsListViewDataHelper<>(swipeRefreshListView, this, this, 10);
		mAdapterHelper.startLoad();
		mAdapterHelper.setOnItemClickListener(new AbsListViewDataHelper.OnItemClickListener<LoginInfo>() {
			@Override
			public void onItemClick(int position, final LoginInfo data, View view) {
				if(Utils.isAccessibilitySettingsEnable(getActivity())) {
					InfoManager.saveInfo(getActivity(), data);
					LoginHelper.getInstance(getActivity()).filterAndStartApp();
				}else {
					Utils.showOpenAccessibilitySetting(getActivity(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							InfoManager.saveInfo(getActivity(), data);
							LoginHelper.getInstance(getActivity()).filterAndStartApp();
						}
					});
				}
			}
		});

		mAdapterHelper.setOnItemLongClickLisetner(new AbsListViewDataHelper.OnItemLongClickListener<LoginInfo>() {
			@Override
			public boolean onItemLongClick(int position, LoginInfo data, View view) {
				showDeleteDialog(position, data);
				return true;
			}
		});

		return view;
	}

	public void showDeleteDialog(final int position, final LoginInfo info) {
		new AlertDialog.Builder(getActivity()).setTitle("删除该条记录?")
		.setNegativeButton("取消", null)
		.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDbHelper.delete(info.getUsername(), info.getPasswrod());
				mAdapterHelper.remove(position);
			}
		}).show();
	}

	public void clearHistory() {
		new AlertDialog.Builder(getActivity()).setTitle("是否清空记录?!!!")
				.setNegativeButton("取消", null)
				.setPositiveButton("清空", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDbHelper.clearTable();
						mAdapterHelper.clear();
					}
				}).show();
	}

	@Override
	public View createCell(int position, View convertView) {
		return mCell.createCell(getActivity(), position, convertView);
	}

	@Override
	public void updateCell(View view, int position, LoginInfo data) {
		mCell.updateCell(position, data);
	}

	@Override
	public void receiveData(int startPos, int endPos, iDataReceiver<LoginInfo> receiver) {
		receiver.receiver(mDbHelper.getList(startPos, endPos));
	}
}