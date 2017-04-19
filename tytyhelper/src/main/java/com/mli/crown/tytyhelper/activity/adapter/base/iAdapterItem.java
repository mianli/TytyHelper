package com.mli.crown.tytyhelper.activity.adapter.base;

import android.view.View;

/**
 * Created by mli on 2017/4/17.
 */

public interface iAdapterItem<T> {

    View createCell(int position, View convertView);
    void updateCell(View view, int position, T data);

}
