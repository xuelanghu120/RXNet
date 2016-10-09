package com.huxin.common.http.responser;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 基类返回体
 */
public abstract class AbstractResponser {

    public final String RESULT = "result";
    public final String RESULT_OK = "OK";
    public final String RESULT_CODE = "code";
    public final String RESULT_MSG= "msg";
    public boolean isCache = false;
    public boolean isSuccess = false;
    public String errorMessage = "网络不稳定，请重试";
    public String errorCode = RESULT_OK;

    public void parser(final String result) {
        parseHeader(result);
        parserBody(result);
    }

    public boolean isCache() {
        return isCache;
    }

    /**
     * 解析成具体的entity类，返回体自己实现
     * @param result
     */
    public abstract void parserBody(final String result);
    //获取错误的msg
    public abstract String getErrorDesc(JSONObject jsonObject);
    //判断业务返回是否成功
    public abstract boolean isSuccess(JSONObject dataObject);

    /**
     * 主要是判断请求网络是否请求成功
     * @param result
     * @return
     */
    public JSONObject parseHeader(String result) {
        JSONObject dataObject = null;
        if (TextUtils.isEmpty(result)) {
            return dataObject;
        }
        try {
            dataObject = new JSONObject(result);
            isSuccess = isSuccess(dataObject);
            getErrorDesc(dataObject);
        } catch (JSONException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return dataObject;
    }


}
