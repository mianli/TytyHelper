package com.mli.crown.tytyhelper.activity.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mli on 2017/4/17.
 */

public interface iCell {

    View createCell(LayoutInflater inflater, ViewGroup container);
    void updateCell(int position, Object object);

}
