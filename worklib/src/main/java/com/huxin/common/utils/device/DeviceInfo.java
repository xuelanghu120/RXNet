package com.huxin.common.utils.device;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.huxin.common.application.Global;

public class DeviceInfo {

    public final static int ICE_CREAM_SANDWICH_MR1 = 15;
    public final static int JELLY_BEAN = 16;
    public final static int JELLY_BEAN_MR1 = 17;
    public final static int JELLY_BEAN_MR2 = 18;
    //ma地址c
    public static String MACADDRESS;
    //屏幕信息
    public static String RESOLUTION;

    private static String sPackageName;
    private static String sVersionName;
    private static int sVersionCode;

    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        MACADDRESS = getLocalMacAddress(context);
        RESOLUTION = getResolution(context);
    }

    public static String getModelAndFactor() {
        return Build.MODEL + "/" + Build.MANUFACTURER;
    }

    /**
     * 获得手机型号
     *
     * @return
     */
    public static String getMobileModel() {
        return Build.MODEL;
    }

    /**
     * 获得手机制造商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getUuid() {
        return new DeviceUuidFactory(sContext).getDeviceUuid();
    }

    public static String getOsVersion() {
        String sdkStr = Build.VERSION.SDK_INT + "";
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.BASE:
                sdkStr = "October 2008: The original, first, version of Android.";
                break;
            case Build.VERSION_CODES.BASE_1_1:
                sdkStr = "Android 1.1.";
                break;
            case Build.VERSION_CODES.CUPCAKE:
                sdkStr = " Android 1.5";
                break;
            case Build.VERSION_CODES.CUR_DEVELOPMENT:
                break;
            case Build.VERSION_CODES.DONUT:
                sdkStr = "Android 1.6";
                break;
            case Build.VERSION_CODES.ECLAIR:
                sdkStr = "Android 2.0";
                break;
            case Build.VERSION_CODES.ECLAIR_0_1:
                sdkStr = "Android 2.0.1";
                break;
            case Build.VERSION_CODES.ECLAIR_MR1:
                sdkStr = "Android 2.1";
                break;
            case Build.VERSION_CODES.FROYO:
                sdkStr = "Android 2.2";
                break;
            case Build.VERSION_CODES.GINGERBREAD:
                sdkStr = "Android 2.3.3";
                break;
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                sdkStr = "Android 2.3.3";
                break;
            case Build.VERSION_CODES.HONEYCOMB:
                sdkStr = "Android 3.0";
                break;
            case Build.VERSION_CODES.HONEYCOMB_MR1:
                sdkStr = "Android 3.1";
                break;
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                sdkStr = "Android 3.2";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                sdkStr = "Android 4.0";
                break;
            case ICE_CREAM_SANDWICH_MR1:
                sdkStr = "Android 4.0.3";
                break;
            case JELLY_BEAN:
                sdkStr = "Android 4.1";
                break;
            case JELLY_BEAN_MR1:
                sdkStr = "Android 4.2";
                break;
            case JELLY_BEAN_MR2:
                sdkStr = "Android 4.3";
                break;
            case Build.VERSION_CODES.KITKAT:
                sdkStr = "Android 4.4";
                break;
            case Build.VERSION_CODES.LOLLIPOP:
                sdkStr = "Android LOLLIPOP";
                break;
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                sdkStr = "Android LOLLIPOP_MR1";
                break;
            case Build.VERSION_CODES.M:
                sdkStr = "Android M";
                break;
            case Build.VERSION_CODES.N:
                sdkStr = "Android N";
                break;
            default:
                break;
        }
        return sdkStr;
    }

    public static String getPackageName() {
        if (TextUtils.isEmpty(sPackageName)) {
            sPackageName = Global.getContext().getPackageName();
        }
        return sPackageName;
    }

    public static String getVersionName() {
        if (TextUtils.isEmpty(sVersionName)) {
            try {
                PackageInfo info = Global.getContext().getPackageManager()
                        .getPackageInfo(getPackageName(), 0);
                sVersionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        }
        return sVersionName;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        if (0 == sVersionCode) {
            try {
                PackageInfo info = Global.getContext().getPackageManager()
                        .getPackageInfo(getPackageName(), 0);
                sVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return sVersionCode;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    private static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取屏幕信息
     *
     * @param context
     * @return
     */
    private static String getResolution(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.widthPixels) + "*" + String.valueOf(metrics.heightPixels);

//        DisplayMetrics dm = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        return String.valueOf(dm.widthPixels) + "*" + String.valueOf(dm.heightPixels);
    }
}
