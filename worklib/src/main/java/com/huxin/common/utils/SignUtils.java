package com.huxin.common.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by changfeng on 2016/9/13.
 * 验签
 */
public class SignUtils {
    public static final String APPID_KEY = "appId";
    public static final String SIGN_KEY = "sign";
    public static final String APPID_VALUE = "2016650431";


    private static final String tag = "SignUtils";
    private static final String key = "daba1f5e7f451ef61d8dce4441ba8b52";

    /**
     * @param map 要加密的参数集合map
     * @return 实际要上传的参数集合
     */
    public static String encryptHmac(Map map){
        map.put(APPID_KEY, APPID_VALUE);
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacMD5");

        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacMD5");
            mac.init(secretKey);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(tag, getSortVaules(map));
        byte[] resultBytes = mac.doFinal(getSortVaules(map).getBytes());
        String resultString = bytesToHexString(resultBytes);
        Log.i(tag, "sign:" + resultString);
        return resultString;
    }

    /**
     * byte数组转成成16进制字符串
     *
     * @param src
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 给map集合里的value，根据key的自然顺序，排列在一起
     *
     * @param map
     * @return 所有value连成字符串
     */
    private static String getSortVaules(Map map) {
        Object[] key = map.keySet().toArray();
        Arrays.sort(key);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < key.length; i++) {
            buffer.append(map.get(key[i]));
        }
        Log.i(tag, buffer.toString());
        return buffer.toString();
    }
}
