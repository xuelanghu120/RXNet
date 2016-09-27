package com.huxin.common.utils.image;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huxin.common.utils.file.FileUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * glide 加载图片工具类
 */
public class ImageWorker {
    public static final String TAG = ImageWorker.class.getSimpleName();

    public static void imageLoaderFitCenter(Context context, ImageView view, String thumnailUrl, String url) {
        DrawableRequestBuilder thumbNailRequest = Glide.with(context)
                .load(thumnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(url)
                .fitCenter()
                .thumbnail(thumbNailRequest)
                .dontAnimate()
                .into(view);
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, String thumnailUrl, String url, RequestListener listener) {
        DrawableRequestBuilder thumbNailRequest = Glide.with(context)
                .load(thumnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(url)
                .fitCenter()
                .listener(listener)
                .thumbnail(thumbNailRequest)
                .dontAnimate()
                .into(view);
    }

    /**
     * 常用于Detail页
     *
     * @param context
     * @param view
     * @param url
     * @param listener
     */
    public static void imageLoaderFitCenter(Context context, ImageView view, String url, RequestListener listener) {
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .listener(listener)
                .into(view);
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            RequestManager request = Glide.with(context);
            request.load(url)
                    .fitCenter()
                    .into(view);
        }
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, byte[] bytes) {
        if (null != bytes && 0 < bytes.length) {
            RequestManager request = Glide.with(context);
            request.load(bytes)
                    .fitCenter()
                    .into(view);
        }
    }

    public static void imageLoaderOnlyDownload(Context context, String url, RequestListener<Object, GlideDrawable> listener) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    public static void imageLoaderOnlyDownload(Context context, String url) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void imageLoader(Context context, ImageView view, String url) {
        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有size的图片和源文件
                    .into(view);
        }
    }

    public static void imageLoader(Context context, ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }


