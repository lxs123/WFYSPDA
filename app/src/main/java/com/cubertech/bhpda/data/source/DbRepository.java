package com.cubertech.bhpda.data.source;

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

import java.util.List;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class DbRepository implements DbDataSource {


    private static DbRepository INSTANCE = null;

    private final DbDataSource mDbsRemoteDataSource;

    private final DbDataSource mDbsLocalDataSource;

    Map<String, DbData> mCachedDbPlans;

    /**
     * 将缓存标记为无效，以便在下次请求数据时强制更新。此变量具有包本地可见性，因此可以从测试中访问它。
     */
    boolean mCacheIsDirty = false;

    public DbRepository(DbDataSource dbsRemoteDataSource,
                        @NonNull DbDataSource dbsLocalDataSource) {
        this.mDbsRemoteDataSource = dbsRemoteDataSource;
        this.mDbsLocalDataSource = checkNotNull(dbsLocalDataSource);
    }

    public static DbRepository getInstance(DbDataSource dbsRemoteDataSource,
                                           DbDataSource dbsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DbRepository(dbsRemoteDataSource, dbsLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void batchSaveDbData(@NonNull List<DbData> dbDataList) {
        checkNotNull(dbDataList);
        mDbsLocalDataSource.batchSaveDbData(dbDataList);
    }

    @Override
    public void getDbDatasASC(@NonNull String PH, @NonNull LoadDbCallback callback) {
        mDbsLocalDataSource.getDbDatasASC(PH, new LoadDbCallback() {
            @Override
            public void onDbsLoaded(List<DbData> dbdatas) {
                callback.onDbsLoaded(dbdatas);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();

            }
        });

    }

    @Override
    public void getDbData(@NonNull String MZ, @NonNull GetDbCallback callback) {
        mDbsLocalDataSource.getDbData(MZ, new GetDbCallback() {
            @Override
            public void onDbLoaded(DbData dbdata) {
                callback.onDbLoaded(dbdata);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }

    @Override
    public void updateDbData(@NonNull DbData dbData) {
        checkNotNull(dbData);
        mDbsLocalDataSource.updateDbData(dbData);
    }

    @Override
    public void updateMZDbData(@NonNull String MZ, @NonNull String SYL) {
        mDbsLocalDataSource.updateMZDbData(MZ, SYL);
    }

    @Override
    public void deleteAllDbData() {
        mDbsLocalDataSource.deleteAllDbData();
    }

    @Override
    public boolean ExistsPH(@NonNull String PH) {
        return mDbsLocalDataSource.ExistsPH(PH);
    }


    @Override
    public void batchSaveDbListresult(@NonNull List<DbListresult> dbListresult) {
        checkNotNull(dbListresult);
        mDbsLocalDataSource.batchSaveDbListresult(dbListresult);
    }

    @Override
    public void getDbListresult(@NonNull String DB, @NonNull String DH, @NonNull GetDbLCallback callback) {
        mDbsLocalDataSource.getDbListresult(DB, DH, new GetDbLCallback() {
            @Override
            public void onDbListLoaded(List<DbListresult> dblistresults) {
                callback.onDbListLoaded(dblistresults);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteAllDbListresult() {
        mDbsLocalDataSource.deleteAllDbListresult();
    }

    @Override
    public void deletXHDbListresult(@NonNull String DB, @NonNull String DH) {
        mDbsLocalDataSource.deletXHDbListresult(DB, DH);
    }

    @Override
    public void deletTKDbListresult(@NonNull String MT) {
        mDbsLocalDataSource.deletTKDbListresult(MT);
    }

    @Override
    public void saveDbZszf(@NonNull List<DbZszf> dbZszfList) {
        mDbsLocalDataSource.saveDbZszf(dbZszfList);
    }

    @Override
    public void getDbZszfList(@Nullable String zszfdh, GetDbZszfCallback callback) {
        mDbsLocalDataSource.getDbZszfList(zszfdh, callback);
    }

    @Override
    public void deleteDbZszf(@NonNull String zszfdh) {
        mDbsLocalDataSource.deleteDbZszf(zszfdh);
    }

    @Override
    public void saveDbCgtzd(@NonNull List<DbCgtzd> dbZszfList) {
        mDbsLocalDataSource.saveDbCgtzd(dbZszfList);
    }

    @Override
    public void getDbCgtzdList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {
        mDbsLocalDataSource.getDbCgtzdList(zszfdh, callback);
    }

    @Override
    public void deleteDbCgtzd(@NonNull String zszfdh) {
        mDbsLocalDataSource.deleteDbCgtzd(zszfdh);
    }

    @Override
    public void saveDbDhys(@NonNull List<DbCgtzd> dbZszfList) {
        mDbsLocalDataSource.saveDbDhys(dbZszfList);
    }

    @Override
    public void getDbDhysList(@Nullable String zszfdh, GetDbCgtzdCallback callback) {
        mDbsLocalDataSource.getDbDhysList(zszfdh, callback);
    }

    @Override
    public void deleteDbDhys(@NonNull String zszfdh) {
        mDbsLocalDataSource.deleteDbDhys(zszfdh);
    }

    @Override
    public void saveDbKjdb(@NonNull List<DbKjdb> dbKjdbList) {
        mDbsLocalDataSource.saveDbKjdb(dbKjdbList);
    }

    @Override
    public void getDbKjdbList(@Nullable String kjdbd, GetDbKjdbCallback callback) {
        mDbsLocalDataSource.getDbKjdbList(kjdbd, callback);
    }

    @Override
    public void deleteDbKjdb(@NonNull String kjdbd) {
        mDbsLocalDataSource.deleteDbKjdb(kjdbd);
    }

    @Override
    public void saveDbPick(@NonNull List<DbPick> dbPickList) {
        mDbsLocalDataSource.saveDbPick(dbPickList);
    }

    @Override
    public void getDbPickList(@Nullable String lld, GetDbPickCallback callback) {
        mDbsLocalDataSource.getDbPickList(lld, callback);
    }

    @Override
    public void deleteDbPick(@NonNull String lld) {
        mDbsLocalDataSource.deleteDbPick(lld);
    }

    @Override
    public void saveDbPickItem(@NonNull List<DbPickItem> dbPickList) {
        mDbsLocalDataSource.saveDbPickItem(dbPickList);
    }

    @Override
    public void getDbPickListItem(@Nullable String id, GetDbPickItemCallback callback) {
        mDbsLocalDataSource.getDbPickListItem(id, callback);
    }

    @Override
    public void deleteDbPickItem(@NonNull String id) {
        mDbsLocalDataSource.deleteDbPickItem(id);
    }

    @Override
    public void saveDbChtzd(@NonNull List<DbChtzd> dbPickList) {
        mDbsLocalDataSource.saveDbChtzd(dbPickList);
    }

    @Override
    public void getDbChtzdList(@Nullable String lld, GetDbChtzdCallback callback) {
        mDbsLocalDataSource.getDbChtzdList(lld, callback);
    }

    @Override
    public void deleteDbChtzd(@NonNull String lld) {
        mDbsLocalDataSource.deleteDbChtzd(lld);
    }

    @Override
    public void saveDbChtzdItem(@NonNull List<DbPickItem> dbPickList) {
        mDbsLocalDataSource.saveDbChtzdItem(dbPickList);
    }

    @Override
    public void getDbChtzdListItem(@Nullable String id, GetDbPickItemCallback callback) {
        mDbsLocalDataSource.getDbChtzdListItem(id, callback);
    }

    @Override
    public void deleteDbChtzdItem(@NonNull String id) {
        mDbsLocalDataSource.deleteDbChtzdItem(id);
    }

    @Override
    public void saveDbCgrkItem(@NonNull List<DbPickItem> dbPickList) {
        mDbsLocalDataSource.saveDbCgrkItem(dbPickList);
    }

    @Override
    public void getDbCgrkListItem(@Nullable String id, GetDbPickItemCallback callback) {
        mDbsLocalDataSource.getDbCgrkListItem(id, callback);
    }

    @Override
    public void deleteDbCgrkItem(@NonNull String id) {
        mDbsLocalDataSource.deleteDbCgrkItem(id);
    }

    @Override
    public void saveDbZszfItem(@NonNull List<DbPickItem> dbPickList) {
        mDbsLocalDataSource.saveDbZszfItem(dbPickList);
    }

    @Override
    public void getDbZszfListItem(@Nullable String id, GetDbPickItemCallback callback) {
        mDbsLocalDataSource.getDbZszfListItem(id, callback);
    }

    @Override
    public void deleteDbZszfItem(@NonNull String id) {
        mDbsLocalDataSource.deleteDbZszfItem(id);
    }

    @Override
    public void saveDbDdxhItem(@NonNull List<DbDdxhItem> ddxhItemList) {
        mDbsLocalDataSource.saveDbDdxhItem(ddxhItemList);
    }

    @Override
    public void getDbDdxhItemList(@Nullable String id, GetDbDdxhItemCallback callback) {
        mDbsLocalDataSource.getDbDdxhItemList(id, callback);
    }

    @Override
    public void deleteDbDdxhItem(@NonNull String id) {
        mDbsLocalDataSource.deleteDbDdxhItem(id);
    }
}
