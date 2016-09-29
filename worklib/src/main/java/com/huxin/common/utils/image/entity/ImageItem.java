package com.huxin.common.utils.image.entity;


import android.os.Parcel;
import android.os.Parcelable;


public class ImageItem  implements Parcelable {



    private int photoId;
    private String photoPath;
    //private String thumbPath;
    private int width;
    private int height;

    public String id;
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public String albumName;
    public Boolean isSelected = false;
    public String uploadThumbnailPath;
    public String tag;
    public int orientation;
    public boolean isCorver;

    public ImageItem(ImageItem item) {
        if (item != null) {
            this.setImageId(item.getImageId());
            this.setThumbnailPath(item.getThumbnailPath());
            this.setImagePath(item.getImagePath());
            this.setAlbumName(item.getAlbumName());
            this.setIsSelected(item.getIsSelected());
            this.setUploadThumbnailPath(item.getUploadThumbnailPath());
            this.setTag(item.getTag());
            this.setCorver(item.isCorver);
        }
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getUploadThumbnailPath() {
        return uploadThumbnailPath;
    }

    public void setUploadThumbnailPath(String uploadThumbnailPath) {
        this.uploadThumbnailPath = uploadThumbnailPath;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isCorver() {
        return isCorver;
    }

    public void setCorver(boolean isCorver) {
        this.isCorver = isCorver;
    }

    public ImageItem() {

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageItem other = (ImageItem) obj;
        if (imageId == null) {
            if (other.imageId != null)
                return false;
        } else if (!imageId.equals(other.imageId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ImageItem [imageId=" + imageId + ", thumbnailPath="
                + thumbnailPath + ", imagePath=" + imagePath + ", albumName="
                + albumName + ", isSelected=" + isSelected
                + ", uploadThumbnailPath=" + uploadThumbnailPath + ", tag="
                + tag + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.imageId);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.imagePath);
        dest.writeString(this.albumName);
        dest.writeValue(this.isSelected);
        dest.writeString(this.uploadThumbnailPath);
        dest.writeString(this.tag);
        dest.writeInt(this.orientation);
        dest.writeByte(this.isCorver ? (byte) 1 : (byte) 0);
    }

    protected ImageItem(Parcel in) {
        this.id = in.readString();
        this.imageId = in.readString();
        this.thumbnailPath = in.readString();
        this.imagePath = in.readString();
        this.albumName = in.readString();
        this.isSelected = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.uploadThumbnailPath = in.readString();
        this.tag = in.readString();
        this.orientation = in.readInt();
        this.isCorver = in.readByte() != 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
