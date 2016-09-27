package com.huxin.common.http.callback;


import com.huxin.common.http.responser.AbstractResponser;

/**
 * 请求的回调
 * @param <T>
 */
public interface NetworkCallback<T extends AbstractResponser> {

    public void onSucessed(T rsp);

    public void onFailed(String code, String msg);

}
