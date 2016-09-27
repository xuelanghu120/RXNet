package com.huxin.common.application;

import android.app.Application;

/**
 * Created by 56417 on 2016/9/27.
 */

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Global.setContext(getApplicationContext());
    }
}
