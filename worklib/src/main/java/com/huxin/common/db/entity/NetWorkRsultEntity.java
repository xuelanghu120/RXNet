package com.huxin.common.db.entity;

/**
 * Created by 56417 on 2016/7/14.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * cache bean
 */
@DatabaseTable
public class NetWorkRsultEntity implements Serializable {

    @DatabaseField(id = true)
    public String url;
    @DatabaseField
    public String resultJsonStr;

    public boolean isCache = false;

    public NetWorkRsultEntity() {
    }

}
