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

public interface DbDataSource {
    interface LoadDbCallback {
        void onDbsLoaded(List<DbData> dbdatas);

        void onDataNotAvailable();
    }

    interface GetDbCallback {
        void onDbLoaded(DbData dbdata);

        void onDataNotAvailable();
    }


    interface GetDbLCallback {
        void onDbListLoaded(List<DbListresult> dblistresults);

        void onDataNotAvailable();
    }

    interface GetDbZszfCallback {
        void onDbZszfListLoaded(List<DbZszf> dbZszfList);

        void onDataNotAvailable();
    }

    interface GetDbCgtzdCallback {
        void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList);

        void onDataNotAvailable();
    }

    interface GetDbKjdbCallback {
        void onDbKjdbListLoaded(List<DbKjdb> dbZszfList);

        void onDataNotAvailable();
    }

    interface GetDbPickCallback {
        void onDbPickListLoaded(List<DbPick> dbPickList);

        void onDataNotAvailable();
    }

    interface GetDbPickItemCallback {
        void onDbPickListItemLoaded(List<DbPickItem> dbPickList);

        void onDataNotAvailable();
    }

    interface GetDbChtzdCallback {
        void onDbChtzdListLoaded(List<DbChtzd> dbPickList);

        void onDataNotAvailable();
    }

    interface GetDbDdxhItemCallback {
        void onDbDdxhItemListLoaded(List<DbDdxhItem> dbDdxhItemList);

        void onDataNotAvailable();
    }


    void batchSaveDbData(@NonNull List<DbData> dbDataList);//批量插入DbData

    void getDbDatasASC(@NonNull String PH, @NonNull LoadDbCallback callback);//按日期升序排列获取列表数据

    void getDbData(@NonNull String MZ, @NonNull GetDbCallback callback);//根据MZ获取单条数据

    void updateDbData(@NonNull DbData dbData);//修改DbPlan,修改数量

    void updateMZDbData(@NonNull String MZ, @NonNull String SYL);//修改DbPlan的剩余数量

    void deleteAllDbData();//删除所有DbData数据

    boolean ExistsPH(@NonNull String PH);//判断表中是否存在该品号的值


    void batchSaveDbListresult(@NonNull List<DbListresult> dbListresult);//批量插入DbListresult

    void getDbListresult(@NonNull String DB, @NonNull String DH, @NonNull GetDbLCallback callback);//获取数据库中的DbListresult

    void deleteAllDbListresult();//删除所有DbListresult数据

    void deletXHDbListresult(@NonNull String DB, @NonNull String DH);//销货后删除指定数据

    void deletTKDbListresult(@NonNull String MT);//退库操作(根据箱唛头或托唛头删除数据)

    void saveDbZszf(@NonNull List<DbZszf> dbZszfList);//插入杂收杂发

    void getDbZszfList(@Nullable String zszfdh, GetDbZszfCallback callback);

    void deleteDbZszf(@NonNull String zszfdh);

    void saveDbCgtzd(@NonNull List<DbCgtzd> dbZszfList);//插入杂收杂发

    void getDbCgtzdList(@Nullable String zszfdh, GetDbCgtzdCallback callback);

    void deleteDbCgtzd(@NonNull String zszfdh);

    void saveDbDhys(@NonNull List<DbCgtzd> dbZszfList);//插入杂收杂发

    void getDbDhysList(@Nullable String zszfdh, GetDbCgtzdCallback callback);

    void deleteDbDhys(@NonNull String zszfdh);

    void saveDbKjdb(@NonNull List<DbKjdb> dbKjdbList);//插入杂收杂发

    void getDbKjdbList(@Nullable String kjdbd, GetDbKjdbCallback callback);

    void deleteDbKjdb(@NonNull String kjdbd);

    void saveDbPick(@NonNull List<DbPick> dbPickList);//插入杂收杂发

    void getDbPickList(@Nullable String lld, GetDbPickCallback callback);

    void deleteDbPick(@NonNull String lld);

    void saveDbPickItem(@NonNull List<DbPickItem> dbPickList);//插入杂收杂发

    void getDbPickListItem(@Nullable String id, GetDbPickItemCallback callback);

    void deleteDbPickItem(@NonNull String id);

    void saveDbChtzd(@NonNull List<DbChtzd> dbPickList);//插入杂收杂发

    void getDbChtzdList(@Nullable String lld, GetDbChtzdCallback callback);

    void deleteDbChtzd(@NonNull String lld);

    void saveDbChtzdItem(@NonNull List<DbPickItem> dbPickList);//插入杂收杂发

    void getDbChtzdListItem(@Nullable String id, GetDbPickItemCallback callback);

    void deleteDbChtzdItem(@NonNull String id);

    void saveDbCgrkItem(@NonNull List<DbPickItem> dbPickList);//插入杂收杂发

    void getDbCgrkListItem(@Nullable String id, GetDbPickItemCallback callback);

    void deleteDbCgrkItem(@NonNull String id);

    void saveDbZszfItem(@NonNull List<DbPickItem> dbPickList);//插入杂收杂发

    void getDbZszfListItem(@Nullable String id, GetDbPickItemCallback callback);

    void deleteDbZszfItem(@NonNull String id);

    void saveDbDdxhItem(@NonNull List<DbDdxhItem> ddxhItemList);//插入订单销货

    void getDbDdxhItemList(@Nullable String id, GetDbDdxhItemCallback callback);

    void deleteDbDdxhItem(@NonNull String id);
}
