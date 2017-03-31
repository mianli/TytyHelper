package com.mli.crown.tytyhelper.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.ApkInfoUtils;
import com.mli.crown.tytyhelper.tools.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crown on 2017/3/28.
 */
public class ApksInfoActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_apks_info);

		List<String> urlList = new ArrayList<>();
		String u1 = getIntent().getStringExtra("u1");
		String u2 = getIntent().getStringExtra("u2");
		String u3 = getIntent().getStringExtra("u3");
		String u4 = getIntent().getStringExtra("u4");
		String u5 = getIntent().getStringExtra("u5");
		String u6 = getIntent().getStringExtra("u6");
		String u7 = getIntent().getStringExtra("u7");
		urlList.add(u1);
		urlList.add(u2);
		urlList.add(u3);
		urlList.add(u4);
		urlList.add(u5);
		urlList.add(u6);
		urlList.add(u7);


		ListView listView = (ListView) findViewById(R.id.apks_info_listview);
		List<Drawable> list = new ArrayList<>();
		for (String url : urlList) {
			Drawable drawable = ApkInfoUtils.getAppIcon(this, FileUtils.getDownloadFile(url).getAbsolutePath());
			list.add(drawable);
		}

		listView.setAdapter(new MyAdapter(list));

		ImageView i1 = (ImageView) findViewById(R.id.i1);
		ImageView i2 = (ImageView) findViewById(R.id.i2);
		ImageView i3 = (ImageView) findViewById(R.id.i3);
		ImageView i4 = (ImageView) findViewById(R.id.i4);
		i1.setImageDrawable(list.get(0));
		i2.setImageDrawable(list.get(1));
		i3.setImageDrawable(list.get(2));
		i4.setImageDrawable(list.get(3));
	}

	private class MyAdapter extends BaseAdapter {

		List<Drawable> mList = new ArrayList<>();

		public MyAdapter(List<Drawable> list) {
			this.mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
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
			ViewHolder viewHolder;
			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_download, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_download_apk_icon_imgv);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imageView.setImageDrawable(mList.get(position));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
		}
	}

}