    public static void imageWrapLoader(Context context, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        }
    }

    public static void imageLoader(Context context, ImageView view, String url, String localUrl) {
        if (!TextUtils.isEmpty(url)) {
            ImageLoaderLoacalCacheEntity entity = new ImageLoaderLoacalCacheEntity(view, url, localUrl);
            getUrl(entity)
                    .subscribe(new Action1<ImageLoaderLoacalCacheEntity>() {
                        @Override
                        public void call(ImageLoaderLoacalCacheEntity entity) {
                            Glide.with(entity.imageView.getContext())
                                    .load(entity.url)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(entity.imageView);
                        }
                    });
        }
    }


    /**
     * RXjava 实体类
     */
    private static class ImageLoaderLoacalCacheEntity {
        private ImageView imageView;
        private String url;
        private String localurl;


        public ImageLoaderLoacalCacheEntity(ImageView imageView, String url, String localurl) {
            this.imageView = imageView;
            this.url = url;
            this.localurl = localurl;
        }
    }

    public static Observable<ImageLoaderLoacalCacheEntity> getUrl(ImageLoaderLoacalCacheEntity entity) {
        return Observable.just(entity)
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<ImageLoaderLoacalCacheEntity>() {
                    @Override
                    public void call(ImageLoaderLoacalCacheEntity entity) {
                        if (!TextUtils.isEmpty(entity.localurl)) {
                            boolean isExists = FileUtils.exists(entity.localurl);
                            if (isExists) {
                                entity.url = entity.localurl;
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void imageLoaderCircle(final Context context, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))//转化 加载出圆形的图片
                    .into(view);
        }
    }

    public static void imageLoader(Context context, ImageView view, int id) {
        Glide.with(context)
                .load(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(view);
    }

    public static void imageLoader(Context context, ImageView view, int placeholder, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(placeholder)//设置默认显示的图片R.drawable.ic_launch z资源文件
                    .into(view);
        }
    }

    /**
     * 获取Bitmap回调
     *
     * @param context
     * @param url
     * @param simpleTarget
     */
    public static void imageLoaderBitmap(final Context context, String url, final SimpleTarget<Bitmap> simpleTarget) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()////总是将其转换为Bitmap的对象
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(simpleTarget);
        }
    }

    /**
     * 获取圆角的drawable   进阶-相关回调
     *
     * @param context
     * @param url
     * @param simpleTarget
     * @param pixels       圆角度
     */

    public static void imageLoaderRoundCorner(final Context context, String url, final SimpleTarget<GlideDrawable> simpleTarget, final float pixels) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation(context, (int) pixels, 0))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            simpleTarget.onResourceReady(resource, glideAnimation);
                        }
                    });

        }
    }

    /**
     * 模糊图片
     * @param context
     * @param view
     * @param url
     */
    public static void imageLoaderBlur(final Context context, final ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(context, 20))
                    .into(view);

        }
    }

    public static void imageLoaderRadius(final Context context, final ImageView view, String url, int radius) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .into(view);

        }
    }

    /**
     * 获得高斯模糊stringDrawableTypeRequest
     *
     * @param context
     * @param url
     * @return
     */
    public static DrawableTypeRequest<String> buildBlurBitmapRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(context, 20, 4));
            return stringDrawableTypeRequest;
        }

        return null;
    }

    public static DrawableTypeRequest<String> buildRoundedImageRequest(final Context context, String url, int radius) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            return stringDrawableTypeRequest;
        }
        return null;
    }

    public static DrawableTypeRequest<String> buildFitCenterImageRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();
            return stringDrawableTypeRequest;
        }
        return null;
    }

    public static DrawableTypeRequest<Integer> buildFitCenterImageRequest(final Context context, int url) {
        if (0 != url) {
            DrawableTypeRequest<Integer> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();
            return stringDrawableTypeRequest;
        }
        return null;
    }


    public static DrawableTypeRequest<String> buildCenterCropImageRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop();
            return stringDrawableTypeRequest;
        }
        return null;
    }

    //demo animotin

    /**
     *
     GlideAnimation四个子类：
     ViewPropertyAnimation
     ViewAnimation
     NoAnimation
     DrawableCrossFadeViewAnimation
     */
    public static void glideWithAnimotion(Context context, ImageView iv){
        ViewPropertyAnimation.Animator animator =  new ViewPropertyAnimation.Animator() {
            @Override
            public void animate(View view) {
                view.setAlpha( 0f );
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                fadeAnim.setDuration( 2500 );
                fadeAnim.start();
            }
        };
        Glide.with(context).load("url").animate(animator).into(iv);
    }
    //demo进阶-定制各种策略
    class GlideConfiguration implements GlideModule {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            // Prefer higher quality images unless we're on a low RAM device
            MemorySizeCalculator calculator = new MemorySizeCalculator(context);
            int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
            int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

            int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
            int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

            //1、内存缓存相关
            builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
            builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

            // set size & external vs. internal
            int cacheSize100MegaBytes = 104857600;
            //2、磁盘缓存相关
            builder.setDiskCache(
//                new InternalCacheDiskCacheFactory(context, cacheSize100MegaBytes)//内部使用的磁盘缓存区
                    new ExternalCacheDiskCacheFactory(context, cacheSize100MegaBytes)//外部可以访问的磁盘缓存区

            );


            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //3、根据运行内存情况，自定义对图像的编码格式
            builder.setDecodeFormat(activityManager.isLowRamDevice() ?
                    DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888);
//             GlideBuilder内部方法：
//            builder.setMemoryCache(MemoryCache memoryCache)
//                    .setBitmapPool(BitmapPool bitmapPool)
//                    .setDiskCache(DiskCache.Factory diskCacheFactory)
//                    .setDiskCacheService(ExecutorService service)
//                    .setResizeService(ExecutorService service)
//                    .setDecodeFormat(DecodeFormat decodeFormat)
        }

        @Override
        public void registerComponents(Context context, Glide glide) {

        }
    }

}
