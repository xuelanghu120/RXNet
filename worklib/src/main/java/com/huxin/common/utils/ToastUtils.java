package com.huxin.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 土司工具类
 */
public class ToastUtils {
    private static Toast toast = null;

    private static Handler mHandler = null;

    public static int mDuration = Toast.LENGTH_LONG;

    public static String mMsg = null;

    private static void initToast(final Context context) {
        if (toast == null) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private static void show(final int duration, final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        mDuration = duration;
        mMsg = msg;
        mHandler.removeCallbacks(showRunnable);
        mHandler.postDelayed(showRunnable, 100);
    }

    public static Runnable showRunnable = new Runnable() {

        @Override
        public void run() {
            if (toast != null) {
                toast.setDuration(mDuration);
                toast.setText(mMsg);
                toast.show();
            }
        }
    };

    public static void showShortToast(Context context, int resID) {
        initToast(context);
        show(Toast.LENGTH_SHORT, context.getString(resID));
    }

    public static void showShortToast(Context context, String msg) {
        initToast(context);
        show(Toast.LENGTH_SHORT, msg);
    }

    public static void showLongToast(Context context, int resID) {
        initToast(context);
        show(Toast.LENGTH_LONG, context.getString(resID));
    }

    public static void showLongToast(Context context, String msg) {
        initToast(context);
        show(Toast.LENGTH_LONG, msg);
    }
}
