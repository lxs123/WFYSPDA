package com.cubertech.bhpda.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DbRemoteDataSource implements DbDataSource {

    private static DbRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, DbData> DBPLANs_SERVICE_DATA;

    static {
        DBPLANs_SERVICE_DATA = new LinkedHashMap<>();
        //addDbPlan("1","1");
        //addDBPlan("2","2");
    }

    public static DbRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbRemoteDataSource();
        }
        return INSTANCE;
    }

    private DbRemoteDataSource() {
    }

    @Override
    public void batchSaveDbData(@NonNull List<DbData> dbDataList) {

    }

    @Override
    public void getDbDatasASC(@NonNull String PH, @NonNull LoadDbCallback callback) {

    }

    @Override
    public void getDbData(@NonNull String MZ, @NonNull GetDbCallback callback) {

    }

    @Override
    public void updateDbData(@NonNull DbData dbData) {

    }

    @Override
    public void updateMZDbData(@NonNull String MZ, @NonNull String SYL) {

    }

    @Override
    public void deleteAllDbData() {

    }

    @Override
    public boolean ExistsPH(@NonNull String PH) {
        return false;
    }

    @Override
    public void batchSaveDbListresult(@NonNull List<DbListresult> dbListresult) {

    }

    @Override
    public void getDbListresult(@NonNull String DB, @NonNull String DH, @NonNull GetDbLCallback callback) {

    }

    @Override
    public void deleteAllDbListresult() {

    }

    @Override
    public void deletXHDbListresult(@NonNull String DB, @NonNull String DH) {

    }

    @Override
    public void deletTKDbListresult(@NonNull String MT) {

    }

    @Override
    public void saveDbZszf(@NonNull List<DbZszf> dbZszfList) {

    }

    @Override
    public void getDbZszfList(@Nullable String zszfdh, GetDbZszfCallback callback) {

    }

    @Override
    public void deleteDbZszf(@NonNull String zszfdh) {

    }

    @Override
    public void saveDbCgtzd(@NonNull List<DbCgtzd> dbZszfList) {

    }

    @Override
    public void getDbCgtzdList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {

    }

    @Override
    public void deleteDbCgtzd(@NonNull String zszfdh) {

    }

    @Override
    public void saveDbDhys(@NonNull List<DbCgtzd> dbZszfList) {

    }

    @Override
    public void getDbDhysList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {

    }

    @Override
    public void deleteDbDhys(@NonNull String zszfdh) {

    }

    @Override
    public void saveDbKjdb(@NonNull List<DbKjdb> dbKjdbList) {

    }

    @Override
    public void getDbKjdbList(@Nullable String kjdbd, GetDbKjdbCallback callback) {

    }

    @Override
    public void deleteDbKjdb(@NonNull String kjdbd) {

    }

    @Override
    public void saveDbPick(@NonNull List<DbPick> dbPickList) {

    }

    @Override
    public void getDbPickList(@Nullable String lld, GetDbPickCallback callback) {

    }

    @Override
    public void deleteDbPick(@NonNull String lld) {

    }

    @Override
    public void saveDbPickItem(@NonNull List<DbPickItem> dbPickList) {

    }

    @Override
    public void getDbPickListItem(@Nullable String id, GetDbPickItemCallback callback) {

    }

    @Override
    public void deleteDbPickItem(@NonNull String id) {

    }

    @Override
    public void saveDbChtzd(@NonNull List<DbChtzd> dbPickList) {

    }

    @Override
    public void getDbChtzdList(@Nullable String lld, GetDbChtzdCallback callback) {

    }

    @Override
    public void deleteDbChtzd(@NonNull String lld) {

    }

    @Override
    public void saveDbChtzdItem(@NonNull List<DbPickItem> dbPickList) {

    }

    @Override
    public void getDbChtzdListItem(@Nullable String id, GetDbPickItemCallback callback) {

    }

    @Override
    public void deleteDbChtzdItem(@NonNull String id) {

    }

    @Override
    public void saveDbCgrkItem(@NonNull List<DbPickItem> dbPickList) {

    }

    @Override
    public void getDbCgrkListItem(@Nullable String id, GetDbPickItemCallback callback) {

    }

    @Override
    public void deleteDbCgrkItem(@NonNull String id) {

    }

    @Override
    public void saveDbZszfItem(@NonNull List<DbPickItem> dbPickList) {

    }

    @Override
    public void getDbZszfListItem(@Nullable String id, GetDbPickItemCallback callback) {

    }

    @Override
    public void deleteDbZszfItem(@NonNull String id) {

    }

    @Override
    public void saveDbDdxhItem(@NonNull List<DbDdxhItem> ddxhItemList) {

    }

    @Override
    public void getDbDdxhItemList(@Nullable String id, GetDbDdxhItemCallback callback) {

    }

    @Override
    public void deleteDbDdxhItem(@NonNull String id) {

    }
}
