package com.huxin.common.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Map;

/**
 *db辅助类
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    public static volatile DatabaseHelper instance;

    private Context context;

    /**
     * single
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DataBaseCommon.DB_NAME, null, DataBaseCommon.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Map<Class<?>, TableInfo> mMap = DataBaseCommon.mMap;
            DataBaseSharePre mSharePre = new DataBaseSharePre(context);
            for (Map.Entry<Class<?>, TableInfo> entry : mMap.entrySet()) {
                Class<?> clazz = entry.getKey();
                TableInfo tbInfo = entry.getValue();
                //create table
                TableUtils.createTable(connectionSource, clazz);
                //save version
                String name = tbInfo.tbName;
                int ver = tbInfo.tbVer;
                mSharePre.saveTBVer(name, ver);
            }
        } catch (SQLException e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            Map<Class<?>, TableInfo> mMap = DataBaseCommon.mMap;
            DataBaseSharePre mSharePre = new DataBaseSharePre(context);
            for (Map.Entry<Class<?>, TableInfo> entry : mMap.entrySet()) {
                Class<?> clazz = entry.getKey();
                TableInfo tbInfo = entry.getValue();
                int oldVer = mSharePre.getTBVerByName(tbInfo.tbName);
                int newVer = tbInfo.tbVer;
                if (newVer != oldVer) {
                    TableUtils.dropTable(connectionSource, clazz, true);
                    TableUtils.createTable(connectionSource, clazz);
                    //save table version
                    mSharePre.saveTBVer(tbInfo.tbName, tbInfo.tbVer);
                }
            }
        } catch (SQLException e) {
        }
    }


    public void clearDb() {
        try {
            Map<Class<?>, TableInfo> mMap = DataBaseCommon.mMap;
            DataBaseSharePre mSharePre = new DataBaseSharePre(context);
            for (Map.Entry<Class<?>, TableInfo> entry : mMap.entrySet()) {
                Class<?> clazz = entry.getKey();
                TableInfo tbInfo = entry.getValue();
                TableUtils.dropTable(getConnectionSource(), clazz, true);
                TableUtils.createTable(getConnectionSource(), clazz);
                //save table version
                mSharePre.saveTBVer(tbInfo.tbName, tbInfo.tbVer);
            }
        } catch (SQLException e) {
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        Exception exception = new Exception("严重警告,数据库发生降级,请纠正该问题...");
    }

    @Override
    public void close() {
        super.close();
    }
}
