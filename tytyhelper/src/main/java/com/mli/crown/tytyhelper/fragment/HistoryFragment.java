package com.mli.crown.tytyhelper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.LoginHelper;
import com.mli.crown.tytyhelper.activity.adapter.base.AbsListViewDataHelper;
import com.mli.crown.tytyhelper.activity.adapter.base.iAdapterItem;
import com.mli.crown.tytyhelper.activity.adapter.base.iDataReceiver;
import com.mli.crown.tytyhelper.activity.adapter.base.iReceiverData;
import com.mli.crown.tytyhelper.activity.adapter.cell.HistoryCell;
import com.mli.crown.tytyhelper.bean.LoginInfo;
import com.mli.crown.tytyhelper.customview.SwipeRefreshListView;
import com.mli.crown.tytyhelper.tools.EntryDbHelper;
import com.mli.crown.tytyhelper.tools.Global;
import com.mli.crown.tytyhelper.tools.InfoManager;
import com.mli.crown.tytyhelper.tools.MessageController;
import com.mli.crown.tytyhelper.tools.MessageId;
import com.mli.crown.tytyhelper.tools.Utils;

/**
 * Created by crown on 2017/3/22.
 */
public class HistoryFragment extends Fragment implements iAdapterItem<LoginInfo>, iReceiverData<LoginInfo> {

	private EntryDbHelper mDbHelper;

	private AbsListViewDataHelper<ListView, LoginInfo> mAdapterHelper;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mDbHelper = new EntryDbHelper(getActivity());

		View view = inflater.inflate(R.layout.activity_history, container, false);
		SwipeRefreshListView swipeRefreshListView = (SwipeRefreshListView) view.findViewById(R.id.history_swiperefresh_listview);
		mAdapterHelper = new AbsListViewDataHelper<>(swipeRefreshListView, this, this, 6);
		mAdapterHelper.startLoad();

		mAdapterHelper.setOnItemLongClickLisetner(new AbsListViewDataHelper.OnItemLongClickListener<LoginInfo>() {
			@Override
			public boolean onItemLongClick(int position, LoginInfo data, View view) {
				showDeleteDialog(position, data);
				return true;
			}
		});

		Global.handler.registMessage(MessageId.kUpdateHistorylist, new MessageController.iMessageHolder() {
			@Override
			public void handleMessage(Object param) {
				mAdapterHelper.currentPageAutoload();
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
	public View createCell(int position, ViewGroup convertView) {
		return HistoryCell.createView(LayoutInflater.from(getActivity()), convertView);
	}

	@Override
	public void updateCell(View view, int position, final LoginInfo data) {
		if(view.getTag() instanceof HistoryCell) {
			HistoryCell cell = (HistoryCell) view.getTag();
			cell.updateCell(position, data);
			cell.mActionView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
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
		}
	}

	@Override
	public void receiveData(int startPos, int endPos, iDataReceiver<LoginInfo> receiver) {
		receiver.receiver(mDbHelper.getList(startPos, endPos));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Global.handler.removeMessageById(MessageId.kUpdateHistorylist);
	}
}
