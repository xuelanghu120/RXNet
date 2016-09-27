package com.huxin.common.utils.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huxin.common.utils.LocalStringUtils;
import com.orhanobut.logger.Logger;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    private static final String TAG = "BitmapUtils";
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    /**
     * 这个是等比例缩放
     */
    public static byte[] getZoomImage(Bitmap sourceBitmap, int widthLimit, int heightLimit, boolean isThumbnail, int quality) {
        InputStream input = null;
        try {
            int sourceWidth = sourceBitmap.getWidth();
            int sourceHeight = sourceBitmap.getHeight();
            float scale = 1f;
            if (isThumbnail) {
                if (sourceWidth > sourceHeight) {
                    scale = widthLimit * 1.0f / sourceWidth;
                    if (scale * sourceHeight > heightLimit) {
                        scale = heightLimit * 1.0f / sourceHeight;
                    }
                } else {
                    scale = heightLimit * 1.0f / sourceHeight;
                    if (scale * sourceWidth > widthLimit) {
                        scale = widthLimit * 1.0f / sourceWidth;
                    }
                }
            } else {
                if (sourceWidth > sourceHeight) {
                    if (sourceWidth > widthLimit) {
                        scale = widthLimit * 1.0f / sourceWidth;
                    }
                } else {
                    if (sourceHeight > heightLimit) {
                        scale = heightLimit * 1.0f / sourceHeight;
                    }
                }
            }

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, os);
            bitmap.recycle();
            bitmap = null;
            return os.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 图片圆角
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, final float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        int roundColor = 0xff424242;
        paint.setColor(roundColor);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.reset();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap compositeBitmap(Bitmap baseBM, Bitmap frontBM) {
        Canvas canvas = new Canvas(baseBM);
        final Paint paint = new Paint();
        canvas.drawBitmap(baseBM, 0, 0, paint);
        final Rect rect = new Rect(0, 0, baseBM.getWidth(), baseBM.getHeight());
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        canvas.drawBitmap(frontBM, rect, rect, paint);
        return baseBM;
    }

    /**
     * 根据路径获取图标的bitmap
     *
     * @return
     */
    public static Bitmap createBitmapByPath(Context context, String path) {
        InputStream input = null;
        try {
            Options opt = new Options();
            input = context.getContentResolver().openInputStream(LocalStringUtils.toUriByStr(path));
            opt.inJustDecodeBounds = true;
            opt.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, opt);
            int outWidth = opt.outWidth;
            int outHeight = opt.outHeight;

            int s = 1;
            while ((outWidth / s > 240) || (outHeight / s > 240)) {
                s *= 2;
            }
            Options options = new Options();
            options.inSampleSize = s;
            options.inPreferredConfig = Config.ARGB_8888;
            input = context.getContentResolver().openInputStream(LocalStringUtils.toUriByStr(path));
            return BitmapFactory.decodeStream(input, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] getThumbFromHD(byte[] hd) {
        Bitmap bm;
        try {
            Options tempOpt = new Options();
            tempOpt.inJustDecodeBounds = true;
            bm = BitmapFactory.decodeByteArray(hd, 0, hd.length, tempOpt);
            int s = 1;
            while ((tempOpt.outHeight / s > 240)) {
                s *= 2;
            }
            Options options = new Options();
            options.inSampleSize = s;
            options.inPreferredConfig = Config.ARGB_8888;
            bm = BitmapFactory.decodeByteArray(hd, 0, hd.length, options);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 75, os);
            bm.recycle();
            bm = null;
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从Bitmap获取byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] getPortraitByteArray(Bitmap bm) {
        InputStream input = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 100, os);
            bm.recycle();
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 这个是等比例缩放: bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
     */
    public static byte[] getResizedImageData(Context context, Uri uri, int quality, int widthLimit, int heightLimit) {
        InputStream input = null;
        Options opt = decodeBitmapOptionsInfo(context, uri);
        if (opt == null) {
            return null;
        }
        int outWidth = opt.outWidth;
        int outHeight = opt.outHeight;
        int s = 1;
        while ((outWidth / s > widthLimit) || (outHeight / s > heightLimit)) {
            s *= 2;
        }

        Options options = new Options();
        options.inSampleSize = s;
        try {
            // options.inSampleSize = computeSampleSize(opt, -1, widthLimit *
            // heightLimit);

            input = context.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(input, null, options);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            b.compress(CompressFormat.JPEG, quality, os);
            b.recycle();
            b = null;
            return os.toByteArray();

        } catch (Exception e) {
            try {
                input = new FileInputStream(new File(uri.toString()));
                Bitmap b = BitmapFactory.decodeStream(input, null, options);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, quality, os);
                b.recycle();
                b = null;
                return os.toByteArray();
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;

    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),

                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Options decodeBitmapOptionsInfo(Context context, Uri uri) {
        InputStream input = null;
        Options opt = new Options();
        try {
            input = context.getContentResolver().openInputStream(uri);
            opt.inJustDecodeBounds = true;
            opt.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, opt);
            return opt;
        } catch (FileNotFoundException e) {
            String path = null;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(1);
                cursor.close();
            } else {
                path = uri.toString();
                if (path.indexOf("file:///mnt") > -1) {
                    path = path.substring("file:///mnt".length());
                } else if (path.indexOf("file://") > -1) {
                    path = path.substring("file://".length());
                }
            }

            if (path != null) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        // FinLog.e("==============", "file extis");
                    }
                    input = new FileInputStream(new File(path));
                    opt.inJustDecodeBounds = true;
                    opt.inPreferredConfig = Config.ARGB_8888;
                    BitmapFactory.decodeStream(input, null, opt);
                    return opt;
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] getScaleddImage(Context context, Uri uri, int widthLimit, int heightLimit) {
        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(input, null, getBitmapOptions());
            Bitmap c = Bitmap.createScaledBitmap(b, widthLimit, heightLimit, true);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            c.compress(CompressFormat.JPEG, 100, os);
            b.recycle();
            c.recycle();
            b = null;
            c = null;
            return os.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static File getFromMediaUri(ContentResolver resolver, Uri uri) {
        if (uri == null) return null;

        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    // Picasa image on newer devices with Honeycomb and up
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (SecurityException ignored) {
                // Nothing we can do
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param imageUri 图片Uri
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(Context context, Uri imageUri) {
        File imageFile = getFromMediaUri(context.getContentResolver(), imageUri);
        if (imageFile == null) {
            return 0;
        }

        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Logger.e(TAG, "getBitmapDegree", e);
        }
        return degree;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param imageFile 图片文件
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(File imageFile) {
        if (imageFile == null) {
            return 0;
        }

        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Logger.e(TAG, "getBitmapDegree", e);
        }
        return degree;
    }


    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree, int width, int height) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 保存图片到SDCard的默认路径下.
     *
     * @param f      - eg. hello.jpg
     * @param bitmap
     * @throws IOException
     */
    public static void saveBitmapToSDCard(File f, Bitmap bitmap) throws IOException {
        saveBitmapToSDCard(f, bitmap, CompressFormat.JPEG);
    }

    /**
     * 保存图片到SDCard的默认路径下.
     *
     * @param f       - eg. hello.jpg
     * @param mBitmap
     * @throws IOException
     */
    public static void saveBitmapToSDCard(File f, Bitmap mBitmap, CompressFormat imageType) throws IOException {
        if (f.exists()) {
            f.delete();
        } else {
            f.createNewFile();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(imageType, 100, fOut);
        try {
            if (null != fOut) {
                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageDataByUri(Context context, Uri uri) {
        InputStream input = null;
        Bitmap b = null;
        try {
            input = context.getContentResolver().openInputStream(uri);

            Options o = new Options();
            o.inJustDecodeBounds = true;                    //仅仅测量
            BitmapFactory.decodeStream(input, null, o);

            final int REQUIRED_SIZE = 640;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            if (width_tmp < REQUIRED_SIZE || height_tmp < REQUIRED_SIZE){
                scale = 1;
            }else {
                float scaleW = width_tmp / (REQUIRED_SIZE * 1.0f);
                float scaleH = height_tmp / (REQUIRED_SIZE * 1.0f);
                float destScale = Math.min(scaleW, scaleH);
                scale = (int) destScale;
            }
            input = context.getContentResolver().openInputStream(uri); //decodeStream 流指针已经错乱
            b = BitmapFactory.decodeStream(input, null, getBitmapOptions(scale));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return b;
    }

    public static Options getBitmapOptions(int scale) {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Config.RGB_565;
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        return opts;
    }

    public static Options getBitmapOptions() {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Config.RGB_565;
        return opts;
    }

    public static Options getThumbBitmapOptions() {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inSampleSize = 2;
        opts.inPreferredConfig = Config.RGB_565;
        return opts;
    }

    public static byte[] getImageDataByUri(Uri uri, Context context) {
        return getResizedImageData(context, uri, 100, 800, 800);
    }

    public static byte[] getCardBackgroundImageDataByUri(Uri uri, Context context) {
        byte[] data = getResizedImageData(context, uri, 100, 960, 960);

        Bitmap sourceBitmap = null;

        // byte[] data = getZoomImage(context, sourceBitmap, 640, 960, false, 100);
        // FinLog.d("BitmapUtil", "getCardBackgroundImageDataByUri 1 data.length = " + data.length);
        int quality = 100;
        int len = data.length;
        while (len > 1024L * 100) {
            quality = quality - 4;
            // data = getZoomImage(context, sourceBitmap, 640, 960, false, 90);
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Config.RGB_565;
            sourceBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            sourceBitmap.compress(CompressFormat.JPEG, quality, os);
            sourceBitmap.recycle();
            sourceBitmap = null;
            len = os.toByteArray().length;
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // FinLog.d("BitmapUtil", "getCardBackgroundImageDataByUri data.length = " + len);
            if (len <= 1024L * 100) {

                return data = os.toByteArray();
            }
        }

        return data;
    }

    public static byte[] getImageThumbDataByUri(Uri uri, Context context) {
        return getResizedImageData(context, uri, 100, 160, 160);
    }

    public static Bitmap imageCrop(Bitmap src, int width, int height) {
        int srcWid = src.getWidth();
        int srcHei = src.getHeight();
        int destW = Math.min(width, srcWid);
        int destH = Math.min(srcHei, height);
        return Bitmap.createBitmap(src, 0, 0, destW, destH, null, false);
    }

    public static Bitmap scaleToAdapterWh(Bitmap bm, int limitWidth, int limitHeight) {
        int srcWid = bm.getWidth();
        int srcHei = bm.getHeight();
        float scaleW = limitWidth * 1.0f / srcWid;
        float scaleH = limitHeight * 1.0f / srcHei;
        float destScale = Math.min(scaleW, scaleH);
        return Bitmap.createScaledBitmap(bm, (int) (srcWid * destScale), (int) (srcHei * destScale), false);
    }

    /**
     * 获取视频缩略图
     *
     * @param videoName
     * @param activity
     * @return
     */
    public static Bitmap loadThumbnail(String videoName, Activity activity) {

        String[] proj = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor videocursor = activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Video.Media.DISPLAY_NAME + "='" + videoName + "'", null, null);
        Bitmap curThumb = null;

        if (videocursor.getCount() > 0) {
            videocursor.moveToFirst();
            ContentResolver crThumb = activity.getContentResolver();
            Options options = new Options();
            options.inSampleSize = 1;
            curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, videocursor.getInt(0), MediaStore.Video.Thumbnails.MICRO_KIND, (Options) null);

        }

        return curThumb;
    }

    public static String getFileName(Context context, String entireFilePath) {
        File imageFile = null;
        if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(entireFilePath).getScheme())) {
            ContentResolver cr = context.getContentResolver();
            Uri imageUri = Uri.parse(entireFilePath);
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = cr.query(imageUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imageFile = new File(cursor.getString(column_index));
            cursor.close();
        } else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(entireFilePath).getScheme())) {
            imageFile = new File(Uri.parse(entireFilePath).getPath());
        } else {
            imageFile = new File(entireFilePath);
        }
        return imageFile.getName();
    }

    private static final long DEFAULT_MAX_BM_SIZE = 1000 * 250;

    public static Bitmap loadBitmap(String bmPath) {
        if (LocalStringUtils.isEmpty(bmPath)) {
            return null;
        }
        File file = new File(bmPath);
        if (!file.exists()) {
            return null;
        } else {
            Bitmap bm;
            Options opts = new Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Config.RGB_565;
            long length = file.length();
            if (length > DEFAULT_MAX_BM_SIZE) {
                long ratio = length / DEFAULT_MAX_BM_SIZE;
                long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
                opts.inSampleSize = (int) simpleSize;
                try {
                    bm = BitmapFactory.decodeFile(bmPath, opts);
                } catch (Exception e) {
                    bm = null;
                }
            } else {
                bm = BitmapFactory.decodeFile(bmPath, opts);
            }
            return bm;
        }
    }

    public static Bitmap loadBitmap(byte[] bmByte) {
        if (bmByte == null || bmByte.length == 0) {
            return null;
        }
        Bitmap bm;
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Config.RGB_565;
        long length = bmByte.length;
        if (length > DEFAULT_MAX_BM_SIZE) {
            long ratio = length / DEFAULT_MAX_BM_SIZE;
            long simpleSize = (long) Math.ceil(Math.sqrt(ratio));
            opts.inSampleSize = (int) simpleSize;
            try {
                bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
            } catch (Exception e) {
                bm = null;
            }
        } else {
            bm = BitmapFactory.decodeByteArray(bmByte, 0, bmByte.length, opts);
        }
        return bm;
    }

    public static Bitmap getImage(String srcPath, float limitW, float limitH) {
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > limitW) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / limitW);
        } else if (w < h && h > limitH) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / limitH);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    /**
     * 压缩到图片100k以内
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 10) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 压缩到图片inWhatK以内
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int inWhatK) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > inWhatK && options >= 0) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少5
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 相片按相框的比例动态缩放
     *
     * @param context
     * @param width   模板宽度
     * @param height  模板高度
     * @return
     */
    public static Bitmap scaleBitmapWithWidthOrHeight(Context context, Bitmap bmp, int width, int height) {
        if (bmp == null) {
            return null;
        }
        // 计算比例
        float scaleX = (float) width / bmp.getWidth();// 宽的比例
        float scaleY = (float) height / bmp.getHeight();// 高的比例
        //新的宽高
        int newW = 0;
        int newH = 0;
        if (scaleX > scaleY) {
            newW = (int) (bmp.getWidth() * scaleX);
            newH = (int) (bmp.getHeight() * scaleX);
        } else if (scaleX <= scaleY) {
            newW = (int) (bmp.getWidth() * scaleY);
            newH = (int) (bmp.getHeight() * scaleY);
        }
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

    /**
     * 相片按相框的比例动态缩放
     *
     * @param width 模板宽度
     * @return
     */
    public static Bitmap scaleBitmapWithWidth(Bitmap bmp, int width) {
        if (bmp == null) {
            return null;
        }
        // 计算比例
        float scaleX = (float) width / bmp.getWidth();// 宽的比例
        //新的宽高
        int newW = (int) (bmp.getWidth() * scaleX);
        int newH = (int) (bmp.getHeight() * scaleX);
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

    /**
     * g根据高度缩放
     *
     * @param height 目标高度
     * @return
     */
    public static Bitmap scaleBitmapWithHeight(Bitmap bmp, int height) {
        if (bmp == null) {
            return null;
        }
        // 计算比例
        float scaleX = (float) height / bmp.getHeight();// 宽的比例
        //新的宽高
        int newW = (int) (bmp.getWidth() * scaleX);
        int newH = (int) (bmp.getHeight() * scaleX);
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

}
