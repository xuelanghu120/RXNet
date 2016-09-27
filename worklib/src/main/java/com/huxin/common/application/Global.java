package com.huxin.common.application;

import android.content.Context;


/**
 * 全局
 * Created by 56417 on 2016/9/14.
 */

public class Global {

    //判断是否在前台
    private static boolean isPageFont;
    //全局上下文
    private static Context mContext;

    /***
     * 获取全局变量
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        Global.mContext = mContext;
    }


    public static boolean isPageFont() {
        return isPageFont;
    }

    public static void setIsPageFont(boolean isPageFont) {
        Global.isPageFont = isPageFont;
    }
}
