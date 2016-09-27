package com.huxin.common.utils.contats;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public interface OnPrivateInfoListener<E> {
    void onReceiveSuccess(List<E> result);

//    void onReceiveError(String msg);
}
