package com.huxin.common.utils.file;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by 56417 on 2016/9/23.
 */

public class FileUtils {

    public static boolean exists(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
        }
        return false;
    }
}
