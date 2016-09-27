package com.huxin.common.http;

import java.io.IOException;

import okhttp3.Call;

public  interface ProgressListener {
    /**
     * @param bytesRead     已下载字节数
     * @param contentLength 总字节数
     * @param done          是否下载完成
     */
    void update(long bytesRead, long contentLength, boolean done);

    void onFailure(Call call, IOException e);
}
