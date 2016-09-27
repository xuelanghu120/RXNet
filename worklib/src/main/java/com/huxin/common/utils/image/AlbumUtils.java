package com.huxin.common.utils.image;

import com.huxin.common.utils.image.entity.ImageBucket;
import com.huxin.common.utils.image.entity.ImageItem;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 相册的工具类
 * Created by 56417 on 2016/9/23.
 */

public class AlbumUtils {
    /**
     * 得到默认选中的相册，就是手机拍照保存的目录
     * @return
     */
    public static ImageBucket getDefaultBucket(ArrayList<ImageBucket> dataList) {
        ImageBucket bucket = null;
        if (dataList != null && dataList.size() > 0) {
            for (ImageBucket img : dataList) {
                if (img.isDefaulPhotoAlbum) {
                    bucket = img;
                    break;
                }
            }
        }
        if (bucket == null) {
            if (dataList != null && dataList.size() > 0) {
                bucket = dataList.get(0);
            }
        }
        return bucket;
    }
    /**
     * 初始化默认相册里面图片的属性都是未
     * @param defaultBucket
     */
    public static void initDefaultBucketImageList(ImageBucket defaultBucket) {
        if (defaultBucket != null) {
            ArrayList<ImageItem> imageList = defaultBucket.imageList;
            for (ImageItem item : imageList) {
                item.isSelected = false;
            }
        }
    }
    /**
     * 按字典顺序比较相册名字符串，不考虑大小写
     */
    public static Comparator<ImageBucket> comparator = new Comparator<ImageBucket>() {

        @Override
        public int compare(ImageBucket l1, ImageBucket l2) {
            int flag = l1.bucketName.compareToIgnoreCase(l2.bucketName);
            if (flag < 0) {
                return -1;
            } else if (flag > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    /**
     * //已经选择的相片设置属性已选
     * @param imageList 所有图片
     * @param selectedImages 已经选择的图片
     * @return
     */
    public static ArrayList<ImageItem> initChoosed(ArrayList<ImageItem> imageList, ArrayList<ImageItem> selectedImages) {
        if (imageList != null && imageList.size() > 0 && selectedImages.size() > 0) {
            for (ImageItem item : selectedImages) {//已经选择的图片
                if (!item.getImagePath().startsWith("#")) {
                    for (ImageItem item2 : imageList) {//所有的图片
                        if (item.getImagePath().equals(item2.getImagePath())) {
                            Logger.d("item",item.getImagePath()+"=="+item2.getImagePath());
                            item2.isSelected = true;
                        }
                    }
                }
            }
        }
        return imageList;
    }
}
