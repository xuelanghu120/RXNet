package com.huxin.common.utils.image;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.util.Log;

import com.huxin.common.application.Global;

import com.huxin.common.utils.image.entity.ImageBucket;
import com.huxin.common.utils.image.entity.ImageItem;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 获取相册的helper
 */
public class AlbumHelper {
    private static final String TAG = "AlbumHelper";
    private ContentResolver cr;

    // 缩略图列表
    HashMap<String, String> thumbnailList = new HashMap<String, String>();
    HashMap<String, String> imagelList = new HashMap<String, String>();
    ArrayList<String> generatorThumbList = new ArrayList<String>();

    // 专辑列表
    List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    public HashMap<String, ImageBucket> bucketMap = new HashMap<String, ImageBucket>();

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
        AlbumHelper albumHelper = new AlbumHelper();
        albumHelper.init();
        return albumHelper;
    }

    /**
     * 初始化
     */
    public void init() {

        cr = Global.getContext().getContentResolver();
        Global.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                .parse("file://" + Environment.getExternalStorageDirectory())));

    }

    /**
     * 获取动态缩略图
     */
    public void temp() {
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, 1920,1080);
        // Images.Thumbnails.getThumbnail(cr, origId, kind, options)
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
        if (cr == null) {
            return;
        }
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor!=null && cursor.getCount()>1){
            getThumbnailColumnData(cursor);
        }
    }

    /**
     * 从数据库中得到系统自动生成的缩略图
     *
     * @param cur
     */
    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                // Do something with the values.
                // Log.i(TAG, _id + " image_id:" + image_id + " path:"
                // + image_path + "---");
                // HashMap<String, String> hash = new HashMap<String, String>();
                // hash.put("image_id", image_id + "");
                // hash.put("path", image_path);
                // thumbnailList.add(hash);
                thumbnailList.put("" + image_id, image_path);
                Logger.w(TAG, "缩略图id:" + image_id);
            } while (cur.moveToNext());
        }
    }

    /**
     * 得到原图
     */
    void getAlbum() {
        String[] projection = {Albums._ID, Albums.ALBUM, Albums.ALBUM_ART, Albums.ALBUM_KEY,
                Albums.ARTIST, Albums.NUMBER_OF_SONGS};
        Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
        getAlbumColumnData(cursor);

    }

    /**
     * 从本地数据库中得到原图
     *
     * @param cur
     */
    private void getAlbumColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            String album;
            String albumArt;
            String albumKey;
            String artist;
            int numOfSongs;

            int _idColumn = cur.getColumnIndex(Albums._ID);
            int albumColumn = cur.getColumnIndex(Albums.ALBUM);
            int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
            int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
            int artistColumn = cur.getColumnIndex(Albums.ARTIST);
            int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                album = cur.getString(albumColumn);
                albumArt = cur.getString(albumArtColumn);
                albumKey = cur.getString(albumKeyColumn);
                artist = cur.getString(artistColumn);
                numOfSongs = cur.getInt(numOfSongsColumn);

                // Do something with the values.
                Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt + "albumKey: "
                        + albumKey + " artist: " + artist + " numOfSongs: " + numOfSongs + "---");
                HashMap<String, String> hash = new HashMap<String, String>();
                hash.put("_id", _id + "");
                hash.put("album", album);
                hash.put("albumArt", albumArt);
                hash.put("albumKey", albumKey);
                hash.put("artist", artist);
                hash.put("numOfSongs", numOfSongs + "");
                albumList.add(hash);

            } while (cur.moveToNext());

        }
        cur.close();
    }

    /**
     * 是否创建了图片集
     */
    boolean hasBuildImagesBucketList = false;
    private ArrayList<ImageBucket> bucketList;

    public ArrayList<ImageBucket> getBucketList() {
        return bucketList;
    }

    private boolean once = true;

    /**
     * 得到图片集
     */
    public void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();
        // 构造缩略图索引
        getThumbnail();

        // 构造相册索引
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA,
                Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME,
                Media.DATE_MODIFIED, Media.ORIENTATION};

        String sortOrder = Media.DATE_MODIFIED + " desc";
        // 得到一个游标
        if (cr==null){
            return;
        }
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, sortOrder);
        if (null != cur &&  cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
            int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
            int photoOrientation = cur.getColumnIndexOrThrow(Media.ORIENTATION);
            // 获取图片总数
            int totalNum = cur.getCount();
            once = true;
            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);
                int orientation = cur.getInt(photoOrientation);
                File file = new File(path);
                if (!file.exists()) {// 如果图片地址不存在，执行下一次循环
                    continue;
                }

                if (orientation != 0) {
                    Logger.d(TAG, path + "翻转角度：" + orientation);
                }

                ImageBucket bucket = bucketMap.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    if (bucketMap.containsValue(bucket)) {
                        Logger.d(TAG, "包含重复图片");
                        return;
                    } else {
                        Logger.d(TAG, "不包含重复图片");
                    }
                    bucketMap.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<ImageItem>();
                    bucket.bucketName = bucketName;
                    File cameraFile = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    String sysAlbumPath = cameraFile.getAbsolutePath();
                    if (path.startsWith(sysAlbumPath)) {// 该目录是否是系统默认相册目录
                        File f = new File(path);
                        String parent = f.getParentFile().getParent();
                        if (parent.equals(sysAlbumPath) && once) {
                            once = false;
                            bucket.isDefaulPhotoAlbum = true;
                            Logger.d(TAG, "sys default photo album:" + sysAlbumPath);
                        }
                    }
                }
                imagelList.put("" + _id, path);
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);
                bucket.imageList.add(imageItem);
                imageItem.orientation = orientation;

                String string = thumbnailList.get(_id);
                if (string == null) {
                    // Bitmap thumbnail = Images.Thumbnails.getThumbnail(cr,
                    // Long.valueOf(_id), Thumbnails.MICRO_KIND, null);
                    generatorThumbList.add(path);
                }

                Logger.d(TAG, "原图id:" + _id);
            } while (cur.moveToNext());
        }
        try {
            if (null != cur) {
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Entry<String, ImageBucket>> itr = bucketMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr.next();
            ImageBucket bucket = entry.getValue();
            Logger.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", " + bucket.count
                    + " ---------- ");
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
                Logger.d(TAG, "----- " + image.imageId + ", " + image.imagePath + ", "
                        + image.thumbnailPath);
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
        Logger.d(TAG, "use time: " + (endTime - startTime) + " ms");
    }

    /**
     * 得到图片集
     *
     * @param refresh
     * @return
     */
    public ArrayList<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || (!refresh && !hasBuildImagesBucketList)) {
            bucketMap.clear();
            buildImagesBucketList();
        }
        bucketList = new ArrayList<ImageBucket>();
        Iterator<Entry<String, ImageBucket>> itr = bucketMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr.next();
            bucketList.add(entry.getValue());
        }

        Logger.d(TAG, "缩略图数量:" + thumbnailList.size());
        Logger.d(TAG, "原始图片数量:" + imagelList.size());

        new AsyncInsertGalleryTask().execute();

        return bucketList;
    }

    private class AsyncInsertGalleryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (generatorThumbList.size() > 0) {
                for (String path : generatorThumbList) {
                    if (!TextUtils.isEmpty(path)) {
                        // Intent intent = new
                        // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        // Uri uri = Uri.fromFile(new File(path));
                        // intent.setData(uri);
                        // AlbumHelper.context.sendBroadcast(intent);

                        // new SingleMediaScanner(context, new File(path));

                        Logger.w(TAG, " 没有找到相关缩略图,现在插入到系统相册中,insertImage:");

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            generatorThumbList.clear();
        }

    }

    /**
     * 得到原始图像路径
     *
     * @param image_id
     * @return
     */
    String getOriginalImagePath(String image_id) {
        String path = null;
        Logger.i(TAG, "---(^o^)----" + image_id);
        String[] projection = {Media._ID, Media.DATA};
        Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection, Media._ID + "="
                + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));
            cursor.close();
        }
        return path;
    }

}
