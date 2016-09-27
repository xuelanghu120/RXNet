package com.huxin.common.utils.image.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * 一个目录的相册对象
 */
public class ImageBucket  implements Serializable {

    private static final long serialVersionUID = 1L;
    public int count = 0;
    public boolean isDefaulPhotoAlbum =false;
    public String bucketName;
    public ArrayList<ImageItem> imageList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
//    @Bindable
//    public boolean isDefaulPhotoAlbum() {
//        return isDefaulPhotoAlbum;
//    }
//
//    public void setIsDefaulPhotoAlbum(boolean isDefaulPhotoAlbum) {
//        this.isDefaulPhotoAlbum = isDefaulPhotoAlbum;
//        notifyPropertyChanged(BR.isDefaulPhotoAlbum);
//    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public ArrayList<ImageItem> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageItem> imageList) {
        this.imageList = imageList;
    }
}
