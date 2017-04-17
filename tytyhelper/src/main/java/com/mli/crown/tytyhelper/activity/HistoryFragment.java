package com.mli.crown.tytyhelper.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.mli.crown.tytyhelper.bean.LoginInfo;
import com.mli.crown.tytyhelper.tools.EntryDbHelper;

import java.util.List;

/**
 * Created by crown on 2017/3/22.
 */
public class HistoryFragment extends Fragment {

	private List<LoginInfo> mList;
	private ListView mListview;
	private HistoryAdapter mAdapter;
	private EntryDbHelper mDbHelper;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_history, container, false);

		mDbHelper = new EntryDbHelper(getActivity());
		mList = mDbHelper.getList();

		mListview = (ListView) view.findViewById(R.id.history_listview);
		mListview.setAdapter(mAdapter = new HistoryAdapter());
		mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mList.get(position);
				LoginHelper.getInstance(getActivity()).filterAndStartApp();
			}
		});
		mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				showDeleteDialog(position);
				return true;
			}
		});

		return view;
	}

	public void showDeleteDialog(final int position) {
		new AlertDialog.Builder(getActivity()).setTitle("删除该条记录?")
		.setNegativeButton("取消", null)
		.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDbHelper.delete(mList.get(position).getUsername(), mList.get(position).getPasswrod());
				mList.remove(position);
				mAdapter.notifyDataSetChanged();
			}
		}).show();
	}

	public void clearHistory() {
		mDbHelper.clearTable();
		mList.clear();
		mAdapter.notifyDataSetChanged();
	}

	private class HistoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList != null ? mList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_history, parent, false);
				viewHolder.nameView = (TextView) convertView.findViewById(R.id.item_history_user_name);
				viewHolder.descView = (TextView) convertView.findViewById(R.id.item_history_user_desc);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			LoginInfo info = mList.get(position);
			if(TextUtils.isEmpty(info.getDesc())) {
				viewHolder.nameView.setText("用户名：" + mList.get(position).getUsername());
				viewHolder.descView.setText("密码：" + mList.get(position).getPasswrod());
			}else {
				viewHolder.nameView.setText("用户名：" + mList.get(position).getUsername() +
					"\t\t密码：" + mList.get(position).getPasswrod());
				viewHolder.descView.setText("描述：" + mList.get(position).getDesc());
			}
			return convertView;
		}

		class ViewHolder {
			TextView nameView;
			TextView descView;
		}
	}
}
