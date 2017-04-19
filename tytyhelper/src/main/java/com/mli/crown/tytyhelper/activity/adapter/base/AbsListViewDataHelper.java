package com.mli.crown.tytyhelper.activity.adapter.base;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mli on 2017/4/17.
 */

public class AbsListViewDataHelper<VIEW_TYPE extends AbsListView, DATA_TYPE> {

    private VIEW_TYPE mAbsListView;
    private List<DATA_TYPE> mDataSource = new ArrayList<>();
    private iReceiverData<DATA_TYPE> mSouce;
    private iAdapterItem<DATA_TYPE> mAdapterItem;
    private MyAdapter<DATA_TYPE> mMyAdapter;

    public AbsListViewDataHelper(VIEW_TYPE AbsListView, iReceiverData<DATA_TYPE> source, final iAdapterItem<DATA_TYPE> adapter) {
        this.mAbsListView = AbsListView;
        this.mSouce = source;
        this.mAdapterItem = adapter;
    }

    public void load() {
        mSouce.setData(new iDataReceiver<DATA_TYPE>() {
            @Override
            public void receiver(List<DATA_TYPE> list) {

                mDataSource.addAll(list);
                if(mMyAdapter == null) {
                    mAbsListView.setAdapter(mMyAdapter = new MyAdapter<>(mDataSource, mAdapterItem));
                }else {
                    mMyAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    public void setOnItemClickListener(final OnItemClickListener<DATA_TYPE> onItemClickListener) {
        mAbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, mDataSource.get(position), view);
                }
            }
        });
    }

    public void setOnItemLongClickLisetner(final OnItemLongClickListener<DATA_TYPE> onItemLongClickLisetner) {
        mAbsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(onItemLongClickLisetner != null) {
                    return onItemLongClickLisetner.onItemLongClick(position, mDataSource.get(position), view);
                }
                return false;
            }
        });
    }

    public DATA_TYPE get(int index) {
        return mDataSource.get(index);
    }

    public void remove(int index) {
        mDataSource.remove(index);
        mMyAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mDataSource.clear();
        mMyAdapter.notifyDataSetChanged();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data, View view);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(int position, T data, View view);
    }

}
