package com.cubertech.bhpda.data.source.local;

import android.provider.BaseColumns;

public class DbPersistenceContract {
    private DbPersistenceContract() {
    }

    public static abstract class DbEntry implements BaseColumns {
        public static final String TABLE_NAME = "DbData";//表名
        public static final String COLUMN_NAME_MZ = "MZ";//码值，托唛头或箱唛头码值(主键)
        public static final String COLUMN_NAME_RQ = "RQ";//日期
        public static final String COLUMN_NAME_PH = "PH";//品号
        public static final String COLUMN_NAME_SL = "SL";//数量
        public static final String COLUMN_NAME_CK = "CK";//仓库编号
        public static final String COLUMN_NAME_KW = "KW";//库位编号
        public static final String COLUMN_NAME_YSL = "YSL";//原数量

        //表DbListresult
        public static final String TABLE_NAME_LS = "DbListresult";//表名
        public static final String COLUMN_NAME_LS_ID = "id";//表id
        public static final String COLUMN_NAME_LS_KHBH = "KHBH";//客户编号
        public static final String COLUMN_NAME_LS_PH = "PH";//品号
        public static final String COLUMN_NAME_LS_SL = "SL";//数量
        public static final String COLUMN_NAME_LS_CK = "CK";//仓库
        public static final String COLUMN_NAME_LS_DB = "DB";//出货通知单别
        public static final String COLUMN_NAME_LS_DH = "DH";//出货通知单号
        public static final String COLUMN_NAME_LS_TZXH = "TZXH";//出货通知序号
        public static final String COLUMN_NAME_LS_KW = "KW";//库位
        public static final String COLUMN_NAME_LS_MT = "MT";//箱唛头码或托唛头码

        public static final String TABLE_NAME_ZSZF = "DbZszf";
        public static final String COLUMN_NAME_ZSZFDH = "ZSZFDH";//企业编号
        public static final String COLUMN_NAME_QYBH = "QYBH";//企业编号
        public static final String COLUMN_NAME_YYJD = "YYJD";//营业据点
        public static final String COLUMN_NAME_DJBH = "DJBH";//单据编号
        public static final String COLUMN_NAME_DJLB = "DJLB";//单据类别
        public static final String COLUMN_NAME_ZDY = "ZDY";//自定义
        public static final String COLUMN_NAME_XC = "XC";//项次
        public static final String COLUMN_NAME_LJBH = "LJBH";//料件编号
        public static final String COLUMN_NAME_CW = "CW";//储位
        public static final String COLUMN_NAME_YDSL = "YDSL";//异动数量
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_MC = "MC";//名称
        public static final String COLUMN_NAME_GG = "GG";//规格
        public static final String COLUMN_NAME_DW = "DW";//单位
        public static final String TABLE_NAME_ZSZF_ITEM = "DbZszfItem";//出货通知单
        //采购通知单
        public static final String TABLE_NAME_CGTZD = "DbCgtzd";//采购通知单
        //收货通知单
        public static final String TABLE_NAME_SHTZD = "DbShtzd";//采购通知单
        public static final String COLUMN_NAME_CGDHSCAN = "CGDHSCAN";//采购单号(扫描值)
        public static final String COLUMN_NAME_CGDH = "CGDH";//采购单号
        public static final String COLUMN_NAME_CGGYS = "CGGYS";//采购供应商
        public static final String COLUMN_NAME_CGSL = "CGSL";//采购数量
        public static final String COLUMN_NAME_RESULT = "RESULT";//指定结果
        public static final String COLUMN_NAME_HGSL = "HGSL";//合格数量
        public static final String COLUMN_NAME_BHGSL = "BHGSL";//不合格数量
        public static final String COLUMN_NAME_BHGYY = "BHGYY";//不合格原因
        public static final String TABLE_NAME_CGTZD_ITEM = "DbCgtzdItem";//出货通知单

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
        //库间调拨
        public static final String TABLE_NAME_KJDBD = "DbKjdbd";//采购通知单
        public static final String COLUMN_NAME_KJDBD = "KJDBD";//库间调拨单
        public static final String COLUMN_NAME_DB = "DB";
        public static final String COLUMN_NAME_DH = "DH";
        public static final String COLUMN_NAME_XH = "XH";
        public static final String COLUMN_NAME_ISTM = "ISTM";
        public static final String COLUMN_NAME_GLCJ = "GLCJ";
        public static final String COLUMN_NAME_PIH = "PIH";

