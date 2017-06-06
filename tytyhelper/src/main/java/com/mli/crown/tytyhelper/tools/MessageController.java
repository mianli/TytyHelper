package com.mli.crown.tytyhelper.tools;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.lang.ref.WeakReference;

/**
 * Created by crown on 2017/6/6.
 */

public class MessageController extends Handler{

    public interface iMessageHolder {
        void handleMessage(Object param);
    }

    private SparseArray<WeakReference<iMessageHolder>> mRunners = new SparseArray<>();
    private SparseArray<iMessageHolder> mTmpRunners = new SparseArray<>();
    //用于清除一定时间内有重复动作的事件
    private SparseIntArray mRunningMessageid = new SparseIntArray();

    public void registMessage(int messageId, iMessageHolder messageHolder) {
        mRunners.put(messageId, new WeakReference<>(messageHolder));
        mTmpRunners.put(messageId, messageHolder);
    }

    public void sendMessage(int messageid, Object param) {
        Message message = Message.obtain(Global.handler);
        message.what = messageid;
        message.obj = param;
        Global.handler.sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        if(mRunningMessageid.get(msg.what, -1) != -1) {
            return;
        }
        mRunningMessageid.put(msg.what, msg.what);
        WeakReference<iMessageHolder> holder = mRunners.get(msg.what);
        if(holder != null) {
            if(holder.get() != null) {
                holder.get().handleMessage(msg.obj);
            }else {
                Log.i("事件处理已经被销毁");
                mTmpRunners.remove(msg.what);
                mRunners.remove(msg.what);
            }
        }
        mRunningMessageid.delete(msg.what);
    }

    public void removeMessageById(int what) {
        removeMessages(what);
        mTmpRunners.remove(what);
        mRunners.remove(what);
    }

}
