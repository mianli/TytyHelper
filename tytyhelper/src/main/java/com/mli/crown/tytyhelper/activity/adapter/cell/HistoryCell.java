package com.mli.crown.tytyhelper.activity.adapter.cell;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
import com.mli.crown.tytyhelper.activity.adapter.base.ViewHolder;
import com.mli.crown.tytyhelper.activity.adapter.base.iCell;
import com.mli.crown.tytyhelper.bean.LoginInfo;

/**
 * Created by mli on 2017/4/19.
 */

public class HistoryCell implements iCell {

    private TextView nameView;
    private TextView descView;
    public Button mActionView;

    public static View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.item_history, container, false);
        HistoryCell cell = new HistoryCell();
        cell.nameView = ViewHolder.get(view, R.id.item_history_user_name);
        cell.descView = ViewHolder.get(view, R.id.item_history_user_desc);
        cell.mActionView = ViewHolder.get(view, R.id.item_history_user_login);
        view.setTag(cell);
        return view;
    }

    @Override
    public View createCell(LayoutInflater inflater, ViewGroup container) {
        return createView(inflater, container);
    }

    @Override
    public void updateCell(int position, Object object) {
        if(object instanceof LoginInfo) {
            LoginInfo info = (LoginInfo) object;
            if(TextUtils.isEmpty(info.getDesc())) {
                nameView.setText("用户名：" + info.getUsername());
                descView.setText("密码：" + info.getPasswrod());
            }else {
                nameView.setText("用户名：" + info.getUsername() +
                        "\t\t密码：" + info.getPasswrod());
                descView.setText("描述：" + info.getDesc());
            }
        }
    }
}
