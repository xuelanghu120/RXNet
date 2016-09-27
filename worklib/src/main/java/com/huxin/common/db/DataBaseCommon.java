package com.huxin.common.db;


import com.huxin.common.db.entity.NetWorkRsultEntity;

import java.util.HashMap;
import java.util.Map;


/**
 * db
 */
public class DataBaseCommon {

    //http cache db
    public static final String HTTP_CACHE_TABLE = "httpCacheTable";

    //db name
    public static final String DB_NAME = "httpcache.db";
    //db version
    public static final int DB_VERSION = 2;

    public static final Map<Class<?>, TableInfo> mMap = new HashMap<Class<?>, TableInfo>();

    static {
        //table TimeLineDataTable version 1
        mMap.put(NetWorkRsultEntity.class, new TableInfo(HTTP_CACHE_TABLE, 2));
    }
}
