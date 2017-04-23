package com.mli.crown.tytyhelper.activity.adapter.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.mli.crown.tytyhelper.customview.SwipeRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mli on 2017/4/17.
 */

public class AbsListViewDataHelper<VIEW_TYPE extends AbsListView, DATA_TYPE> {

    public static final int NO_PAGING = -1;//无分页

    private SwipeRefreshListView mSwipeRefreshListView;
    private VIEW_TYPE mAbsListView;
    private List<DATA_TYPE> mDataSource = new ArrayList<>();
    private iReceiverData<DATA_TYPE> mSouce;
    private iAdapterItem<DATA_TYPE> mAdapterItem;
    private MyAdapter<DATA_TYPE> mMyAdapter;
    private int mCountPerPage = 20;

    private boolean mIsBlock;
    private boolean isLastPage;

    @SuppressWarnings("unchecked")
    public AbsListViewDataHelper(SwipeRefreshListView swipeRefreshListView, iReceiverData<DATA_TYPE> source, final iAdapterItem<DATA_TYPE> adapter, final int mCountPerPage) {
        this.mSwipeRefreshListView = swipeRefreshListView;
        this.mAbsListView = (VIEW_TYPE) swipeRefreshListView.getListView();
        this.mCountPerPage = mCountPerPage;
        this.mSouce = source;
        this.mAdapterItem = adapter;

        mSwipeRefreshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoad();
            }
        });

        mAbsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(visibleItemCount == 0) {
                    //如果没有
                    return;
                }

                if (firstVisibleItem == 0 && mAbsListView.getChildAt(0).getBottom() <= 0) {
                    View firstVisibleItemView = mAbsListView.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
//                        startLoad();
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {

                    if(mCountPerPage < 0) {
                        //没有分页功能
                        return;
                    }

                    View lastVisibleItemView = mAbsListView.getChildAt(mAbsListView.getChildCount() - 1);

                    if (lastVisibleItemView != null &&
                            (lastVisibleItemView.getBottom() <= mAbsListView.getHeight())) {
                        load(mDataSource.size(), mDataSource.size() + mCountPerPage);
                    }
                }
            }
        });
    }

    public void startLoad() {
        if(mIsBlock) {
            return;
        }

        mSouce.receiveData(0, mCountPerPage - 1, new iDataReceiver<DATA_TYPE>() {
            @Override
            public void receiver(List<DATA_TYPE> list) {
                isLastPage = false;


                mDataSource.clear();
                mDataSource.addAll(list);
                if(mMyAdapter == null) {
                    mAbsListView.setAdapter(mMyAdapter = new MyAdapter<>(mDataSource, mAdapterItem));
                }else {
                    mMyAdapter.notifyDataSetChanged();
                }

                mSwipeRefreshListView.setRefreshing(false);
            }
        });
    }

    public void load(int startPos, int endPos) {
        if(mIsBlock || isLastPage) {
            return;
        }

        mIsBlock = true;
        mSouce.receiveData(startPos, endPos, new iDataReceiver<DATA_TYPE>() {
            @Override
            public void receiver(List<DATA_TYPE> list) {

                if(list == null || list.size() == 0) {
                    isLastPage = true;
                }else {
                    mDataSource.addAll(list);
                    if(mMyAdapter == null) {
                        mAbsListView.setAdapter(mMyAdapter = new MyAdapter<>(mDataSource, mAdapterItem));
                    }else {
                        mMyAdapter.notifyDataSetChanged();
                    }
                }
                mSwipeRefreshListView.setRefreshing(false);

                mIsBlock = false;
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
