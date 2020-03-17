package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/17.
 */

public class DbKjdb {
    //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
    //12管理层级   13批号   14 单位   15 库位   16 状态
    private final String KJDBD;//库间调拨单1
    private final String DB;//单别
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
    private final String BCSL;
    private final String BRSL;
    private final String STATE;//状态

    public DbKjdb(String KJDBD, String DB, String DH, String XH, String PH, String PM,
                  String SL, String BCPIH, String BCCW, String ISTM, String GLCJ, String BRPIH,
                  String BRCW, String PIH, String BMBH, String BMMC, String gg, String dw,
                  String KW, String BCSL, String BRSL, String STATE) {
        this.KJDBD = KJDBD;
        this.DB = DB;
        this.DH = DH;
        this.XH = XH;
        this.PH = PH;
        this.PM = PM;
        this.SL = SL;
        this.BCKW = BCPIH;
        this.BCCW = BCCW;
        this.ISTM = ISTM;
        this.GLCJ = GLCJ;
        this.BRKW = BRPIH;
        this.BRCW = BRCW;
        this.PIH = PIH;
        this.BMBH = BMBH;
        this.BMMC = BMMC;
        this.GG = gg;
        this.DW = dw;
        this.KW = KW;
        this.BRSL = BRSL;
        this.BCSL = BCSL;
        this.STATE = STATE;
    }

    public String getKJDBD() {
        return KJDBD;
    }

    public String getDB() {
        return DB;
    }

    public String getDH() {
        return DH;
    }

    public String getXH() {
        return XH;
    }

    public String getPH() {
        return PH;
    }

    public String getPM() {
        return PM;
    }

    public String getSL() {
        return SL;
    }

    public String getBCKW() {
        return BCKW;
    }

    public String getBCCW() {
        return BCCW;
    }

    public String getISTM() {
        return ISTM;
    }

    public String getGLCJ() {
        return GLCJ;
    }

    public String getBRKW() {
        return BRKW;
    }

    public String getBRCW() {
        return BRCW;
    }

    public String getPIH() {
        return PIH;
    }

    public String getBMBH() {
        return BMBH;
    }

    public String getBMMC() {
        return BMMC;
    }

    public String getGG() {
        return GG;
    }

    public String getDW() {
        return DW;
    }

    public String getSTATE() {
        return STATE;
    }

    public String getKW() {
        return KW;
    }

    public String getBCSL() {
        return BCSL;
    }

    public String getBRSL() {
        return BRSL;
    }

    @Override
    public String toString() {
        return "DbKjdb{" +
                "KJDBD='" + KJDBD + '\'' +
                ", DB='" + DB + '\'' +
                ", DH='" + DH + '\'' +
                ", XH='" + XH + '\'' +
                ", PH='" + PH + '\'' +
                ", PM='" + PM + '\'' +
                ", SL='" + SL + '\'' +
                ", BCKW='" + BCKW + '\'' +
                ", BCCW='" + BCCW + '\'' +
                ", ISTM='" + ISTM + '\'' +
                ", GLCJ='" + GLCJ + '\'' +
                ", BRKW='" + BRKW + '\'' +
                ", BRCW='" + BRCW + '\'' +
                ", PIH='" + PIH + '\'' +
                ", BMBH='" + BMBH + '\'' +
                ", BMMC='" + BMMC + '\'' +
                ", GG='" + GG + '\'' +
                ", DW='" + DW + '\'' +
                ", KW='" + KW + '\'' +
                ", BCSL='" + BCSL + '\'' +
                ", BRSL='" + BRSL + '\'' +
                ", STATE='" + STATE + '\'' +
                '}';
    }

    public static List<Object> toListObject(List<DbKjdb> dbZszfList) {
        //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
        //12管理层级   13批号   14 单位 15库位  16状态
        List<Object> objList = new ArrayList<>();
        for (DbKjdb kjdb : dbZszfList) {
            List<Object> list = new ArrayList<>();
            list.add(kjdb.getDB());//0
            list.add(kjdb.getDH());//1
            list.add(kjdb.getXH());//2
            list.add(kjdb.getPH());//3
            list.add(kjdb.getPM());//4
            list.add(kjdb.getGG());//5
            list.add(kjdb.getSL());//6
            list.add(kjdb.getBCCW());//7
            list.add(kjdb.getBRCW());//8
            list.add(kjdb.getBMBH());//9
            list.add(kjdb.getBMMC());//10
            list.add(kjdb.getISTM());//11
            list.add(kjdb.getGLCJ());//12
            list.add(kjdb.getPIH());//13
            list.add(kjdb.getDW());//14dw
            list.add(kjdb.getKW());//15kw
            list.add(kjdb.getBCCW());//16bcsl
            list.add(kjdb.getBRSL());//17brsl
            list.add(kjdb.getSTATE());//状态18
            objList.add(list);
        }

        return objList;
    }
}
