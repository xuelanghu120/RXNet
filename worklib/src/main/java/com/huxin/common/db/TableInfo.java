package com.huxin.common.db;

/**
 * db bean
 */
public class TableInfo {
    //table name
    public String tbName;
    //table version
    public int tbVer;

    public TableInfo(String tbName, int tbVer) {
        // TODO Auto-generated constructor stub
        this.tbName = tbName;
        this.tbVer = tbVer;
    }

}
