package com.huxin.common.utils.contats;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.huxin.common.application.Global;


/**
 * Created by changfeng on 2016/9/19.
 */
public class PrivateInfoUtil {
    public final static int REQUEST_PERMISSION_CODE = 0;
    public static void getAllSms(final Activity context, final OnPrivateInfoListener<SmsEntity> listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(checkPermission(Manifest.permission.READ_SMS)){
                    listener.onReceiveSuccess(PhoneUtil.getAllSMS(Global.getContext()));
                }else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_SMS)) {

                        Log.i("===", "用户拒绝过一次权限，解释给用户为什么需要该权限");
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION_CODE);
                    }else{
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION_CODE);
                        Log.i("===", "用户选择不再弹窗或者第一次申请权限");
                    }

                }
            }
        }).start();

    }
    public static void getAllCallRecords(final Activity context, final OnPrivateInfoListener<CallRecordEntity> listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(checkPermission(Manifest.permission.READ_CALL_LOG)){
                    listener.onReceiveSuccess(PhoneUtil.getCallsRecord());
                }else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CALL_LOG)) {
                        Log.i("===", "用户拒绝过一次权限，解释给用户为什么需要该权限");
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CALL_LOG},REQUEST_PERMISSION_CODE);
                    } else {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CALL_LOG},REQUEST_PERMISSION_CODE);
                        Log.i("===", "用户选择不再弹窗或者第一次申请权限");
                    }

                }
            }
        }).start();

    }
    public static void getAllConstats(final Activity context, final OnPrivateInfoListener<PhoneUserEntity> listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(checkPermission(Manifest.permission.READ_CONTACTS)){
                    listener.onReceiveSuccess(PhoneUtil.getAllPhoneUser());
                }else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
                        Log.i("===", "用户拒绝过一次权限，解释给用户为什么需要该权限");
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE);
                    } else {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE);
                        Log.i("===", "用户选择不再弹窗或者第一次申请权限");
                    }

                }
            }
        }).start();

    }

    /**
     * 权限检查，避免因没有权限抛出异常
     * @param permName
     * @return
     */
    private static boolean checkPermission(String permName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(Global.getContext(), permName) == PackageManager.PERMISSION_GRANTED;
        } else {
            //这个问题貌似无解。
            //6.0以下，涉及到权限检查和调出授权对话框都是无效的。
            PackageManager pm = Global.getContext().getPackageManager();
            if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, "com.huxin.afinance")) {
                System.out.println("com.huxin.afinance" + "has permission : " + permName);
                return true;
            } else {
                //PackageManager.PERMISSION_DENIED == pm.checkPermission(permName, pkgName)
                System.out.println("com.huxin.afinance" + "not has permission : " + permName);
                return false;
            }
        }
    }

    /**
     * changfeng
     * 备用
     * 防止连续多次调用某个方法，调用前先判断是否是快速点击
     * 是（true）则不执行某个方法，反之
     */
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
