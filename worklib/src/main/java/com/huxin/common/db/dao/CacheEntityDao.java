package com.huxin.common.db.dao;

import android.content.Context;
import android.util.Log;

import com.huxin.common.db.DatabaseHelper;
import com.huxin.common.db.entity.NetWorkRsultEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;


/**
 * dao
 */
public class CacheEntityDao {


    public static final String TAG = CacheEntityDao.class.getSimpleName();

    private Dao<NetWorkRsultEntity, String> mCacheDao;

    public CacheEntityDao(Context context) {
        try {
            mCacheDao =   DatabaseHelper.getInstance(context).getDao(NetWorkRsultEntity.class);
        } catch (SQLException e) {
        }
    }

    public int delete(NetWorkRsultEntity user) {
        int cnt = 0;
        try {
            mCacheDao.delete(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public int saveItem(NetWorkRsultEntity entity) {
        int cnt = 0;
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus = mCacheDao.createOrUpdate(entity);
            cnt++;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        return cnt;
    }

    /**
     * @param url
     * @return
     */
    public NetWorkRsultEntity queryForID(String url) {
        try {
            return mCacheDao.queryForId(url);
        } catch (SQLException e) {
        }
        return null;
    }

    public List<NetWorkRsultEntity> queryForAll() {
        try {
            return mCacheDao.queryForAll();
        } catch (SQLException e) {
        }
        return null;
    }
}
