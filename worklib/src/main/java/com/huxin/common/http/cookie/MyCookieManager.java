package com.huxin.common.http.cookie;



import com.huxin.common.application.Global;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by 56417 on 2016/9/19.
 */

/**
 * 自动管理Cookies
 */
public class MyCookieManager implements CookieJar {
    private final MyPersistentCookieStore cookieStore = MyPersistentCookieStore.getInstance(Global.getContext());

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.getMyCookies();
    }
}

