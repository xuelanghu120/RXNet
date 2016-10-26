package com.huxin.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Activity跳转工具类
 * Created by 56417 on 2016/9/13.
 */

public class ActivityUtil {
    public static void toActivity(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public static void toActivity(Context context, Class<?> cls, String key,
                                  Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, bundle);
        context.startActivity(intent);
    }

    public static void toActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
       context.startActivity(intent);
    }

    public static void toActivity(Context context, Class<?> cls, String key,
                                  Parcelable parcelable) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, parcelable);
         context.startActivity(intent);
    }

    public static void toActivity(Context context, Class<?> cls, String key,
                                  Serializable serializable) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, serializable);
        context.startActivity(intent);
    }

    public static void toActivity(Context context, Class<?> cls, String key,
                                  int value) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public static void toActivityForResult(Context context, Class<?> cls,
                                           String key, int value, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, value);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void toActivityForResult(Context context, Class<?> cls,
                                           String key, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, bundle);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void toActivityForResult(Context context, Fragment f,
                                           Class<?> cls, String key, int value, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(key, value);
        f.startActivityForResult(intent, requestCode);
    }
}
