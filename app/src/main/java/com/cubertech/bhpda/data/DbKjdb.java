package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/17.
 */

public class DbKjdb {
    private final String KJDBD;//库间调拨单1
    private final String QYBH;//企业编号2
    private final String YYJD;//营业据点3
    private final String ZDY;//自定义4
    private final String XC;//项次5
    private final String LJBH;//料件编号6
    private final String BCSL;//拨出数量7
    private final String BCKW;//拨出库位8
    private final String BCCW;//拨出储位9
    private final String BCPH;//拨出批号10

    private final String BRSL;//拨入数量11
    private final String BRKW;//拨入库位12
    private final String BRCW;//拨入储位12
    private final String BRPH;//拨入批号14

    private final String DBSL;//调拨数量15
    private final String MC;//名称16
    private final String GG;//规格17
    private final String DW;//单位18
    private final String STATE;//状态

    public DbKjdb(String KJDBD, String QYBH, String YYJD, String ZDY, String XC, String LJBH,
                  String BCSL, String BCKW, String BCCW, String BCPH, String BRSL, String BRKW,
                  String BRCW, String BRPH, String ydsl, String mc, String gg, String dw, String STATE) {
        this.KJDBD = KJDBD;
        this.QYBH = QYBH;
        this.YYJD = YYJD;
        this.ZDY = ZDY;
        this.XC = XC;
        this.LJBH = LJBH;
        this.BCSL = BCSL;
        this.BCKW = BCKW;
        this.BCCW = BCCW;
        this.BCPH = BCPH;
        this.BRSL = BRSL;
        this.BRKW = BRKW;
        this.BRCW = BRCW;
        this.BRPH = BRPH;
        this.DBSL = ydsl;
        this.MC = mc;
        this.GG = gg;
        this.DW = dw;
        this.STATE = STATE;
    }

    public String getKJDBD() {
        return KJDBD;
    }

    public String getQYBH() {
        return QYBH;
    }

    public String getYYJD() {
        return YYJD;
    }

    public String getZDY() {
        return ZDY;
    }

    public String getXC() {
        return XC;
    }

    public String getLJBH() {
        return LJBH;
    }

    public String getBCSL() {
        return BCSL;
    }

    public String getBCKW() {
        return BCKW;
    }

    public String getBCCW() {
        return BCCW;
    }

    public String getBCPH() {
        return BCPH;
    }

    public String getBRSL() {
        return BRSL;
    }

    public String getBRKW() {
        return BRKW;
    }

    public String getBRCW() {
        return BRCW;
    }

    public String getBRPH() {
        return BRPH;
    }

    public String getDBSL() {
        return DBSL;
    }

    public String getMC() {
        return MC;
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

    @Override
    public String toString() {
        return "DbKjdb{" +
                "KJDBD='" + KJDBD + '\'' +
                ", QYBH='" + QYBH + '\'' +
                ", YYJD='" + YYJD + '\'' +
                ", ZDY='" + ZDY + '\'' +
                ", XC='" + XC + '\'' +
                ", LJBH='" + LJBH + '\'' +
                ", BCSL='" + BCSL + '\'' +
                ", BCKW='" + BCKW + '\'' +
                ", BCCW='" + BCCW + '\'' +
                ", BCPH='" + BCPH + '\'' +
                ", BRSL='" + BRSL + '\'' +
                ", BRKW='" + BRKW + '\'' +
                ", BRCW='" + BRCW + '\'' +
                ", BRPH='" + BRPH + '\'' +
                ", DBSL='" + DBSL + '\'' +
                ", MC='" + MC + '\'' +
                ", GG='" + GG + '\'' +
                ", DW='" + DW + '\'' +
                ", STATE='" + STATE + '\'' +
                '}';
    }

    public static List<Object> toListObject(List<DbKjdb> dbZszfList) {
        //0企业编号 1 据点 2挑拨单号 3项次 4料号 5名称 6规格 7 单位8 调拨数量 9 拨出库位 10拨入库位
        List<Object> objList = new ArrayList<>();
        for (DbKjdb kjdb : dbZszfList) {
            List<Object> list = new ArrayList<>();
            list.add(kjdb.getQYBH());//0
            list.add(kjdb.getYYJD());//1
            list.add(kjdb.getKJDBD());//2
            list.add(kjdb.getXC());//3
            list.add(kjdb.getLJBH());//4
            list.add(kjdb.getMC());//5
            list.add(kjdb.getGG());//6
            list.add(kjdb.getDW());//7
            list.add(kjdb.getDBSL());//8
            list.add(kjdb.getBCKW());//9
            list.add(kjdb.getBRKW());//10
            list.add(kjdb.getSTATE());//状态11
            objList.add(list);
        }

        return objList;
    }
}
