package com.huxin.common.utils.contats;

import com.huxin.common.entity.IEntity;

/**
 * Created by Administrator on 2016/9/19.
 */
public class CallRecordEntity implements IEntity {
    private String name,phoneNumber,type,date,duration;

    public CallRecordEntity() {
    }

    public CallRecordEntity(String date, String duration, String name, String phoneNumber, String type) {
        this.date = date;
        this.duration = duration;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "CallRecordEntity{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
