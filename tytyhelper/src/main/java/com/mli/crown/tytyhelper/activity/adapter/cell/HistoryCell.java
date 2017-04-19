package com.mli.crown.tytyhelper.activity.adapter.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
    @Override
    public View createCell(Context context, int position, View container) {
        if(container == null) {
            container = LayoutInflater.from(context).inflate(R.layout.item_history, null);
        }
        nameView = ViewHolder.get(container, R.id.item_history_user_name);
        descView = ViewHolder.get(container, R.id.item_history_user_desc);
        return container;
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
