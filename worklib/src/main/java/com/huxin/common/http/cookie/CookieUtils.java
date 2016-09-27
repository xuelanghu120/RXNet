package com.huxin.common.http.cookie;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


import com.huxin.common.application.Global;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

/**
 * Created by 56417 on 2016/9/21.
 */

public class CookieUtils {

    /**
     * 删除cookies，当native退出登录时，或cookies失效时，要删除数据库中的cookies。
     * 该方法要和
     */
    public static void removeCookies() {
        CookieSyncManager.createInstance(Global.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

    /**
     * 同步（种）cookies，实质就是往数据库中存储cookie，当webview加载页面或请求服务器接口时，
     * 会自动去数据库取，根据不同的域名取对应的cookies。
     * 该方法在每次登录后（webview初始化和加载url前）调用。
     *
     * @param domains
     */
    public static void sysnCookies(@NonNull List<String> domains, @Nullable Map<String, String> cookies) {
        CookieSyncManager.createInstance(Global.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (int i = 0; i < domains.size(); i++) {
            String url = domains.get(i);
            //cookies有值，就把新的cookies同步，心得cookies包括：1、key没变，会覆盖以前cookie。
            //2、key变了，数据库会添加新的cookie，请求到所对应的域名时会带上新cookie；
            Iterator iter = cookies.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = ((String) entry.getKey());
                String val = (String) entry.getValue();
                cookieManager.setCookie(url, key + "=" + val);
            }
            if (Build.VERSION.SDK_INT < 21) {
                CookieSyncManager.getInstance().sync();
            } else {
                CookieManager.getInstance().flush();
            }
        }
    }

    /**
     * url种cookie
     * @param domains
     * @param cookies
     */
    public static void sysnCookies(@NonNull List<String> domains, List<Cookie> cookies) {
        CookieSyncManager.createInstance(Global.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (int i = 0; i < domains.size(); i++) {
            String url = domains.get(i);
            //cookies有值，就把新的cookies同步，心得cookies包括：1、key没变，会覆盖以前cookie。
            //2、key变了，数据库会添加新的cookie，请求到所对应的域名时会带上新cookie；
            for (Cookie cookie : cookies){
//                if (cookie.name().equals("U") || cookie.name().equals("T") || cookie.name().equals("D")){
                    cookieManager.setCookie(url, cookie.name() + "=" + cookie.value());
//                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                CookieSyncManager.getInstance().sync();
            } else {
                CookieManager.getInstance().flush();
            }
        }
    }
}
