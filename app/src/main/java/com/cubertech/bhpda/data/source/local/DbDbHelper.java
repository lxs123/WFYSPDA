package com.cubertech.bhpda.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "DbCon.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_DBDATA =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MZ + TEXT_TYPE + " PRIMARY KEY," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_RQ + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_SL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YSL + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_LS =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_LS + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_ID + TEXT_TYPE + " PRIMARY KEY," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_KHBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_SL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_DB + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_DH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_TZXH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_MT + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_ZSZF =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_ZSZF + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZSZFDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DJLB + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZDY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YDSL + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_CGTZD =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_CGTZD + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGDHSCAN + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGGYS + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZDY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_RESULT + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_HGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BHGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BHGYY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_SHTZD =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_SHTZD + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGDHSCAN + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGGYS + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZDY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_RESULT + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_HGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BHGSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BHGYY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_KJDBD =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_KJDBD + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KJDBD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZDY + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BCSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BCKW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BCCW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BCPH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BRSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BRKW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BRCW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_BRPH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DBSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_LLD =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_LLD + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_SQSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GDDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ZTM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PPSL + TEXT_TYPE +

                    " )";
    //    private final String PH;   //[0]品号COLUMN_NAME_PPSL
//    private final String PM;   //[1]品名
//    private final String GG;   //[2]规格
//    private final String CK;   //[3]仓库
//    private final String KW;   //[4]库位
//    private final String PC;   //[5]批次
//    private final String DW;   //[6]单位
//    private final String KCSL; //[7]库存数量
//    private final String TIME; //[8]最后更新时间
//    private final String ID;   // 9标识id 领料单号+ 料号+position  (lld+lh+position)
//    private final String STATE;//10选择状态
    private static final String SQL_CREATE_LLD_ITEM =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_LLD_ITEM + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDLS_ID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KCSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_FLSL + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_CHTZD =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_CHTZD + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_QYBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YYJD + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LJBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_MC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_SQSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PPSL + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_CHTZD_ITEM =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_CHTZD_ITEM + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDLS_ID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KCSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_FLSL + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_CGTZD_ITEM =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_CGTZD_ITEM + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDLS_ID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KCSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_FLSL + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_ZSZF_ITEM =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_ZSZF_ITEM + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDLS_ID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PC + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_DW + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_KCSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_FLSL + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_DDXH_ITEM =
            "CREATE TABLE " + DbPersistenceContract.DbEntry.TABLE_NAME_DDXH_ITEM + " (" +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_ID + BOOLEAN_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LLDLS_ID + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_LS_TZXH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PM + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_GG + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_CK + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_YJCHL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_PPSL + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XMBH + TEXT_TYPE + COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_XMMC + TEXT_TYPE +COMMA_SEP +
                    DbPersistenceContract.DbEntry.COLUMN_NAME_IF + TEXT_TYPE +
                    " )";

    public DbDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DBDATA);
        db.execSQL(SQL_CREATE_LS);
        db.execSQL(SQL_CREATE_ZSZF);
        db.execSQL(SQL_CREATE_CGTZD);
        db.execSQL(SQL_CREATE_KJDBD);
        db.execSQL(SQL_CREATE_LLD);
        db.execSQL(SQL_CREATE_LLD_ITEM);
        db.execSQL(SQL_CREATE_CHTZD);
        db.execSQL(SQL_CREATE_CHTZD_ITEM);
        db.execSQL(SQL_CREATE_SHTZD);
        db.execSQL(SQL_CREATE_CGTZD_ITEM);
        db.execSQL(SQL_CREATE_ZSZF_ITEM);
        db.execSQL(SQL_CREATE_DDXH_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_CREATE_LS);
    }
}
