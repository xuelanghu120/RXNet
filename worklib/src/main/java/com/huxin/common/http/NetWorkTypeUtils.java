package com.huxin.common.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

/***
 * NetWork工具类
 * 获取APN_TYPE接入点类型
 */
public class NetWorkTypeUtils {

    private static final String TAG = "NetWorkTypeUtils";

    /**
     * 没有网络
     */
    public static final int NETWORKTYPE_INVALID = 0;
    /**
     * wap网络
     */
    public static final int NETWORKTYPE_WAP = 1;
    /**
     * 2G网络
     */
    public static final int NETWORKTYPE_2G = 2;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final int NETWORKTYPE_3G = 3;
    /**
     * wifi网络
     */
    public static final int NETWORKTYPE_WIFI = 4;


    /**
     * 中国移动
     */
    public static final int OPERATOR_CHINA_MOBILE = 0;

    /**
     * 中国联通
     */
    public static final int OPERATOR_CHINA_UNICOM = 1;

    /**
     * 中国电信
     */
    public static final int OPERATOR_CHINA_TELECOM = 2;

    /**
     * 未知运营商
     */
    public static final int OPERATOR_UNKNOWN = -1;

    public static final int APN_WIFI = 0;

    public static final int APN_CMWAP = 1;

    public static final int APN_CMNET = 2;

    public static final int APN_UNIWAP = 3;

    public static final int APN_UNINET = 4;

    public static final int APN_3GWAP = 5;

    public static final int APN_3GNET = 6;

    public static final int APN_CTWAP = 7;

    public static final int APN_CTNET = 8;

    public static final String APN_WIFI_NAME = "wifi";

    public static final String APN_CMWAP_NAME = "cmwap";

    public static final String APN_CMNET_NAME = "cmnet";

    public static final String APN_UNIWAP_NAME = "uniwap";

    public static final String APN_UNINET＿NAME = "uninet";

    public static final String APN_3GWAP_NAME = "3gwap";

    public static final String APN_3GNET_NAME = "3gnet";

    public static final String APN_CTWAP_NAME = "ctwap";

    public static final String APN_CTNET_NAME = "ctnet";

    public static final int APN_UNKNOWN = -1;

    private static boolean isNetworkActive = true;

    public static boolean isNetworkWifi = false;

    private static int mNetWorkType;

    /**
     * 获取移动网络接入点类型
     *
     * @param context
     * @return
     */
    private static int getMobileApnType(Context context) {
        final boolean isWap = isWap();
        final int operator = getSimOperator(context);
        switch (operator) {
            case OPERATOR_CHINA_MOBILE: // 中国移动
                if (isWap) {
                    return APN_CMWAP;
                } else {
                    return APN_CMNET;
                }
            case OPERATOR_CHINA_UNICOM: // 中国联通
                // 先判断是2g还是3g网络
                TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                final int networkType = telManager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSDPA: // 联通3g
                        if (isWap) {
                            return APN_3GWAP;
                        } else {
                            return APN_3GNET;
                        }
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 联通2g
                        if (isWap) {
                            return APN_UNIWAP;
                        } else {
                            return APN_UNINET;
                        }
                    default:
                        return APN_UNKNOWN;
                }
            case OPERATOR_CHINA_TELECOM: // 中国电信
                if (isWap) {
                    return APN_CTWAP;
                } else {
                    return APN_CTNET;
                }
            default:
                return APN_UNKNOWN;
        }
    }

    /**
     * 获取移动网络运营商
     *
     * @param context
     * @return
     */
    public static int getSimOperator(Context context) {
        String networkOperator= getNetworkOperator(context);
        if (!TextUtils.isEmpty(networkOperator)) {
            if (networkOperator.equals("46000") || networkOperator.equals("46002") || networkOperator.equals("46007")) {
                return OPERATOR_CHINA_MOBILE;
            } else if (networkOperator.equals("46001")) {
                return OPERATOR_CHINA_UNICOM;
            } else if (networkOperator.equals("46003")) {
                return OPERATOR_CHINA_TELECOM;
            }
        }
        return OPERATOR_UNKNOWN;
    }

    /**
     * 判断是否是wap类网络
     *
     * @return
     */
    public static boolean isWap() {
        String host = Proxy.getDefaultHost();
        return !TextUtils.isEmpty(host);
    }

    /**
     * 获取接入点类型
     *
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager == null) {
            isNetworkActive = false;
            return APN_UNKNOWN;
        }
        NetworkInfo info = conManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            isNetworkActive = false;
            return APN_UNKNOWN;
        }
        isNetworkActive = true;
        int type = info.getType();
        if (type == ConnectivityManager.TYPE_WIFI) { // wifi网络
            isNetworkWifi = true;
            return APN_WIFI;
        } else {
            return getMobileApnType(context);
        }
    }

    public static final boolean isNetworkTypeWifi(Context context) {
        boolean flag = false;
        Context mContext = context;
        if (getNetworkType(mContext) == APN_WIFI) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取接入点名称
     *
     * @param context
     * @return
     */
    public static String getNetworkName(Context context) {
        final int netWorkType = getNetworkType(context);
        switch (netWorkType) {
            case NetWorkTypeUtils.APN_WIFI:
                return NetWorkTypeUtils.APN_WIFI_NAME;
            case NetWorkTypeUtils.APN_CMWAP:
                return NetWorkTypeUtils.APN_CMWAP_NAME;
            case NetWorkTypeUtils.APN_CMNET:
                return NetWorkTypeUtils.APN_CMNET_NAME;
            case NetWorkTypeUtils.APN_3GWAP:
                return NetWorkTypeUtils.APN_3GWAP_NAME;
            case NetWorkTypeUtils.APN_3GNET:
                return NetWorkTypeUtils.APN_3GNET_NAME;
            case NetWorkTypeUtils.APN_UNIWAP:
                return NetWorkTypeUtils.APN_UNIWAP_NAME;
            case NetWorkTypeUtils.APN_UNINET:
                return NetWorkTypeUtils.APN_UNINET＿NAME;
            case NetWorkTypeUtils.APN_CTWAP:
                return NetWorkTypeUtils.APN_CTWAP_NAME;
            case NetWorkTypeUtils.APN_CTNET:
                return NetWorkTypeUtils.APN_CTNET_NAME;
            default:
                return "NA";
        }
    }

    public static boolean isNetworkActive() {
        return isNetworkActive;
    }

    /**
     * 获取网络状态，wifi,wap,2g,3g.
     *
     * @param context 上下文
     * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G},          *{@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}* <p>{@link #NETWORKTYPE_WIFI}
     */

    public static int getNetWorkType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = Proxy.getDefaultHost();

                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
                        : NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }

        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 判断当前是否有可用的网络连接，不考虑是wifi or 3g
     *
     * @param context
     * @return
     */
    public static boolean isConnnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();

            if (null != networkInfo) {
                for (NetworkInfo info : networkInfo) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        Logger.e(TAG, "the net is ok");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        if (APN_WIFI == getNetworkType(context)) {
            return true;
        }
        return false;

    }

    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接
     * 设置一些自己的逻辑调用
     */
    public static void isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING) {
            Toast.makeText(context, "当前是手机流量", Toast.LENGTH_LONG).show();
        }
        //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            Toast.makeText(context, "当前是wifi环境", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 获得 SIM卡唯一标识：IMSI 国际移动用户识别码IMSI：
     *
     * @return
     */
    public static String getNetworkOperator(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telManager.getNetworkOperator();
        return networkOperator;
    }
}
