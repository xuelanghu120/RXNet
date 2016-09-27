package com.huxin.common.utils.contats;

/**
 * Created by changfeng on 2016/9/19.
 */

import com.huxin.common.entity.IEntity;

/**
 * 短信实体类
 */
public class SmsEntity implements IEntity {
    private String address,date,type,body;//type 1 是入，2是出

    public SmsEntity() {
    }

    public SmsEntity(String address, String body, String date, String type) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SmsEntity{" +
                "address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
