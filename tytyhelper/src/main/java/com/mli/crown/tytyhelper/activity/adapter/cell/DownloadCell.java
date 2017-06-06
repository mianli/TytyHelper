package com.mli.crown.tytyhelper.activity.adapter.cell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.adapter.base.ViewHolder;
import com.mli.crown.tytyhelper.activity.adapter.base.iCell;
import com.mli.crown.tytyhelper.bean.LoginInfo;
import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;
import com.mli.crown.tytyhelper.tools.Log;

/**
 * Created by mli on 2017/4/17.
 */

public class DownloadCell implements iCell {

    private TextView nameView;
    private TextView descView;

    @Override
    public View createCell(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.item_history, container, false);
        DownloadCell cell = new DownloadCell();
        cell.nameView = ViewHolder.get(container, R.id.item_history_user_name);
        cell.descView = ViewHolder.get(container, R.id.item_history_user_desc);
        view.setTag(cell);
        return container;
    }

    @Override
    public void updateCell(int position, Object object) {
        if(object instanceof SimpleLoginInfo) {
            SimpleLoginInfo info = (SimpleLoginInfo) object;
            nameView.setText(info.getUsername());
            descView.setText(info.getDesc());
        }
    }
}
