package com.huxin.common.http.builder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认builder
 */
public class DefaultURLBuilder implements URLBuilder {

    protected String url;
    protected Map<String, Object> paramsMap;
    //req对象解析成map，请求的参数
    @Override
    public void parse(Path path, Map<String, Field> fields,
                      ParamEntity entity) throws IllegalAccessException {
        url = path.host() + path.url();
        paramsMap = new HashMap<String, Object>();
        if (fields != null) {
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                Object value = entry.getValue().get(entity);
                if (value != null) {
                    paramsMap.put(entry.getKey(), String.valueOf(value));
                }
            }
        }
        //增加通用参数
        addCommonParams(paramsMap);
    }
    //在ok请求网络的时候写入的url
    @Override
    public String getUrl() {
        return url;
    }
    //返回组合的请求参数的map
    @Override
    public Map<String, Object> getParams() {
        return paramsMap;
    }
    //缓存以后有缓存的参数
    @Override
    public Map<String, Object> getCacheKeyParams() {
        return getParams();
    }
    //请求的类型，post请求时使用，暂时是三个，json，kv，file三种形式
    @Override
    public byte getReqType() {
        return REQ_TYPE_KV;
    }

    /**
     * 填入通用参数
     *
     * @param tempParams
     */
    public void addCommonParams(Map<String, Object> tempParams) {
    }
}
