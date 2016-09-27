package com.huxin.common.http.builder;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huxin.common.http.OkHttpWork;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 请求pam转化RequestBody
 */
public class MapParamsConverter {
    private static final String TAG = "MapParamsConverter";
    public final static String FILE_KEY = "file";

    public static RequestBody map2ForBody(Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map == null || map.size() == 0) return builder.build();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            if (key != null && value != null) {
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    /**
     * 用于FILE上传
     *
     * @param map
     * @return
     */
    public static RequestBody map2ForMultBody(Map<String, Object> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (map == null || map.size() == 0) return builder.build();
        Log.d(TAG, "map2ForMultBody: map.size:" + map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(FILE_KEY)) {
                String value = (String) entry.getValue();
                builder.addFormDataPart(entry.getKey(), value);
            } else {
                List<String> files = (List<String>) entry.getValue();
                if (files == null || files.size() == 0) {
                    throw new IllegalArgumentException("没有要上传的文件");
                }
                for (String file : files) {
                    File uploadFile = new File(file);
                    RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(uploadFile.getName())), uploadFile);
                    builder.addFormDataPart(uploadFile.getName(), uploadFile.getName(), fileBody);
                }
            }
        }
        return builder.build();
    }

    public static RequestBody map2ForJSON(Map<String, Object> map) {
        String jsonStirng = "";
        Log.d(TAG, "map2ForMultBody: map.size:" + map.size());
        Log.d(TAG, "map2ForMultBody: map:" + map.toString());
        Gson gson = new GsonBuilder().create();
        if (map.size() > 0) {
            jsonStirng = gson.toJson(map);
        }
        Log.d(TAG, "map2ForMultBody: jsonStirng:" + jsonStirng);
        RequestBody body = RequestBody.create(OkHttpWork.JSON, jsonStirng);
        return body;
    }

    //获取上传文件的类型
    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


}

