package com.mli.crown.tytyhelper.customview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.tools.Utils;

/**
 * Created by mli on 2017/4/22.
 */

public class SwipeRefreshListView<VIEW_TYPE extends AbsListView> extends SwipeRefreshLayout {

    private VIEW_TYPE mListView;

    public SwipeRefreshListView(Context context) {
        super(context);
    }

    public SwipeRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.theme_color);
        mListView = Utils.cast(LayoutInflater.from(context).inflate(R.layout.listview, this, false));

        addView(mListView);
    }

    public AbsListView getListView() {
        return mListView;
    }

}
