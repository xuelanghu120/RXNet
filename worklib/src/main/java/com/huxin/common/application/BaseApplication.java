package com.huxin.common.application;

import android.app.Application;

import com.huxin.common.utils.device.DeviceInfo;

/**
 * Created by 56417 on 2016/9/27.
 */

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //全局变量
        Global.setContext(getApplicationContext());
        //设备信息
        DeviceInfo.init(Global.getContext());
    }
}
