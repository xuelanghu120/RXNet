package com.huxin.common.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class DataBaseSharePre {
    private final String FILE_NAME = "RMS_DB";

    private Context mContext;

    public DataBaseSharePre(Context mContext) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public int getTBVerByName(String tbName){
        int ver = 0;
        SharedPreferences mSharePre = this.mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        ver = mSharePre.getInt(tbName,0);
        return ver;
    }

    public void saveTBVer(String name, int ver){
        if(ver > 0){
            SharedPreferences mSharePre = this.mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharePre.edit();
            editor.putInt(name, ver);
            editor.commit();
        }
    }

}
