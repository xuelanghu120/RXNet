package com.huxin.common.utils.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 */
public class ImageFileUtils {

    public static Observable<String> saveBitmap(final Context context, final Bitmap b, final String path, final String fileName) {
        return Observable.just(fileName)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return ImageFileUtils.saveImage(b, path, fileName);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ImageFileUtils.insertImage(context, s);
                    }
                });
    }

    /**
     * 初始化SD路径
     */
    public static String initSDcardPaht(Context context) {
        String sdCardPath;
        if (isSDCardExistReal()) {
            sdCardPath = Environment.getExternalStorageDirectory().toString() + File.separator;
        } else {
            sdCardPath = context.getFilesDir().getAbsolutePath() + File.separator;
        }
        return sdCardPath;
    }

    public static void initPath(String path) {
        Observable.just(path)
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        initFilePath(s);
                    }
                }).subscribe();
    }

    /**
     * 初始化路径
     *
     * @param path
     * @return
     */
    public static String initFilePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return path;
    }

    /**
     * 是否存在文件
     *
     * @param path
     * @return
     */
    public static boolean exists(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     * @param namePath
     */
    public static void delete(String namePath) {
        File file = new File(namePath);
        if (file.exists()) {
            delete(file);
        }
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean isSDCardExistReal() {
        boolean isExits = false;
        isExits = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        return isExits;
    }

    /**
     * 存储图片
     *
     * @param b
     * @param path
     * @param fileName
     * @return
     */
    public static String saveImage(Bitmap b, String path, String fileName) {
        String jpegName = path + "/" + fileName;
        try {
            if (ImageUtils.needRoll(b)) {
                b = ImageUtils.adjustPhotoRotation(b, 90);
            }
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            if (!b.isRecycled()) {
                b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            }
            bos.flush();
            bos.close();
            b.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegName;
    }

    /**
     * 发送新图片插入广播
     *
     * @param context
     * @param path
     */
    public static void insertImage(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

}
