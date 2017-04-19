package com.mli.crown.tytyhelper.activity.adapter.base;

import java.util.List;

/**
 * Created by mli on 2017/4/17.
 */

public interface iDataReceiver<DATA_TYPE> {

    void receiver(List<DATA_TYPE> list);

}
