package com.mli.crown.tytyhelper.activity.adapter.base;

import android.widget.AbsListView;

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

}
