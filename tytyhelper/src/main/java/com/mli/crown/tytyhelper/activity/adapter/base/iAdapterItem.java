package com.mli.crown.tytyhelper.activity.adapter.base;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mli on 2017/4/17.
 */

public interface iAdapterItem<T> {

    View createCell(int position, ViewGroup convertView);
    void updateCell(View view, int position, T data);

}
