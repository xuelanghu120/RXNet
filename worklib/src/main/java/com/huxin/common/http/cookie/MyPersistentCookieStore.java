package com.huxin.common.http.cookie;

import android.content.Context;
import android.content.SharedPreferences;

import com.huxin.common.http.cookie.entity.SerializableOkHttpCookies;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by 56417 on 2016/9/19.
 */

public class MyPersistentCookieStore {
    private final String LOG_TAG = "MyPersistentCookieStore";
    private static final String MY_COOKIE_PREFS = "My_Cookies_Prefs";

    private HashMap<String, Cookie> myCookiesMap = new HashMap<>();
    private final SharedPreferences mycookiePrefs;

    private volatile static MyPersistentCookieStore instance;

    public static MyPersistentCookieStore getInstance(Context context) {
        if (instance == null) {
            synchronized (MyPersistentCookieStore.class) {
                if (instance == null) {
                    instance = new MyPersistentCookieStore(context);
                }
            }
        }
        return instance;
    }


    public MyPersistentCookieStore(Context context) {
        mycookiePrefs = context.getSharedPreferences(MY_COOKIE_PREFS, 0);

        //将持久化的cookies缓存到内存中 即map cookies
        getCookiesFromSp(mycookiePrefs, myCookiesMap);

    }

    private HashMap<String, Cookie> getCookiesFromSp(SharedPreferences cookiePrefs, HashMap<String, Cookie> cookies) {
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            String encodedCookie = cookiePrefs.getString(entry.getKey(), null);
            if (encodedCookie != null) {
                Cookie decodedCookie = decodeCookie(encodedCookie);
                if (decodedCookie != null) {
                    cookies.put(decodedCookie.name(), decodedCookie);
                }
            }
        }
        return cookies;
    }

    public void add(HttpUrl url, Cookie cookie) {

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (System.currentTimeMillis() < cookie.expiresAt()) {
            myCookiesMap.put(cookie.name(), cookie);
        } else {
            myCookiesMap.remove(cookie.name());
        }

        //讲cookies持久化到本地
        SharedPreferences.Editor prefsWriter = mycookiePrefs.edit();
        //根据u，d，t单独存储
        prefsWriter.putString(cookie.name(), encodeCookie(new SerializableOkHttpCookies(cookie)));

        prefsWriter.apply();
    }

    public List<Cookie> getMyCookies() {
        List<Cookie> cookies = new ArrayList<>();
        for (Map.Entry<String, Cookie> entity : myCookiesMap.entrySet()) {
            cookies.add(entity.getValue());
        }
        return cookies;
    }

    public Map<String ,String> getCookieMap(){
       Map<String, String> myCookiesStringMap = new HashMap<>();
        for (Map.Entry<String, Cookie> entity : myCookiesMap.entrySet()) {
            Cookie cookie = entity.getValue();
            myCookiesStringMap.put(cookie.name(), String.valueOf(entity.getValue()));
        }
        return myCookiesStringMap;
    }

    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = mycookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        myCookiesMap.clear();
        return true;
    }

    public boolean remove(Cookie cookie) {

        if (myCookiesMap.containsKey(cookie.name())) {
            SharedPreferences.Editor prefsWriter = mycookiePrefs.edit();
            if (mycookiePrefs.contains(cookie.name())) {
                prefsWriter.remove(cookie.name());
            }
            myCookiesMap.remove(cookie.name());
            prefsWriter.apply();
            return true;
        } else {
            return false;
        }
    }


    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    protected String encodeCookie(SerializableOkHttpCookies cookie) {
        if (cookie == null)
            return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Logger.d(LOG_TAG, "IOException in encodeCookie" + e.getMessage());
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableOkHttpCookies) objectInputStream.readObject()).getCookies();
        } catch (IOException e) {
            Logger.d(LOG_TAG, "IOException in decodeCookie"+ e.getMessage());
        } catch (ClassNotFoundException e) {
            Logger.d(LOG_TAG, "ClassNotFoundException in decodeCookie"+ e.getMessage());
        }

        return cookie;
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
