package com.cubertech.bhpda.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.cubertech.bhpda.Activity.TransferPackage.RqmdyConfirmActivity;
import com.cubertech.bhpda.Activity.TransferPackage.TpmtdyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.TpmtdyConfirmActivity;
import com.cubertech.bhpda.Activity.TransferPackage.XmtdyConfirmActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/4.
 */

public class SQLiteUtils {
    private static SQLiteUtils instance;
    private SQLiteDatabase database;
    private List list;
    private List<Integer> idList;
    private ArrayList resultList;
    private ArrayList tmList;
    private ArrayList cptmList;
    private static Context context;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    public static final String RQM = "rqm";
    public static final String TPMT = "tpmt";
    public static final String XMT = "xmt";

    /**
     * @param context
     * @return
     */
    public static SQLiteUtils getInstance(Context context) {
//        if (instance == null) {
        instance = new SQLiteUtils(context);
//        }
        return instance;
    }

    public SQLiteUtils(Context context) {
        this.context = context;
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);
    }

    /**
     * 获取状态  如果存在 则提示 (首页检测)
     */
    public void getStatus() {
        database = mySQLiteOpenHelper.getReadableDatabase();
        list = new ArrayList<>();
        idList = new ArrayList<>();
        resultList = new ArrayList<>();
        tmList = new ArrayList<>();
        cptmList = new ArrayList<>();
        String sql = "select * from dmcode;";
        Cursor rawQuery = database.rawQuery(sql, null);
        while (rawQuery.moveToNext()) {
            String name = rawQuery.getString(rawQuery.getColumnIndex("flag"));
            int id = rawQuery.getInt(rawQuery.getColumnIndex("id"));
            String result = rawQuery.getString(rawQuery.getColumnIndex("result"));
            String tm = rawQuery.getString(rawQuery.getColumnIndex("tm"));
            String cptm = rawQuery.getString(rawQuery.getColumnIndex("cptm"));
            list.add(name);
            idList.add(id);
            resultList.add(result);
            tmList.add(tm);
            cptmList.add(cptm);
        }
        database.close();
        rawQuery.close();
//        for (int i = 0; i < ListUtils.getSize(list1); i++) {
//            for (int k = i + 1; k < ListUtils.getSize(list1); k++) {
//                if (TextUtils.equals(list1.get(i), list1.get(k))) {
//                    list1.remove(k);
//                    k--;
//                }
//            }
//        }
        Log.e("###", list.toString());
        if (!ListUtils.isEmpty(list)) {
            showDialog(list.get(0).toString(), 0);//
        }

    }

    /**
     * 获取状态  如果存在 则提示(打印生成前界面) flag:rqm   tpmt  xmt
     */
    public void getStatus(String flag) {
        database = mySQLiteOpenHelper.getReadableDatabase();
        list = new ArrayList<>();
        idList = new ArrayList<>();
        resultList = new ArrayList<>();
        tmList = new ArrayList<>();
        cptmList = new ArrayList<>();
        String sql = "select * from dmcode;";
        Cursor rawQuery = database.rawQuery(sql, null);
        while (rawQuery.moveToNext()) {
            String name = rawQuery.getString(rawQuery.getColumnIndex("flag"));
            int id = rawQuery.getInt(rawQuery.getColumnIndex("id"));
            String result = rawQuery.getString(rawQuery.getColumnIndex("result"));
            String tm = rawQuery.getString(rawQuery.getColumnIndex("tm"));
            String cptm = rawQuery.getString(rawQuery.getColumnIndex("cptm"));
            list.add(name);
            idList.add(id);
            resultList.add(result);
            tmList.add(tm);
            cptmList.add(cptm);
        }
        database.close();
        rawQuery.close();
        Log.e("###", resultList.toString());
        if (!ListUtils.isEmpty(list)) {
            int anInt = 0;
            for (int i = 0; i < ListUtils.getSize(list); i++) {
                if (TextUtils.equals(flag, list.get(i).toString())) {
                    anInt = i;
                    showDialog(list.get(anInt).toString(), anInt);//
                    break;
                }
            }

        }

    }

    private void showDialog(String flag, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        String flagStr = null;
        switch (flag) {
            case RQM:
                flagStr = "容器码";
                break;
            case XMT:
                flagStr = "箱唛头条码";
                break;
            case TPMT:
                flagStr = "托盘唛头条码";
                break;
        }
        builder.setMessage("检查当前存在未打印的" + flagStr + ",是否去打印?");
        builder.setNegativeButton("去打印", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.putExtra("dm", tmList.get(position).toString());
                //[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                intent.putExtra("result", (Serializable) ListUtils.toList(resultList.get(position).toString()));
                intent.putExtra("cptm", cptmList.get(position).toString());
                switch (flag) {
                    case RQM:
                        intent.setClass(context, RqmdyConfirmActivity.class);
                        context.startActivity(intent);
                        break;
                    case XMT:
                        intent.setClass(context, XmtdyConfirmActivity.class);
                        context.startActivity(intent);
                        break;
                    case TPMT:
                        intent.setClass(context, TpmtdyConfirmActivity.class);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                deleteDm(tmList.get(position).toString());
            }
        });
        builder.show();
    }

    public void saveDm(String dm, String flag, String cptm, String result) {
        database = mySQLiteOpenHelper.getReadableDatabase();
//        for (int i = 0; i < ListUtils.getSize(list); i++) {
//            if (StringUtils.equals(list.get(i), keywords)) {
//                database.delete("keywords", "id=?", new String[]{String.valueOf(i)});
//            }
//            for (int j = 0; j < ListUtils.getSize(list); j++) {
//                if (StringUtils.equals(list.get(i), list.get(j))) {
//                    database.delete("keywords", "id=?", new String[]{String.valueOf(j)});
//                }
//            }
//        }

        ContentValues values = new ContentValues();
        database = mySQLiteOpenHelper.getReadableDatabase();
        idList = new ArrayList<>();
        String sql = "select * from dmcode;";
        Cursor rawQuery = database.rawQuery(sql, null);
        while (rawQuery.moveToNext()) {
            int id = rawQuery.getInt(rawQuery.getColumnIndex("id"));
            idList.add(id);
        }

        rawQuery.close();
        values.put("flag", flag);
        values.put("id", ListUtils.isEmpty(idList) ? 0 : (idList.get(ListUtils.getSize(idList) - 1) + 1));
        values.put("tm", dm);
        values.put("result", result);
        values.put("cptm", cptm);
        database.insert("dmcode", null, values);
        database.close();
    }

    public void deleteDm(String dm) {
        database = mySQLiteOpenHelper.getReadableDatabase();
//        idList = new ArrayList<>();
//        String sql = "select * from dmcode;";
//        Cursor rawQuery = database.rawQuery(sql, null);
//        while (rawQuery.moveToNext()) {
//            int id = rawQuery.getInt(rawQuery.getColumnIndex("id"));
//            idList.add(id);
//        }
//
//        rawQuery.close();
        idList = new ArrayList<>();
        String sql = "select * from dmcode;";
        Cursor rawQuery = database.rawQuery(sql, null);
        while (rawQuery.moveToNext()) {
            int id = rawQuery.getInt(rawQuery.getColumnIndex("id"));
            idList.add(id);
        }

        rawQuery.close();
        if (!ListUtils.isEmpty(idList))
            database.delete("dmcode", "tm=?", new String[]{dm});
//        mySQLiteOpenHelper.deleteTable(database);
        database.close();
    }
}
