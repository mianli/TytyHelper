package com.mli.crown.tytyhelper.activity.adapter.base;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by mli on 2017/4/17.
 */

public class ViewHolder {

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewholder = (SparseArray<View>) view.getTag();
        if(viewholder == null) {
            viewholder = new SparseArray<>();
            view.setTag(viewholder);
        }
        View childView = viewholder.get(id);
        if(childView == null) {
            childView = view.findViewById(id);
            viewholder.put(id, childView);
        }
        return (T) childView;
    }
}