        public static final String COLUMN_NAME_BMBH = "BMBH";
        public static final String COLUMN_NAME_BMMC = "BMMC";

        public static final String COLUMN_NAME_BCSL = "BCSL";//拨出数量
        public static final String COLUMN_NAME_BCKW = "BCKW";//拨出库位
        public static final String COLUMN_NAME_BCCW = "BCCW";//拨出储位
        public static final String COLUMN_NAME_BCPH = "BCPH";//拨出批号
        public static final String COLUMN_NAME_BRSL = "BRSL";//拨入数量
        public static final String COLUMN_NAME_BRKW = "BRKW";//拨入库位
        public static final String COLUMN_NAME_BRCW = "BRCW";//拨入储位
        public static final String COLUMN_NAME_BRPH = "BRPH";//拨入批号
        public static final String COLUMN_NAME_DBSL = "DBSL";//调拨数量
        public static final String COLUMN_NAME_STATE = "STATE";//状态

        //领料单号
        public static final String TABLE_NAME_LLD = "DbLld";//领料单
        public static final String COLUMN_NAME_LLD = "LLD";    //领料单1
        //        public final String COLUMN_NAME_QYBH = "";   //[2] 企业编号
//        public final String COLUMN_NAME_YYJD = "";   //[3] 据点
        public static final String COLUMN_NAME_LLDH = "LLDH";   //[4] 领料单号
        //        public final String COLUMN_NAME_XC = "";     //[5] 项次
//        public final String COLUMN_NAME_LJBH = "";   //[6] 料号
//        public final String COLUMN_NAME_MC = "";     //[7] 名称
//        public final String COLUMN_NAME_GG = "";     //[8] 规格
//        public final String COLUMN_NAME_DW = "";     //[9] 单位
        public static final String COLUMN_NAME_SQSL = "SQSL";   //[10] 申请数量
        //        public final String COLUMN_NAME_CK = "";     //[11] 指定仓库
//        public final String COLUMN_NAME_PH = "";     //[12] 指定批号
        public static final String COLUMN_NAME_GDDH = "GDDH";   //[13] 工单单号
        public static final String COLUMN_NAME_ZTM = "ZTM";    //[14] 状态码
        public static final String COLUMN_NAME_PPSL = "PPSL";


        public static final String TABLE_NAME_LLD_ITEM = "DbLldItem";//领料单
        //        public static final String COLUMN_NAME_PH = "";   //[0]品号
        public static final String COLUMN_NAME_PM = "PM";   //[1]品名
        //        public static final String COLUMN_NAME_GG = "";   //[2]规格
//        public static final String COLUMN_NAME_CK = "";   //[3]仓库
//        public static final String COLUMN_NAME_KW = "";   //[4]库位
        public static final String COLUMN_NAME_PC = "PC";   //[5]批次
        //        public static final String COLUMN_NAME_DW = "";   //[6]单位
        public static final String COLUMN_NAME_KCSL = "KCSL"; //[7]库存数量
        public static final String COLUMN_NAME_TIME = "TIME"; //[8]最后更新时间
        public static final String COLUMN_NAME_LLDLS_ID = "LldLhId";//表id   // 9标识id 领料单号+ 料号+position  (lld+lh+position)
        //        public static final String COLUMN_NAME_STATE = "";//10选择状态
        public static final String COLUMN_NAME_FLSL = "FLSL";//发料数量

        public static final String TABLE_NAME_CHTZD = "DbChtzd";//出货通知单
        public static final String TABLE_NAME_CHTZD_ITEM = "DbChtzdItem";//出货通知单

        public static final String TABLE_NAME_DDXH_ITEM = "DbDDXH_ITEM";//出货通知单
        public static final String COLUMN_NAME_YJCHL = "yjchl";//预计出货量
        public static final String COLUMN_NAME_XMBH = "xmbh";//项目编号
        public static final String COLUMN_NAME_XMMC = "xmmc";//项目名称
        public static final String COLUMN_NAME_IF = "IF";//项目名称

        public static final String TABLE_NAME_KJDB_ITEM = "DbKJDB_ITEM";//库间调拨库存信息表


    }
}
