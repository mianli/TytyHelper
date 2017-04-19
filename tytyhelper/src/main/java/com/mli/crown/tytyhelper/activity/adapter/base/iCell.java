package com.mli.crown.tytyhelper.activity.adapter.base;

import android.content.Context;
import android.view.View;

/**
 * Created by mli on 2017/4/17.
 */

public interface iCell {

    View createCell(Context context, int position, View container);
    void updateCell(int position, Object object);

}
