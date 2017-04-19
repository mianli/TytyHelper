package com.mli.crown.tytyhelper.activity.adapter.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by mli on 2017/4/17.
 */

public class MyAdapter<DATA_TYPE> extends BaseAdapter{

    private List<DATA_TYPE> mSource;
    private iAdapterItem<DATA_TYPE> mAdapter;

    public MyAdapter(List<DATA_TYPE> source, iAdapterItem<DATA_TYPE> adapter) {
        this.mSource = source;
        this.mAdapter = adapter;
    }

    @Override
    public int getCount() {
        return mSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mAdapter.createCell(position, convertView);
        mAdapter.updateCell(convertView, position, mSource.get(position));
        return convertView;
    }
}
