package com.huxin.common.http.upload.impl.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.huxin.common.http.upload.impl.PostProgressListener;
import com.huxin.common.http.upload.impl.model.ProgressModel;

import java.lang.ref.WeakReference;


/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-10-02
 * Time: 15:25
 */
public abstract class ProgressHandler extends Handler {
    public static final int UPDATE = 0x01;
    public static final int START = 0x02;
    public static final int FINISH = 0x03;
    //弱引用
    private final WeakReference<PostProgressListener> mUIProgressAbstractWeakReference;

    public ProgressHandler(PostProgressListener PostProgressListener) {
        super(Looper.getMainLooper());
        mUIProgressAbstractWeakReference = new WeakReference<PostProgressListener>(PostProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UPDATE: {
                PostProgressListener uiProgessListener = mUIProgressAbstractWeakReference.get();
                if (uiProgessListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    progress(uiProgessListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                }
                break;
            }
            case START: {
                PostProgressListener PostProgressListener = mUIProgressAbstractWeakReference.get();
                if (PostProgressListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    start(PostProgressListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());

                }
                break;
            }
            case FINISH: {
                PostProgressListener PostProgressListener = mUIProgressAbstractWeakReference.get();
                if (PostProgressListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    finish(PostProgressListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                }
                break;
            }
            default:
                super.handleMessage(msg);
                break;
        }
    }

    public abstract void start(PostProgressListener PostProgressListener, long currentBytes, long contentLength, boolean done);
    public abstract void progress(PostProgressListener PostProgressListener, long currentBytes, long contentLength, boolean done);
    public abstract void finish(PostProgressListener PostProgressListener, long currentBytes, long contentLength, boolean done);
}
