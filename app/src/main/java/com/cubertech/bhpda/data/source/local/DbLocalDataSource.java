package com.cubertech.bhpda.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cubertech.bhpda.data.DbCgtzd;
import com.cubertech.bhpda.data.DbChtzd;
import com.cubertech.bhpda.data.DbData;
import com.cubertech.bhpda.data.DbDdxhItem;
import com.cubertech.bhpda.data.DbKjdb;
import com.cubertech.bhpda.data.DbListresult;
import com.cubertech.bhpda.data.DbPick;
import com.cubertech.bhpda.data.DbPickItem;
import com.cubertech.bhpda.data.DbZszf;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.local.DbPersistenceContract.DbEntry;
import com.google.zxing.oned.ITFReader;

import java.util.ArrayList;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class DbLocalDataSource implements DbDataSource {

    private static DbLocalDataSource INSTANCE;

    private DbDbHelper mDbDbHelper;

    private DbLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbDbHelper = new DbDbHelper(context);
    }

    public static DbLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DbLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void batchSaveDbData(@NonNull List<DbData> dbDataList) {
        Log.i("wang", "000000");
        checkNotNull(dbDataList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME + "("
                    + DbEntry.COLUMN_NAME_MZ + ","//箱唛头或托盘唛头码值
                    + DbEntry.COLUMN_NAME_RQ + ","//日期
                    + DbEntry.COLUMN_NAME_PH + ","//品号
                    + DbEntry.COLUMN_NAME_SL + ","//数量
                    + DbEntry.COLUMN_NAME_CK + ","//仓库
                    + DbEntry.COLUMN_NAME_KW + ","//库位
                    + DbEntry.COLUMN_NAME_YSL //原数量
                    + ") " + "values(?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbData dbdata : dbDataList) {
                stat.bindString(1, dbdata.getMZ());//箱唛头或托唛头码值
                stat.bindString(2, dbdata.getRQ());//日期
                stat.bindString(3, dbdata.getPH());//品号
                stat.bindString(4, dbdata.getSL());//数量
                stat.bindString(5, dbdata.getCK());//仓库
                stat.bindString(6, dbdata.getKW());//库位
                stat.bindString(7, dbdata.getYSL());//原数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            Log.i("wang", "111111");
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            Log.i("wang", "33333" + e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                    Log.i("wang", "444444");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("wang", "555555");
            }
        }
        Log.i("wang", "6666666");
        return;
    }

    @Override
    public void getDbDatasASC(@NonNull String PHs, @NonNull LoadDbCallback callback) {
        List<DbData> dbDatalist = new ArrayList<DbData>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_MZ,//码值
                DbEntry.COLUMN_NAME_RQ,//日期
                DbEntry.COLUMN_NAME_PH,//品号
                DbEntry.COLUMN_NAME_SL,//数量
                DbEntry.COLUMN_NAME_CK,//仓库
                DbEntry.COLUMN_NAME_KW,//库位
                DbEntry.COLUMN_NAME_YSL,//原数量
        };

        String selection = DbEntry.COLUMN_NAME_PH + " LIKE ?";
        String[] selectionArgs = {PHs};

        Cursor c = db.query(
                DbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_RQ + " ASC", null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String MZ = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MZ));//码值
                String RQ = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_RQ));//日期
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));//品号
                String SL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_SL));//数量
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));//仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));//库位
                String YSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YSL));//原数量
                DbData dbData = new DbData(MZ, RQ, PH, SL, CK, KW, YSL);
                dbDatalist.add(dbData);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbsLoaded(dbDatalist);
        }
    }

    @Override
    public void getDbData(@NonNull String MZ, @NonNull GetDbCallback callback) {
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_MZ,//MZ
                DbEntry.COLUMN_NAME_RQ,//RQ
                DbEntry.COLUMN_NAME_PH,//PH
                DbEntry.COLUMN_NAME_SL,//SL
                DbEntry.COLUMN_NAME_CK,//仓库
                DbEntry.COLUMN_NAME_KW,//库位
                DbEntry.COLUMN_NAME_YSL,//原数量
        };

        String selection = DbEntry.COLUMN_NAME_MZ + " LIKE ?";
        String[] selectionArgs = {MZ};

        Cursor c = db.query(
                DbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        DbData dbData = null;
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String MZs = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MZ));//码值
                String RQ = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_RQ));//日期
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));//品号
                String SL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_SL));//数量
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));//仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));//库位
                String YSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YSL));//原数量
                dbData = new DbData(MZs, RQ, PH, SL, CK, KW, YSL);
            }
        }
        if (c != null) {
            c.close();
        }
        Log.i("wang", "2222");
        db.close();
        if (dbData != null) {
            Log.i("wang", "333");
            callback.onDbLoaded(dbData);
        } else {
            Log.i("wang", "444");
            callback.onDataNotAvailable();
        }

    }

    @Override
    public void updateDbData(@NonNull DbData dbData) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbEntry.COLUMN_NAME_SL, dbData.getSL().trim());

        String selection = DbEntry.COLUMN_NAME_MZ + " LIKE ?";
        String[] selectionArgs = {dbData.getMZ().trim()};

        db.update(DbEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void updateMZDbData(@NonNull String MZ, @NonNull String SYL) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbEntry.COLUMN_NAME_SL, SYL);

        String selection = DbEntry.COLUMN_NAME_MZ + " LIKE ?";
        String[] selectionArgs = {MZ};

        db.update(DbEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void deleteAllDbData() {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();

        db.delete(DbEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public boolean ExistsPH(@NonNull String PH) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_PH + " LIKE ?";
        String[] selectionArgs = {PH};
        Cursor cursor = db.query(DbEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean result = cursor.moveToNext();
        db.close();
        return result;
    }

    @Override
    public void batchSaveDbListresult(@NonNull List<DbListresult> dbListresult) {
        checkNotNull(dbListresult);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_LS + "("
                    + DbEntry.COLUMN_NAME_LS_ID + ","//表id
                    + DbEntry.COLUMN_NAME_LS_KHBH + ","//客户编号
                    + DbEntry.COLUMN_NAME_LS_PH + ","//品号
                    + DbEntry.COLUMN_NAME_LS_SL + ","//数量
                    + DbEntry.COLUMN_NAME_LS_CK + ","//仓库
                    + DbEntry.COLUMN_NAME_LS_DB + ","//出货通知单别
                    + DbEntry.COLUMN_NAME_LS_DH + ","//出货通知单号
                    + DbEntry.COLUMN_NAME_LS_TZXH + ","//出货通知序号
                    + DbEntry.COLUMN_NAME_LS_KW + ","//库位
                    + DbEntry.COLUMN_NAME_LS_MT //箱唛头码或托唛头码
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbListresult dbdata : dbListresult) {
                stat.bindString(1, dbdata.getID());//表id
                stat.bindString(2, dbdata.getKHBH());//客户编号
                stat.bindString(3, dbdata.getPH());//品号
                stat.bindString(4, dbdata.getSL());//数量
                stat.bindString(5, dbdata.getCK());//仓库
                stat.bindString(6, dbdata.getDB());//出货通知单别
                stat.bindString(7, dbdata.getDH());//出货通知单号
                stat.bindString(8, dbdata.getTZXH());//出货通知序号
                stat.bindString(9, dbdata.getKW());//库位
                stat.bindString(10, dbdata.getMT());//箱唛头码或托唛头码
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbListresult(@NonNull String DB, @NonNull String DH, @NonNull GetDbLCallback callback) {
        List<DbListresult> dbDatalist = new ArrayList<DbListresult>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LS_ID,//表id
                DbEntry.COLUMN_NAME_LS_KHBH,//客户编号
                DbEntry.COLUMN_NAME_LS_PH,//品号
                DbEntry.COLUMN_NAME_LS_SL,//数量
                DbEntry.COLUMN_NAME_LS_CK,//仓库
                DbEntry.COLUMN_NAME_LS_DB,//出货通知单别
                DbEntry.COLUMN_NAME_LS_DH,//出货通知单号
                DbEntry.COLUMN_NAME_LS_TZXH,//出货通知序号
                DbEntry.COLUMN_NAME_LS_KW,//库位
                DbEntry.COLUMN_NAME_LS_MT, //箱唛头码或托唛头码
        };

        //通过出货通知单单别和出售通知单单号获取数据
        String selection = DbEntry.COLUMN_NAME_LS_DB + " LIKE ? AND " + DbEntry.COLUMN_NAME_LS_DH + " LIKE ?";
        String[] selectionArgs = {DB, DH};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LS_TZXH + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_ID));
                String KHBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_KHBH));
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_PH));
                String SL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_SL));
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_CK));
                String DBs = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_DB));
                String DHs = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_DH));
                String TZXH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_TZXH));
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_KW));
                String MT = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_MT));

                DbListresult dbData = new DbListresult(ID, KHBH, PH, SL, CK, DBs, DHs, TZXH, KW, MT);

                dbDatalist.add(dbData);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteAllDbListresult() {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();

        db.delete(DbEntry.TABLE_NAME_LS, null, null);

        db.close();
    }

    @Override
    public void deletXHDbListresult(@NonNull String DB, @NonNull String DH) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LS_DB + " = ? AND " + DbEntry.COLUMN_NAME_LS_DH + "= ?";
        String[] selectionArgs = {DB, DH};
        db.delete(DbEntry.TABLE_NAME_LS, selection, selectionArgs);
        db.close();
    }

    @Override
    public void deletTKDbListresult(@NonNull String MT) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LS_MT + " = ?";
        String[] selectionArgs = {MT};
        db.delete(DbEntry.TABLE_NAME_LS, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbZszf(@NonNull List<DbZszf> dbZszfList) {
        checkNotNull(dbZszfList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_ZSZF + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_ZSZFDH + ","//杂收杂发单号
                    + DbEntry.COLUMN_NAME_QYBH + ","//企业编号
                    + DbEntry.COLUMN_NAME_YYJD + ","//营业据点
                    + DbEntry.COLUMN_NAME_DJBH + ","//单据编号
                    + DbEntry.COLUMN_NAME_DJLB + ","//单据类别
                    + DbEntry.COLUMN_NAME_ZDY + ","//自定义
                    + DbEntry.COLUMN_NAME_XC + ","//项次
                    + DbEntry.COLUMN_NAME_LJBH + ","//料件编号
                    + DbEntry.COLUMN_NAME_KW + ","//库位
                    + DbEntry.COLUMN_NAME_CW + ","//储位位
                    + DbEntry.COLUMN_NAME_PH + ","//储位位
                    + DbEntry.COLUMN_NAME_MC + ","//名称
                    + DbEntry.COLUMN_NAME_GG + ","//规格
                    + DbEntry.COLUMN_NAME_DW + ","//单位
                    + DbEntry.COLUMN_NAME_YDSL //异动数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbZszf dbdata : dbZszfList) {
                stat.bindString(1, dbdata.getZSZFDH());//杂收杂发单
                stat.bindString(2, dbdata.getQYBH());//企业编号
                stat.bindString(3, dbdata.getYYJD());//营业据点
                stat.bindString(4, dbdata.getDJBH());//单据编号
                stat.bindString(5, dbdata.getDJLB());//单据类别
                stat.bindString(6, dbdata.getZDY());//自定义
                stat.bindString(7, dbdata.getXC());//项次
                stat.bindString(8, dbdata.getLJBH());//料件编号
                stat.bindString(9, dbdata.getKW());//库位
                stat.bindString(10, dbdata.getCW());//储位
                stat.bindString(11, dbdata.getPH());//异动数量
                stat.bindString(12, dbdata.getMC());//名称
                stat.bindString(13, dbdata.getGG());//规格
                stat.bindString(14, dbdata.getDW());//单位
                stat.bindString(15, dbdata.getYDSL());//异动数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbZszfList(@Nullable String zszfdh, GetDbZszfCallback callback) {
        List<DbZszf> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_ZSZFDH,//杂收杂发单号
                DbEntry.COLUMN_NAME_QYBH, //企业编号
                DbEntry.COLUMN_NAME_YYJD,//营业据点
                DbEntry.COLUMN_NAME_DJBH,//单据编号
                DbEntry.COLUMN_NAME_DJLB,//单据类别
                DbEntry.COLUMN_NAME_ZDY,//自定义
                DbEntry.COLUMN_NAME_XC,//项次
                DbEntry.COLUMN_NAME_LJBH,//料件编号
                DbEntry.COLUMN_NAME_KW,//库位
                DbEntry.COLUMN_NAME_CW,//储位位
                DbEntry.COLUMN_NAME_PH,//批号
                DbEntry.COLUMN_NAME_MC,
                DbEntry.COLUMN_NAME_GG,
                DbEntry.COLUMN_NAME_DW,
                DbEntry.COLUMN_NAME_YDSL //异动数量
        };

        //通过杂收杂发单号获取数据
        String selection = DbEntry.COLUMN_NAME_ZSZFDH + " LIKE ?";
        String[] selectionArgs = {zszfdh};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_ZSZF, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_ZSZFDH + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ZSZFDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ZSZFDH));
                String QYBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_QYBH));
                String YYJD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YYJD));
                String DJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DJBH));
                String DJLB = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DJLB));
                String ZDY = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ZDY));
                String XC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XC));
                String LJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LJBH));
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));
                String CW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CW));
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));
                String MC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MC));
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));
                String YDSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YDSL));

                DbZszf dbZszf = new DbZszf(ZSZFDH, QYBH, YYJD, DJBH, DJLB, ZDY, XC, LJBH, KW, CW, PH, YDSL, MC, GG, DW);

                dbDatalist.add(dbZszf);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbZszfListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbZszf(@NonNull String zszfdh) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_ZSZFDH + " = ?";
        String[] selectionArgs = {zszfdh};
        db.delete(DbEntry.TABLE_NAME_ZSZF, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbCgtzd(@NonNull List<DbCgtzd> dbZszfList) {
        checkNotNull(dbZszfList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_CGTZD + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_CGDHSCAN + ","//采购单号扫描/出货单号扫描
                    + DbEntry.COLUMN_NAME_QYBH + ","//企业编号
                    + DbEntry.COLUMN_NAME_YYJD + ","//营业据点
                    + DbEntry.COLUMN_NAME_CGDH + ","//采购单号/出货单号
                    + DbEntry.COLUMN_NAME_CGGYS + ","//采购供应商
                    + DbEntry.COLUMN_NAME_XC + ","//项次
                    + DbEntry.COLUMN_NAME_ZDY + ","//自定义
                    + DbEntry.COLUMN_NAME_LJBH + ","//料件编号
                    + DbEntry.COLUMN_NAME_KW + ","//库位
                    + DbEntry.COLUMN_NAME_RESULT + ","//结果
                    + DbEntry.COLUMN_NAME_MC + ","//名称
                    + DbEntry.COLUMN_NAME_GG + ","//规格
                    + DbEntry.COLUMN_NAME_DW + ","//单位
                    + DbEntry.COLUMN_NAME_CGSL + ","//采购数量/出货数量
                    + DbEntry.COLUMN_NAME_HGSL + ","//合格数量
                    + DbEntry.COLUMN_NAME_BHGSL + ","//不合格数量
                    + DbEntry.COLUMN_NAME_BHGYY + "," //不合格原因
                    + DbEntry.COLUMN_NAME_STATE
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbCgtzd dbdata : dbZszfList) {
                stat.bindString(1, dbdata.getCgdhScan());//采购单号扫描/出货单号扫描
                stat.bindString(2, dbdata.getQYBH());    //企业编号
                stat.bindString(3, dbdata.getYYJD());    //营业据点
                stat.bindString(4, dbdata.getCgdh());    //采购单号
                stat.bindString(5, dbdata.getCggys());   //采购运营商
                stat.bindString(6, dbdata.getXC());      //项次
                stat.bindString(7, dbdata.getZDY());     //自定义
                stat.bindString(8, dbdata.getLJBH());    //料件编号
                stat.bindString(9, dbdata.getKW());      //库位
                stat.bindString(10, dbdata.getResult()); //结果
                stat.bindString(11, dbdata.getMC());     //名称
                stat.bindString(12, dbdata.getGG());     //规格
                stat.bindString(13, dbdata.getDW());     //单位
                stat.bindString(14, dbdata.getCgsl());   //采购数量
                stat.bindString(15, dbdata.getHGSL());     //合格数量
                stat.bindString(16, dbdata.getBHGSL());     //不合格数量
                stat.bindString(17, dbdata.getBHGYY());   //不合格原因
                stat.bindString(18, dbdata.getSTATE());
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbCgtzdList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {
        List<DbCgtzd> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_CGDHSCAN,//采购单号扫描/出货单号扫描
                DbEntry.COLUMN_NAME_QYBH,    //企业编号
                DbEntry.COLUMN_NAME_YYJD,    //营业据点
                DbEntry.COLUMN_NAME_CGDH,    //采购单号
                DbEntry.COLUMN_NAME_CGGYS,    //采购运营商
                DbEntry.COLUMN_NAME_XC,      //项次
                DbEntry.COLUMN_NAME_ZDY,     //自定义
                DbEntry.COLUMN_NAME_LJBH,    //料件编号
                DbEntry.COLUMN_NAME_KW,     ///库位
                DbEntry.COLUMN_NAME_RESULT,     ///结果
                DbEntry.COLUMN_NAME_MC,       //名称
                DbEntry.COLUMN_NAME_GG,      //规格
                DbEntry.COLUMN_NAME_DW,      //单位
                DbEntry.COLUMN_NAME_CGSL,     //采购数量
                DbEntry.COLUMN_NAME_HGSL,      //合格数量
                DbEntry.COLUMN_NAME_BHGSL,      //不合格数量
                DbEntry.COLUMN_NAME_BHGYY,     //不合格原因
                DbEntry.COLUMN_NAME_STATE
        };

        //通过杂收杂发单号获取数据
        String selection = DbEntry.COLUMN_NAME_CGDHSCAN + " LIKE ?";
        String[] selectionArgs = {zszfdh};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_CGTZD, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_CGDHSCAN + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String CGDHSCAN = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGDHSCAN));//采购单号扫描/出货单号扫描
                String QYBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_QYBH));//企业编号
                String YYJD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YYJD));//营业据点
                String CGDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGDH));//采购单号
                String CGGYS = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGGYS)); //采购运营商
                String XC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XC));//项次
                String ZDY = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ZDY));//自定义
                String LJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LJBH));//料件编号
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));//库位
                String RESULT = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_RESULT));   ///结果
                String MC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MC)); //名称
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));//规格
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));//单位
                String CGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGSL));//采购数量
                String HGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_HGSL));//规格
                String BHGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BHGSL));//单位
                String BHGYY = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BHGYY));//采购数量
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));//采购数量
                DbCgtzd dbZszf = new DbCgtzd(CGDHSCAN, QYBH, YYJD, CGDH, CGGYS, ZDY, XC, LJBH, KW, RESULT,
                        CGSL, MC, GG, DW, HGSL, BHGSL, BHGYY, STATE);
                dbDatalist.add(dbZszf);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbCgtzdListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbCgtzd(@NonNull String zszfdh) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_CGDHSCAN + " = ?";
        String[] selectionArgs = {zszfdh};
        db.delete(DbEntry.TABLE_NAME_CGTZD, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbDhys(@NonNull List<DbCgtzd> dbZszfList) {
        checkNotNull(dbZszfList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_SHTZD + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_CGDHSCAN + ","//采购单号扫描/出货单号扫描
                    + DbEntry.COLUMN_NAME_QYBH + ","//企业编号
                    + DbEntry.COLUMN_NAME_YYJD + ","//营业据点
                    + DbEntry.COLUMN_NAME_CGDH + ","//采购单号/出货单号
                    + DbEntry.COLUMN_NAME_CGGYS + ","//采购供应商
                    + DbEntry.COLUMN_NAME_XC + ","//项次
                    + DbEntry.COLUMN_NAME_ZDY + ","//自定义
                    + DbEntry.COLUMN_NAME_LJBH + ","//料件编号
                    + DbEntry.COLUMN_NAME_KW + ","//库位
                    + DbEntry.COLUMN_NAME_RESULT + ","//结果
                    + DbEntry.COLUMN_NAME_MC + ","//名称
                    + DbEntry.COLUMN_NAME_GG + ","//规格
                    + DbEntry.COLUMN_NAME_DW + ","//单位
                    + DbEntry.COLUMN_NAME_CGSL + ","//采购数量/出货数量
                    + DbEntry.COLUMN_NAME_HGSL + ","//合格数量
                    + DbEntry.COLUMN_NAME_BHGSL + ","//不合格数量
                    + DbEntry.COLUMN_NAME_BHGYY + "," //不合格原因
                    + DbEntry.COLUMN_NAME_STATE
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbCgtzd dbdata : dbZszfList) {
                stat.bindString(1, dbdata.getCgdhScan());//采购单号扫描/出货单号扫描
                stat.bindString(2, dbdata.getQYBH());    //企业编号
                stat.bindString(3, dbdata.getYYJD());    //营业据点
                stat.bindString(4, dbdata.getCgdh());    //采购单号
                stat.bindString(5, dbdata.getCggys());   //采购运营商
                stat.bindString(6, dbdata.getXC());      //项次
                stat.bindString(7, dbdata.getZDY());     //自定义
                stat.bindString(8, dbdata.getLJBH());    //料件编号
                stat.bindString(9, dbdata.getKW());      //库位
                stat.bindString(10, dbdata.getResult()); //结果
                stat.bindString(11, dbdata.getMC());     //名称
                stat.bindString(12, dbdata.getGG());     //规格
                stat.bindString(13, dbdata.getDW());     //单位
                stat.bindString(14, dbdata.getCgsl());   //采购数量
                stat.bindString(15, dbdata.getHGSL());     //合格数量
                stat.bindString(16, dbdata.getBHGSL());     //不合格数量
                stat.bindString(17, dbdata.getBHGYY());   //不合格原因
                stat.bindString(18, dbdata.getSTATE());
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbDhysList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {
        List<DbCgtzd> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_CGDHSCAN,//采购单号扫描/出货单号扫描
                DbEntry.COLUMN_NAME_QYBH,    //企业编号
                DbEntry.COLUMN_NAME_YYJD,    //营业据点
                DbEntry.COLUMN_NAME_CGDH,    //采购单号
                DbEntry.COLUMN_NAME_CGGYS,    //采购运营商
                DbEntry.COLUMN_NAME_XC,      //项次
                DbEntry.COLUMN_NAME_ZDY,     //自定义
                DbEntry.COLUMN_NAME_LJBH,    //料件编号
                DbEntry.COLUMN_NAME_KW,     ///库位
                DbEntry.COLUMN_NAME_RESULT,     ///结果
                DbEntry.COLUMN_NAME_MC,       //名称
                DbEntry.COLUMN_NAME_GG,      //规格
                DbEntry.COLUMN_NAME_DW,      //单位
                DbEntry.COLUMN_NAME_CGSL,     //采购数量
                DbEntry.COLUMN_NAME_HGSL,      //合格数量
                DbEntry.COLUMN_NAME_BHGSL,      //不合格数量
                DbEntry.COLUMN_NAME_BHGYY,     //不合格原因
                DbEntry.COLUMN_NAME_STATE
        };

        //通过杂收杂发单号获取数据
        String selection = DbEntry.COLUMN_NAME_CGDHSCAN + " LIKE ?";
        String[] selectionArgs = {zszfdh};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_SHTZD, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_CGDHSCAN + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String CGDHSCAN = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGDHSCAN));//采购单号扫描/出货单号扫描
                String QYBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_QYBH));//企业编号
                String YYJD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YYJD));//营业据点
                String CGDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGDH));//采购单号
                String CGGYS = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGGYS)); //采购运营商
                String XC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XC));//项次
                String ZDY = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ZDY));//自定义
                String LJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LJBH));//料件编号
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));//库位
                String RESULT = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_RESULT));   ///结果
                String MC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MC)); //名称
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));//规格
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));//单位
                String CGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CGSL));//采购数量
                String HGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_HGSL));//规格
                String BHGSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BHGSL));//单位
                String BHGYY = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BHGYY));//采购数量
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));//采购数量
                DbCgtzd dbZszf = new DbCgtzd(CGDHSCAN, QYBH, YYJD, CGDH, CGGYS, ZDY, XC, LJBH, KW, RESULT,
                        CGSL, MC, GG, DW, HGSL, BHGSL, BHGYY, STATE);
                dbDatalist.add(dbZszf);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbCgtzdListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbDhys(@NonNull String zszfdh) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_CGDHSCAN + " = ?";
        String[] selectionArgs = {zszfdh};
        db.delete(DbEntry.TABLE_NAME_SHTZD, selection, selectionArgs);
        db.close();
    }

    /***
     *    private final String DB;//单别
     private final String DH;//单号    3
     private final String XH;//序号4
     private final String PH;//品号5
     private final String PM;//规格6
     private final String SL;//数量7
     private final String BCKW;//拨出库位8
     private final String BCCW;//拨出仓库9
     private final String ISTM;//是否启用条码10

     private final String GLCJ;//管理层级11
     private final String BRKW;//拨入库位12
     private final String BRCW;//拨入储位12
     private final String PIH;//批号14

     private final String BMBH;//部门编号15
     private final String BMMC;//部门名称16
     private final String GG;//规格17
     private final String DW;//单位18
     private final String KW;//库位
     private final String STATE;//状态
     * @param dbKjdbList
     */
    @Override
    public void saveDbKjdb(@NonNull List<DbKjdb> dbKjdbList) {
        checkNotNull(dbKjdbList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_KJDBD + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_KJDBD + "," //库间调拨单
                    + DbEntry.COLUMN_NAME_DB + ","    //单别
                    + DbEntry.COLUMN_NAME_DH + ","    //单号    3
                    + DbEntry.COLUMN_NAME_XH + ","    //序号4
                    + DbEntry.COLUMN_NAME_PH + ","    //品号5
                    + DbEntry.COLUMN_NAME_PM + ","    //品名6
                    + DbEntry.COLUMN_NAME_SL + ","    //数量7
                    + DbEntry.COLUMN_NAME_BCKW + ","  //拨出库位8
                    + DbEntry.COLUMN_NAME_BCCW + ","  //拨出仓库9
                    + DbEntry.COLUMN_NAME_ISTM + ","  //是否启用条码10
                    + DbEntry.COLUMN_NAME_GLCJ + ","  //管理层级11
                    + DbEntry.COLUMN_NAME_BRKW + ","  //拨入库位12
                    + DbEntry.COLUMN_NAME_BRCW + ","  //拨入储位12
                    + DbEntry.COLUMN_NAME_PIH + ","   //批号14
                    + DbEntry.COLUMN_NAME_BMBH + ","   //部门编号15
                    + DbEntry.COLUMN_NAME_BMMC + ","   //部门名称16
                    + DbEntry.COLUMN_NAME_GG + ","    //规格17
                    + DbEntry.COLUMN_NAME_DW + ","    //单位18
                    + DbEntry.COLUMN_NAME_KW + ","    //库位
                    + DbEntry.COLUMN_NAME_BCSL + ","    //匹配数量
                    + DbEntry.COLUMN_NAME_BRSL + ","
                    + DbEntry.COLUMN_NAME_STATE      //状态
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbKjdb dbdata : dbKjdbList) {
                stat.bindString(1, dbdata.getKJDBD());//库间调拨单
                stat.bindString(2, dbdata.getDB()); //单别
                stat.bindString(3, dbdata.getDH()); //单号    3
                stat.bindString(4, dbdata.getXH());  //序号4
                stat.bindString(5, dbdata.getPH());   //品号5
                stat.bindString(6, dbdata.getPM()); //品名6
                stat.bindString(7, dbdata.getSL()); //数量7
                stat.bindString(8, dbdata.getBCKW()); //拨出库位8
                stat.bindString(9, dbdata.getBCCW()); //拨出仓库9
                stat.bindString(10, dbdata.getISTM());//是否启用条码10
                stat.bindString(11, dbdata.getGLCJ());//管理层级11
                stat.bindString(12, dbdata.getBRKW());//拨入库位12
                stat.bindString(13, dbdata.getBRCW());//拨入储位12
                stat.bindString(14, dbdata.getPIH());//批号14
                stat.bindString(15, dbdata.getBMBH()); //部门编号15
                stat.bindString(16, dbdata.getBMMC());   //部门名称16
                stat.bindString(17, dbdata.getGG());  //规格17
                stat.bindString(18, dbdata.getDW());  //单位18
                stat.bindString(19, dbdata.getKW());//库位19
                stat.bindString(20, dbdata.getBCSL());//库位20
                stat.bindString(21, dbdata.getBRSL());//库位20
                stat.bindString(22, dbdata.getSTATE());//21状态
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbKjdbList(@Nullable String kjdbd, GetDbKjdbCallback callback) {
        List<DbKjdb> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_KJDBD, //库间调拨单
                DbEntry.COLUMN_NAME_DB,    //单别
                DbEntry.COLUMN_NAME_DH,    //单号    3
                DbEntry.COLUMN_NAME_XH,    //序号4
                DbEntry.COLUMN_NAME_PH,    //品号5
                DbEntry.COLUMN_NAME_PM,    //品名6
                DbEntry.COLUMN_NAME_SL,    //数量7
                DbEntry.COLUMN_NAME_BCKW,  //拨出库位8
                DbEntry.COLUMN_NAME_BCCW,  //拨出仓库9
                DbEntry.COLUMN_NAME_ISTM,  //是否启用条码10
                DbEntry.COLUMN_NAME_GLCJ,  //管理层级11
                DbEntry.COLUMN_NAME_BRKW,  //拨入库位12
                DbEntry.COLUMN_NAME_BRCW,  //拨入储位12
                DbEntry.COLUMN_NAME_PIH,   //批号14
                DbEntry.COLUMN_NAME_BMBH,   //部门编号15
                DbEntry.COLUMN_NAME_BMMC,   //部门名称16
                DbEntry.COLUMN_NAME_GG,    //规格17
                DbEntry.COLUMN_NAME_DW,    //单位18
                DbEntry.COLUMN_NAME_KW,    //库位19
                DbEntry.COLUMN_NAME_BCSL,
                DbEntry.COLUMN_NAME_BRSL,
                DbEntry.COLUMN_NAME_STATE
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_KJDBD + " LIKE ?";
        String[] selectionArgs = {kjdbd};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_KJDBD, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_KJDBD + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String KJDBD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KJDBD));  //库间调拨单
                String DB = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DB));    //单别
                String dh = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DH));    //单号    3
                String xh = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XH));      //序号4
                String ph = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));        //品号5
                String pm = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));    //品名6
                String sl = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_SL));    //数量7
                String BCKW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BCKW));    //拨出库位8
                String BCCW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BCCW));    //拨出仓库9
                String istm = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ISTM));    //是否启用条码10
                String glcj = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GLCJ));    //管理层级11
                String BRKW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BRKW));    //拨入库位12
                String BRCW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BRCW));    //拨入储位12
                String pih = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PIH));    //批号14
                String bmbh = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BMBH));         //部门编号15
                String bmmc = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BMMC));         //部门名称16
                String gg = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));        //规格17
                String dw = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));    //单位18
                String kw = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));   //库位19
                String bcsl = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BCSL));
                String brsl = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_BRSL));
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));   //状态20
                DbKjdb dbKjdb = new DbKjdb(KJDBD, DB, dh, xh, ph, pm, sl, BCKW, BCCW, istm,
                        glcj, BRKW, BRCW, pih, bmbh, bmmc, gg, dw, kw, bcsl, brsl, STATE);

                dbDatalist.add(dbKjdb);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbKjdbListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbKjdb(@NonNull String kjdbd) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_KJDBD + " = ?";
        String[] selectionArgs = {kjdbd};
        db.delete(DbEntry.TABLE_NAME_KJDBD, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbPick(@NonNull List<DbPick> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_LLD + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_LLD + "," //领料单1
                    + DbEntry.COLUMN_NAME_QYBH + ","  //[2] 企业编号
                    + DbEntry.COLUMN_NAME_YYJD + ","  //[3] 据点
                    + DbEntry.COLUMN_NAME_LLDH + ","   //[4] 领料单号
                    + DbEntry.COLUMN_NAME_XC + ","    //[5] 项次
                    + DbEntry.COLUMN_NAME_LJBH + ","  //[6] 料号
                    + DbEntry.COLUMN_NAME_MC + ","  //[7] 名称
                    + DbEntry.COLUMN_NAME_GG + ","  //[8] 规格
                    + DbEntry.COLUMN_NAME_DW + ","  //[9] 单位
                    + DbEntry.COLUMN_NAME_SQSL + ","  //[10] 申请数量
                    + DbEntry.COLUMN_NAME_CK + ","  //[11] 指定仓库
                    + DbEntry.COLUMN_NAME_PH + ","  //[12] 指定批号
                    + DbEntry.COLUMN_NAME_GDDH + ","  //[13] 工单单号
                    + DbEntry.COLUMN_NAME_ZTM + ","   //[14] 状态码
                    + DbEntry.COLUMN_NAME_PPSL      //[15] PPSL
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPick dbdata : dbPickList) {
                stat.bindString(1, dbdata.getLLD());//领料单1
                stat.bindString(2, dbdata.getQYBH()); //[2] 企业编号
                stat.bindString(3, dbdata.getYYJD()); //[3] 据点
                stat.bindString(4, dbdata.getLLDH());  //[4] 领料单号
                stat.bindString(5, dbdata.getXC());   //[5] 项次
                stat.bindString(6, dbdata.getLJBH()); //[6] 料号
                stat.bindString(7, dbdata.getMC()); //[7] 名称
                stat.bindString(8, dbdata.getGG()); //[8] 规格
                stat.bindString(9, dbdata.getDW()); //[9] 单位
                stat.bindString(10, dbdata.getSQSL());//[10] 申请数量
                stat.bindString(11, dbdata.getCK());//[11] 指定仓库
                stat.bindString(12, dbdata.getPH());//[12] 指定批号
                stat.bindString(13, dbdata.getGDDH());//[13] 工单单号
                stat.bindString(14, dbdata.getZTM());//[14] 状态码
                stat.bindString(15, dbdata.getPPSL());//[15] ppsl

                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbPickList(@Nullable String lld, GetDbPickCallback callback) {
        List<DbPick> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLD,//领料单1
                DbEntry.COLUMN_NAME_QYBH, //[2] 企业编号
                DbEntry.COLUMN_NAME_YYJD, //[3] 据点
                DbEntry.COLUMN_NAME_LLDH,  //[4] 领料单号
                DbEntry.COLUMN_NAME_XC,   //[5] 项次
                DbEntry.COLUMN_NAME_LJBH, //[6] 料号
                DbEntry.COLUMN_NAME_MC, //[7] 名称
                DbEntry.COLUMN_NAME_GG, //[8] 规格
                DbEntry.COLUMN_NAME_DW, //[9] 单位
                DbEntry.COLUMN_NAME_SQSL, //[10] 申请数量
                DbEntry.COLUMN_NAME_CK, //[11] 指定仓库
                DbEntry.COLUMN_NAME_PH, //[12] 指定批号
                DbEntry.COLUMN_NAME_GDDH, //[13] 工单单号
                DbEntry.COLUMN_NAME_ZTM, //[14] 状态码
                DbEntry.COLUMN_NAME_PPSL, //[15] ppsl
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLD + " LIKE ?";
        String[] selectionArgs = {lld};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_LLD, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLD + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String LLD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLD));//领料单1单
                String QYBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_QYBH));  //[2] 企业编号
                String YYJD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YYJD));  //[3] 据点
                String LLDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDH));    //[4] 领料单号
                String XC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XC));      //[5] 项次
                String LJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LJBH));  //[6] 料号
                String MC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MC));  //[7] 名称
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[8] 规格
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));  //[9] 单位
                String SQSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_SQSL));  //[10] 申请数量
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));  //[11] 指定仓库
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[12] 指定批号
                String GDDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GDDH));  //[13] 工单单号
                String ZTM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_ZTM));  //[14] 状态码
                String PPSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PPSL));  //[14] 状态码
                DbPick dbPick = new DbPick(LLD, QYBH, YYJD, LLDH, XC, LJBH, MC, GG, DW, SQSL,
                        CK, PH, GDDH, ZTM, PPSL);

                dbDatalist.add(dbPick);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbPick(@NonNull String lld) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLD + " = ?";
        String[] selectionArgs = {lld};
        db.delete(DbEntry.TABLE_NAME_LLD, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbPickItem(@NonNull List<DbPickItem> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_LLD_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_PH + ","    //[0]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[1]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[2]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[3]仓库
                    + DbEntry.COLUMN_NAME_KW + ","     //[4]库位
                    + DbEntry.COLUMN_NAME_PC + ","   //[5]批次
                    + DbEntry.COLUMN_NAME_DW + ","     //[6]单位
                    + DbEntry.COLUMN_NAME_KCSL + ","     //[7]库存数量
                    + DbEntry.COLUMN_NAME_TIME + ","     //[8]最后更新时间
                    + DbEntry.COLUMN_NAME_STATE + ","     //[9] 状态码值
                    + DbEntry.COLUMN_NAME_FLSL       //[10]发料数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPickItem dbdata : dbPickList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getPH());    //[0]品号
                stat.bindString(3, dbdata.getPM());    //[1]品名
                stat.bindString(4, dbdata.getGG());    //[2]规格
                stat.bindString(5, dbdata.getCK());    //[3]仓库
                stat.bindString(6, dbdata.getKW());    //[4]库位
                stat.bindString(7, dbdata.getPC());    //[5]批次
                stat.bindString(8, dbdata.getDW());     //[6]单位
                stat.bindString(9, dbdata.getKCSL());       //[7]库存数量
                stat.bindString(10, dbdata.getTIME());    //[8]最后更新时间
                stat.bindString(11, dbdata.getSTATE());  //[9] 状态码值
                stat.bindString(12, dbdata.getFLSL());  //[10] 发料数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbPickListItem(@Nullable String id, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_LLD_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbPickItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_LLD_ITEM, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbChtzd(@NonNull List<DbChtzd> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_CHTZD + "("
//                    + DbEntry.COLUMN_NAME_ID + ","//表id
                    + DbEntry.COLUMN_NAME_LLD + "," //领料单1
                    + DbEntry.COLUMN_NAME_QYBH + ","  //[2] 企业编号
                    + DbEntry.COLUMN_NAME_YYJD + ","  //[3] 据点
                    + DbEntry.COLUMN_NAME_LLDH + ","   //[4] 领料单号
                    + DbEntry.COLUMN_NAME_XC + ","    //[5] 项次
                    + DbEntry.COLUMN_NAME_LJBH + ","  //[6] 料号
                    + DbEntry.COLUMN_NAME_MC + ","  //[7] 名称
                    + DbEntry.COLUMN_NAME_GG + ","  //[8] 规格
                    + DbEntry.COLUMN_NAME_SQSL + ","  //[9] 申请数量
                    + DbEntry.COLUMN_NAME_DW + ","  //[10] 单位
                    + DbEntry.COLUMN_NAME_CK + ","  //[11] 指定仓库
                    + DbEntry.COLUMN_NAME_PH + ","  //[12] 指定批号
                    + DbEntry.COLUMN_NAME_PPSL      //[15] PPSL
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbChtzd dbdata : dbPickList) {
                stat.bindString(1, dbdata.getLLD());//领料单1
                stat.bindString(2, dbdata.getQYBH()); //[2] 企业编号
                stat.bindString(3, dbdata.getYYJD()); //[3] 据点
                stat.bindString(4, dbdata.getLLDH());  //[4] 领料单号
                stat.bindString(5, dbdata.getXC());   //[5] 项次
                stat.bindString(6, dbdata.getLJBH()); //[6] 料号
                stat.bindString(7, dbdata.getMC()); //[7] 名称
                stat.bindString(8, dbdata.getGG()); //[8] 规格
                stat.bindString(9, dbdata.getSQSL());//[9] 申请数量
                stat.bindString(10, dbdata.getDW()); //[10] 单位
                stat.bindString(11, dbdata.getCK());//[11] 指定仓库
                stat.bindString(12, dbdata.getPH());//[12] 指定批号
                stat.bindString(13, dbdata.getPPSL());//[15] ppsl

                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbChtzdList(@Nullable String lld, GetDbChtzdCallback callback) {
        List<DbChtzd> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLD,//领料单1
                DbEntry.COLUMN_NAME_QYBH, //[2] 企业编号
                DbEntry.COLUMN_NAME_YYJD, //[3] 据点
                DbEntry.COLUMN_NAME_LLDH,  //[4] 领料单号
                DbEntry.COLUMN_NAME_XC,   //[5] 项次
                DbEntry.COLUMN_NAME_LJBH, //[6] 料号
                DbEntry.COLUMN_NAME_MC, //[7] 名称
                DbEntry.COLUMN_NAME_GG, //[8] 规格
                DbEntry.COLUMN_NAME_SQSL, //[9] 申请数量
                DbEntry.COLUMN_NAME_DW, //[10] 单位
                DbEntry.COLUMN_NAME_CK, //[11] 指定仓库
                DbEntry.COLUMN_NAME_PH, //[12] 指定批号
                DbEntry.COLUMN_NAME_PPSL, //[13] ppsl
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLD + " LIKE ?";
        String[] selectionArgs = {lld};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_CHTZD, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLD + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String LLD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLD));//领料单1单
                String QYBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_QYBH));  //[2] 企业编号
                String YYJD = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YYJD));  //[3] 据点
                String LLDH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDH));    //[4] 领料单号
                String XC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XC));      //[5] 项次
                String LJBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LJBH));  //[6] 料号
                String MC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_MC));  //[7] 名称
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[8] 规格
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));  //[9] 单位
                String SQSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_SQSL));  //[10] 申请数量
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));  //[11] 指定仓库
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[12] 指定批号
                String PPSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PPSL));  //[14] 状态码
                DbChtzd dbPick = new DbChtzd(LLD, QYBH, YYJD, LLDH, XC, LJBH, MC, GG, SQSL, DW,
                        CK, PH, PPSL);

                dbDatalist.add(dbPick);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbChtzdListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbChtzd(@NonNull String lld) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLD + " = ?";
        String[] selectionArgs = {lld};
        db.delete(DbEntry.TABLE_NAME_CHTZD, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbChtzdItem(@NonNull List<DbPickItem> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_CHTZD_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_PH + ","    //[0]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[1]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[2]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[3]仓库
                    + DbEntry.COLUMN_NAME_KW + ","     //[4]库位
                    + DbEntry.COLUMN_NAME_PC + ","   //[5]批次
                    + DbEntry.COLUMN_NAME_DW + ","     //[6]单位
                    + DbEntry.COLUMN_NAME_KCSL + ","     //[7]库存数量
                    + DbEntry.COLUMN_NAME_TIME + ","     //[8]最后更新时间
                    + DbEntry.COLUMN_NAME_STATE + ","     //[9] 状态码值
                    + DbEntry.COLUMN_NAME_FLSL       //[10]发料数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPickItem dbdata : dbPickList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getPH());    //[0]品号
                stat.bindString(3, dbdata.getPM());    //[1]品名
                stat.bindString(4, dbdata.getGG());    //[2]规格
                stat.bindString(5, dbdata.getCK());    //[3]仓库
                stat.bindString(6, dbdata.getKW());    //[4]库位
                stat.bindString(7, dbdata.getPC());    //[5]批次
                stat.bindString(8, dbdata.getDW());     //[6]单位
                stat.bindString(9, dbdata.getKCSL());       //[7]库存数量
                stat.bindString(10, dbdata.getTIME());    //[8]最后更新时间
                stat.bindString(11, dbdata.getSTATE());  //[9] 状态码值
                stat.bindString(12, dbdata.getFLSL());  //[10] 发料数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbChtzdListItem(@Nullable String id, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_CHTZD_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbChtzdItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_CHTZD_ITEM, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbCgrkItem(@NonNull List<DbPickItem> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_CGTZD_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_PH + ","    //[0]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[1]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[2]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[3]仓库
                    + DbEntry.COLUMN_NAME_KW + ","     //[4]库位
                    + DbEntry.COLUMN_NAME_PC + ","   //[5]批次
                    + DbEntry.COLUMN_NAME_DW + ","     //[6]单位
                    + DbEntry.COLUMN_NAME_KCSL + ","     //[7]库存数量
                    + DbEntry.COLUMN_NAME_TIME + ","     //[8]最后更新时间
                    + DbEntry.COLUMN_NAME_STATE + ","     //[9] 状态码值
                    + DbEntry.COLUMN_NAME_FLSL       //[10]发料数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPickItem dbdata : dbPickList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getPH());    //[0]品号
                stat.bindString(3, dbdata.getPM());    //[1]品名
                stat.bindString(4, dbdata.getGG());    //[2]规格
                stat.bindString(5, dbdata.getCK());    //[3]仓库
                stat.bindString(6, dbdata.getKW());    //[4]库位
                stat.bindString(7, dbdata.getPC());    //[5]批次
                stat.bindString(8, dbdata.getDW());     //[6]单位
                stat.bindString(9, dbdata.getKCSL());       //[7]库存数量
                stat.bindString(10, dbdata.getTIME());    //[8]最后更新时间
                stat.bindString(11, dbdata.getSTATE());  //[9] 状态码值
                stat.bindString(12, dbdata.getFLSL());  //[10] 发料数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbCgrkListItem(@Nullable String id, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_CGTZD_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbCgrkItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_CGTZD_ITEM, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbZszfItem(@NonNull List<DbPickItem> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_ZSZF_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_PH + ","    //[0]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[1]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[2]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[3]仓库
                    + DbEntry.COLUMN_NAME_KW + ","     //[4]库位
                    + DbEntry.COLUMN_NAME_PC + ","   //[5]批次
                    + DbEntry.COLUMN_NAME_DW + ","     //[6]单位
                    + DbEntry.COLUMN_NAME_KCSL + ","     //[7]库存数量
                    + DbEntry.COLUMN_NAME_TIME + ","     //[8]最后更新时间
                    + DbEntry.COLUMN_NAME_STATE + ","     //[9] 状态码值
                    + DbEntry.COLUMN_NAME_FLSL       //[10]发料数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPickItem dbdata : dbPickList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getPH());    //[0]品号
                stat.bindString(3, dbdata.getPM());    //[1]品名
                stat.bindString(4, dbdata.getGG());    //[2]规格
                stat.bindString(5, dbdata.getCK());    //[3]仓库
                stat.bindString(6, dbdata.getKW());    //[4]库位
                stat.bindString(7, dbdata.getPC());    //[5]批次
                stat.bindString(8, dbdata.getDW());     //[6]单位
                stat.bindString(9, dbdata.getKCSL());       //[7]库存数量
                stat.bindString(10, dbdata.getTIME());    //[8]最后更新时间
                stat.bindString(11, dbdata.getSTATE());  //[9] 状态码值
                stat.bindString(12, dbdata.getFLSL());  //[10] 发料数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbZszfListItem(@Nullable String id, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_ZSZF_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbZszfItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_ZSZF_ITEM, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbDdxhItem(@NonNull List<DbDdxhItem> ddxhItemList) {
        checkNotNull(ddxhItemList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_DDXH_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_LS_TZXH + ","//[0]序号
                    + DbEntry.COLUMN_NAME_PH + ","    //[1]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[2]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[3]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[4]仓库
                    + DbEntry.COLUMN_NAME_YJCHL + ","     //[5]预计出货量
                    + DbEntry.COLUMN_NAME_PPSL + ","   //[6]匹配数量
                    + DbEntry.COLUMN_NAME_XMBH + ","     //[7]项目编号
                    + DbEntry.COLUMN_NAME_XMMC + ","     //[7]项目名称
                    + DbEntry.COLUMN_NAME_IF
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbDdxhItem dbdata : ddxhItemList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getXH());//[0]序号
                stat.bindString(3, dbdata.getPH());    //[0]品号
                stat.bindString(4, dbdata.getPM());    //[1]品名
                stat.bindString(5, dbdata.getGG());    //[2]规格
                stat.bindString(6, dbdata.getCK());    //[3]仓库
                stat.bindString(7, dbdata.getYJCHL());    //[4]库位
                stat.bindString(8, dbdata.getPPSL());    //[5]批次
                stat.bindString(9, dbdata.getXMBH());     //[6]单位
                stat.bindString(10, dbdata.getXMMC());       //[7]库存数量
                stat.bindString(11, dbdata.getIF());
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbDdxhItemList(@Nullable String id, GetDbDdxhItemCallback callback) {
        List<DbDdxhItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_LS_TZXH,
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_YJCHL,       //[4]预计出货量
                DbEntry.COLUMN_NAME_PPSL,       //[5]匹配数量
                DbEntry.COLUMN_NAME_XMBH,        //[6]项目编号
                DbEntry.COLUMN_NAME_XMMC,        //[7]项目名称
                DbEntry.COLUMN_NAME_IF,

        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_DDXH_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String XH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LS_TZXH));
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String YJCHL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_YJCHL));      //[5]批次
                String PPSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PPSL));       //[6]单位
                String XMBH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XMBH));         //[7]库存数量
                String XMMC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_XMMC)); //[8]最后更新时间
                String IF = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_IF));
                DbDdxhItem item = new DbDdxhItem(ID, XH, PH, PM, GG, CK, YJCHL, PPSL, XMBH, XMMC, IF);

                dbDatalist.add(item);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbDdxhItemListLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbDdxhItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_DDXH_ITEM, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveDbKjdbItem(@NonNull List<DbPickItem> dbPickList) {
        checkNotNull(dbPickList);
        SQLiteDatabase db = null;
        try {
            db = mDbDbHelper.getWritableDatabase();
            String sql = "insert into " + DbEntry.TABLE_NAME_KJDB_ITEM + "("
                    + DbEntry.COLUMN_NAME_LLDLS_ID + ","    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                    + DbEntry.COLUMN_NAME_PH + ","    //[0]品号
                    + DbEntry.COLUMN_NAME_PM + ","   //[1]品名
                    + DbEntry.COLUMN_NAME_GG + ","   //[2]规格
                    + DbEntry.COLUMN_NAME_CK + ","   //[3]仓库
                    + DbEntry.COLUMN_NAME_KW + ","     //[4]库位
                    + DbEntry.COLUMN_NAME_PC + ","   //[5]批次
                    + DbEntry.COLUMN_NAME_DW + ","     //[6]单位
                    + DbEntry.COLUMN_NAME_KCSL + ","     //[7]库存数量
                    + DbEntry.COLUMN_NAME_TIME + ","     //[8]最后更新时间
                    + DbEntry.COLUMN_NAME_STATE + ","     //[9] 状态码值
                    + DbEntry.COLUMN_NAME_FLSL       //[10]发料数量
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement stat = db.compileStatement(sql);  //预编译Sql语句避免重复解析Sql语句
            db.beginTransaction();  //开启事务
            for (DbPickItem dbdata : dbPickList) {
                stat.bindString(1, dbdata.getID());    //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                stat.bindString(2, dbdata.getPH());    //[0]品号
                stat.bindString(3, dbdata.getPM());    //[1]品名
                stat.bindString(4, dbdata.getGG());    //[2]规格
                stat.bindString(5, dbdata.getCK());    //[3]仓库
                stat.bindString(6, dbdata.getKW());    //[4]库位
                stat.bindString(7, dbdata.getPC());    //[5]批次
                stat.bindString(8, dbdata.getDW());     //[6]单位
                stat.bindString(9, dbdata.getKCSL());       //[7]库存数量
                stat.bindString(10, dbdata.getTIME());    //[8]最后更新时间
                stat.bindString(11, dbdata.getSTATE());  //[9] 状态码值
                stat.bindString(12, dbdata.getFLSL());  //[10] 发料数量
                long result = stat.executeInsert();
                if (result < 0) {
                    return;
                }
            }
            db.setTransactionSuccessful();  //控制回滚，如果不设置此项自动回滚
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();  //事务提交
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void getDbKjdbListItem(@Nullable String id, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_KJDB_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void getDbKjdbListItem(@Nullable String id, String type, GetDbPickItemCallback callback) {
        List<DbPickItem> dbDatalist = new ArrayList<>();
        SQLiteDatabase db = mDbDbHelper.getReadableDatabase();
        String[] projection = {
                DbEntry.COLUMN_NAME_LLDLS_ID,      //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                DbEntry.COLUMN_NAME_PH,       //[0]品号
                DbEntry.COLUMN_NAME_PM,       //[1]品名
                DbEntry.COLUMN_NAME_GG,       //[2]规格
                DbEntry.COLUMN_NAME_CK,         //[3]仓库
                DbEntry.COLUMN_NAME_KW,       //[4]库位
                DbEntry.COLUMN_NAME_PC,       //[5]批次
                DbEntry.COLUMN_NAME_DW,        //[6]单位
                DbEntry.COLUMN_NAME_KCSL,        //[7]库存数量
                DbEntry.COLUMN_NAME_TIME,    //[8]最后更新时间
                DbEntry.COLUMN_NAME_STATE,//[9] 状态码值
                DbEntry.COLUMN_NAME_FLSL,
        };

        //通过库间调拨单号获取数据
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " LIKE ?AND " + DbEntry.COLUMN_NAME_TIME + " LIKE ?";
        String[] selectionArgs = {id,type};

        Cursor c = db.query(
                DbEntry.TABLE_NAME_KJDB_ITEM, projection, selection, selectionArgs, null, null, DbEntry.COLUMN_NAME_LLDLS_ID + " desc", null);
        /*Cursor c = db.query(
                DbEntry.TABLE_NAME_LS, projection, null, null, null, null, DbEntry.COLUMN_NAME_LS_TZXH+" desc",null);*/

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String ID = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_LLDLS_ID));   //表id 9标识id 领料单号+ 料号+position  (lld+lh+position)
                String PH = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PH));  //[0]品号
                String PM = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PM));  //[1]品名
                String GG = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_GG));  //[2]规格
                String CK = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_CK));        //[3]仓库
                String KW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KW));  //[4]库位
                String PC = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_PC));      //[5]批次
                String DW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_DW));       //[6]单位
                String KCSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_KCSL));         //[7]库存数量
                String TIMW = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_TIME)); //[8]最后更新时间
                String STATE = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_STATE));       //[9] 状态码值
                String FLSL = c.getString(c.getColumnIndexOrThrow(DbEntry.COLUMN_NAME_FLSL));       //[9] 状态码值
                DbPickItem pickItem = new DbPickItem(PH, PM, GG, CK, KW, PC, DW, KCSL, TIMW, ID, STATE, FLSL);

                dbDatalist.add(pickItem);

            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        if (dbDatalist.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDbPickListItemLoaded(dbDatalist);
        }
    }

    @Override
    public void deleteDbKjdbItem(@NonNull String id) {
        SQLiteDatabase db = mDbDbHelper.getWritableDatabase();
        String selection = DbEntry.COLUMN_NAME_LLDLS_ID + " = ?";
        String[] selectionArgs = {id};
        db.delete(DbEntry.TABLE_NAME_KJDB_ITEM, selection, selectionArgs);
        db.close();
    }
}
