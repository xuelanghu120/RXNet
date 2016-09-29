package com.huxin.common.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;


import com.huxin.common.utils.file.FileUtils;
import com.huxin.common.utils.image.entity.ImageItem;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * 图片压缩工具类
 */
public class ImageCompressUtil {

    private static final String TAG = "ImageCompressUtil";
    //已经压缩的图片集合
    private static ArrayList<ImageItem> compressImageList = new ArrayList<>();

    private static Quality defauQuality = Quality.QUALITY_80;

    public enum Quality {
        QUALITY_ORIGINAL, QUALITY_90, QUALITY_80, QUALITY_70, QUALITY_60,
    }

    public static Bitmap getBitmap(InputStream is) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, option);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 通过压缩图片的尺寸来压缩图片大小，通过读入流的方式，可以有效防止网络图片数据流形成位图对象时内存过大的问题；
     *
     * @param is           要压缩图片，以流的形式传入
     * @param targetWidth  缩放的目标宽度
     * @param targetHeight 缩放的目标高度
     * @return 缩放后的图片
     * @throws IOException 读输入流的时候发生异常
     */
    public static Bitmap compressBySize(InputStream is, int targetWidth, int targetHeight)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }

        byte[] data = baos.toByteArray();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        return bitmap;
    }

    /**
     * 压缩图片，自动计算要压缩的宽和高，默认压缩质量为原图的60%
     *
     * @param fromPath 原始图片路径
     * @param toPath   压缩后的图片的保存路径
     * @return
     */
    public static File compressImage(String fromPath, String toPath) {
        try {
            FileInputStream is = new FileInputStream(fromPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            is.close();
            byte[] data = baos.toByteArray();
            int degree = BitmapUtils.getBitmapDegree(new File(fromPath));

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;

            int[] params = ImageCompressUtil.calculateWidhtHeight(bitmapWidth, bitmapHeight);
            int width = params[0];
            int height = params[1];
            opts.inJustDecodeBounds = false;

            opts.inDither = false;
            opts.inPurgeable = true;
            opts.inTempStorage = new byte[12 * 1024];

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            File file = scaleImage(toPath, width, height, defauQuality, bitmap, bitmapWidth,
                    bitmapHeight, degree);

            return file;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "compressImage: " + e.getStackTrace());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 压缩图片，自动计算要压缩的宽和高
     *
     * @param fromPath 原始图片路径
     * @param toPath   压缩后的图片路径
     * @param quality  压缩质量
     * @return
     */
    public static File compressImage(String fromPath, String toPath, Quality quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromPath);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int[] params = ImageCompressUtil.calculateWidhtHeight(bitmapWidth, bitmapHeight);
            int width = params[0];
            int height = params[1];
            int degree = BitmapUtils.getBitmapDegree(new File(fromPath));
            File file = scaleImage(toPath, width, height, quality, bitmap, bitmapWidth,
                    bitmapHeight, degree);

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按照指定的分辨率和质量压缩图片到指定目录
     *
     * @param fromPath
     * @param toPath
     * @param width
     * @param height
     * @param quality
     */
    public static File compressImage(String fromPath, String toPath, int width, int height,
                                     Quality quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromPath);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int degree = BitmapUtils.getBitmapDegree(new File(fromPath));
            File file = scaleImage(toPath, width, height, quality, bitmap, bitmapWidth,
                    bitmapHeight, degree);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "compressImage", e);
            return null;
        }
    }

    private static File scaleImage(String toPath, int width, int height, Quality quality,
                                   Bitmap bitmap, int bitmapWidth, int bitmapHeight, int degree)
            throws FileNotFoundException, IOException, NullPointerException {
        // 缩放图片的尺寸
        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix,
                true);

        if (degree > 0) {
            resizeBitmap = BitmapUtils.rotateBitmapByDegree(resizeBitmap, degree);
        }

        // 保存图片到指定的目录
        File myCaptureFile = null;
        try {
            myCaptureFile = new File(toPath);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            int qualityValue = getImageQuality(quality);

            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, qualityValue, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();// 释放资源，否则容易内存溢出 fuck
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myCaptureFile;
    }

    private static int getImageQuality(Quality quality) {
        int qualityValue = 100;
        switch (quality) {
            case QUALITY_ORIGINAL:
                qualityValue = 100;
                break;
            case QUALITY_90:
                qualityValue = 90;
                break;
            case QUALITY_80:
                qualityValue = 80;
                break;
            case QUALITY_70:
                qualityValue = 80;
                break;
            case QUALITY_60:
                qualityValue = 80;
                break;
            default:
                break;
        }
        return qualityValue;
    }

    private static final int DEFAULT_IMAGE_MAX_HEIGHT = 800;
    private static final int DEFAULT_IMAGE_MAX_WIDTH = 640;

    /**
     * 计算出合适的图片分辨率
     *
     * @param width
     * @param height
     * @return
     */
    public static int[] calculateWidhtHeight(int width, int height) {
        int[] params = new int[2];
        // 得到图片比例
        double percent = ((double) width) / height;
        int mWidth;
        int mHeight;
        if (width > height) {
            Logger.d(TAG, "宽图");
            int ratio = width / height;
            if (ratio > 2.5) {
                // 超级大宽图
                if (height >= DEFAULT_IMAGE_MAX_HEIGHT) {
                    // 计算应该缩放的宽
                    mWidth = (int) (DEFAULT_IMAGE_MAX_HEIGHT * percent);
                    mHeight = DEFAULT_IMAGE_MAX_HEIGHT;
                } else {
                    mWidth = width;
                    mHeight = height;
                }
            } else {
                // 小宽图
                if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                    mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                    mHeight = (int) (DEFAULT_IMAGE_MAX_WIDTH / percent);
                } else {
                    mWidth = width;
                    mHeight = height;
                }
            }
        } else if (height > width) {
            Logger.d(TAG, "长图");
            if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                // 计算应该缩放的高
                mHeight = (int) (DEFAULT_IMAGE_MAX_WIDTH / percent);
                params[0] = mWidth;
                params[1] = mHeight;
            } else {
                mWidth = width;
                mHeight = height;
            }
        } else {// 宽高一致

            Logger.d(TAG, "正方形图");

            if (width >= DEFAULT_IMAGE_MAX_WIDTH) {
                mWidth = DEFAULT_IMAGE_MAX_WIDTH;
                mHeight = DEFAULT_IMAGE_MAX_WIDTH;
            } else {
                mWidth = width;
                mHeight = height;
            }
        }
        params[0] = mWidth;
        params[1] = mHeight;
        return params;
    }


    /**
     * 图片压缩
     * @param items
     * @param tempPath
     * @return
     */
    public static ArrayList<ImageItem> compressImage(ArrayList<ImageItem> items, String tempPath) {
        compressImageList.clear();
        for (ImageItem item : items) {
            ImageItem compressItem = new ImageItem(item);
            String path = compressItem.getImagePath();
            String imageId = compressItem.getImageId();

            if (path.startsWith("#")) {
                continue;
            }
            // 判断该图片是否已经压缩过，如果压缩过，则跳过不压缩
            boolean isCompress = isCompress(imageId);
            if (isCompress) {
                Logger.w(TAG, item.getImageId() + " 已经压缩过，无需再次压缩");
                continue;
            }

            File file = new File(path);
            if (file.exists()) {
                FileUtils.initFilePath(tempPath);//修复bug 目录不存在
                String savePath = tempPath + item.getImageId() + ".jpg";
                File saveFile = compressImage(path, savePath);
                if (saveFile != null && saveFile.exists()) {
                    Logger.d(TAG, "压缩前图片大小：" + (file.length() / 1024) + " kb");
                    Logger.d(TAG, "压缩后图片大小：" + (saveFile.length() / 1024) + " kb");
                    String uploadThumbnailPath = saveFile.getAbsolutePath();
                    compressItem.setUploadThumbnailPath(uploadThumbnailPath);
                    compressImageList.add(compressItem);
                } else {
                    Logger.e(TAG, "压缩图片保存失败，原图上传");
                    // 压缩图片失败，比如出现内存溢出等问题就上传原图
                    compressItem.setUploadThumbnailPath(path);
                    compressImageList.add(compressItem);
                }
            }
        }
        return compressImageList;
    }

    private static boolean isCompress(String imageId) {
        if (compressImageList != null && compressImageList.size() > 0) {
            for (ImageItem item : compressImageList) {
                if (item.getImageId().equals(imageId)) {
                    return true;
                }
            }
        }
        return false;
    }

}
