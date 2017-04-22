package com.mli.crown.tytyhelper.activity.adapter.base;

/**
 * Created by mli on 2017/4/17.
 */

public interface iReceiverData<DATA_TYPE> {

    void setData(int startPos, int endPos, iDataReceiver<DATA_TYPE> receiver);

}
