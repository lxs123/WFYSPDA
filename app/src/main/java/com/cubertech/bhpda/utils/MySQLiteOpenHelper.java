package com.cubertech.bhpda.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Author LXS
 * @Data 2017/10/20 9:20
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySQLiteOpenHelper(Context applicationContext) {
        // TODO Auto-generated constructor stub
        this(applicationContext, "savePrintLog.db", null, 1);
    }

    /**
     * flag : 标识(容器码,托盘唛头码等等) tm : 生成的dm容器码  result :附带传送的结果 cptm:产品条码
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table dmcode(id integer not null,flag text not null,result text not null,tm text not null,cptm text not null);";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteTable(SQLiteDatabase sqLiteDatabase) {
//      删除表后，记住新建
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS dmcode");
        onCreate(sqLiteDatabase);
    }
}
