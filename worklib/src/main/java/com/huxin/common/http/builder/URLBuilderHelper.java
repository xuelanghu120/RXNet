package com.huxin.common.http.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * url辅助类
 */
public class URLBuilderHelper {

    public static String replaceQueryStringParamNames(String url, Map<String, Object> paramsMap) {
        if (url.contains("$")) {
            int qIndex = url.indexOf('?');
            if (qIndex < url.length() - 1) {
                String query = url.substring(qIndex + 1);
                String[] kvArray = query.split("&");
                for (String kv : kvArray) {
                    String name = kv.split("=")[0];
                    if (paramsMap.containsKey(name)) {
                        url = url.replaceFirst("\\$", String.valueOf(paramsMap.get(name)));
                        paramsMap.remove(name);
                    }
                }
            }
        }
        return url;
    }

    public static String getSignStr(String[] signParams, Map<String, Object> paramsMap) {
        String result = "";

        if (signParams != null && signParams.length > 0 && paramsMap != null && paramsMap.size() > 0) {
            for (String key : signParams) {
                Object item = paramsMap.get(key);
                if (item != null) {
                    result += item;
                }
            }
        }
        return result;
    }

    public static String getUrlStr(String url, Map<String, Object> paramsMap) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(url + "?");
        if (null == paramsMap) {
            paramsMap = new HashMap<>();
        }
        if (paramsMap != null && paramsMap.size() > 0) {
            Iterator<Map.Entry<String, Object>> entryIterator = paramsMap.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, Object> entry = entryIterator.next();
                String k = entry.getKey();
                String v = entry.getValue().toString();
                int index = urlBuffer.lastIndexOf("?");
                if (index != urlBuffer.length()-1) {
                    urlBuffer.append("&");
                }
                urlBuffer.append(k + "=" + v);
            }
        }
        return urlBuffer.toString();
    }
}
