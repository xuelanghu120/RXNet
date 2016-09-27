package com.huxin.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;


import com.huxin.common.application.Global;
import com.huxin.common.worklib.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by 56417 on 2016/9/21.
 */

public class PermissionUtils {
    /**
     * 申请单个权限
     *
     * @param context
     * @param String
     * @return
     */
    public static Observable<Boolean> reqPermissionObservable(final Context context, String String) {
        return RxPermissions.getInstance(context)
//                .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .request(String)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + Global.getContext().getPackageName()));
                            context.startActivity(intent);
                            ToastUtils.showShortToast(context, "请通过相关权限");
                        }
                    }
                });
    }

    /**
     * 获取多个权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static Observable<Boolean> reqPermissionsObservable(final Context context, String... permissions) {
        return RxPermissions.getInstance(context)
//                .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .request(permissions)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + Global.getContext().getPackageName()));
                            context.startActivity(intent);
                            ToastUtils.showShortToast(context, "请通过相关权限");
                        }
                    }
                });
    }

//    public static Observable<Boolean> reqPermissionsObservable2(final Context context, String... permissions) {
//        return Observable.just("")
//                .compose(RxPermissions.getInstance(context).ensure(permissions))
//                .doOnNext(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if(!aBoolean){
//                            ToastUtils.showShortToast(context, context.getString(R.string.please_set_the_permission));
//                        }
//                    }
//                });
//    }
}
