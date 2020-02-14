package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 领料
 * Created by Administrator on 2019/12/18.
 */

public class DbChtzd {
    //[0] 企业编号
    //[1] 据点
    //[2] 领料单号
    //[3] 项次
    //[4] 料号
    //[5] 名称
    //[6] 规格
    //[7] 申请数量
    //[8] 单位
    //[9] 指定仓库
    //[10] 指定批号
    //[11] 工单单号
    //[12] 状态码
    private final String LLD;    //领料单1出货单号
    private final String QYBH;   //[2] 企业编号
    private final String YYJD;   //[3] 据点
    private final String LLDH;   //[4] 领料单号 = >出货单号
    private final String XC;     //[5] 项次
    private final String LJBH;   //[6] 料号
    private final String MC;     //[7] 名称
    private final String GG;     //[8] 规格
    private final String SQSL;   //[9] 申请数量
    private final String DW;     //[10] 单位
    private final String CK;     //[11] 指定仓库
    private final String PH;     //[12] 指定批号
    private final String PPSL;

    //[0]品号 //[1]品名//[2]规格//[3]仓库//[4]库位
    //[5]批次//[6]单位//[7]库存数量//[8]最后更新时间


    public DbChtzd(String LLD, String QYBH, String YYJD, String LLDH, String XC, String LJBH,
                   String MC, String GG, String SQSL, String DW, String CK, String PH, String PPSL) {
        this.LLD = LLD;
        this.QYBH = QYBH;
        this.YYJD = YYJD;
        this.LLDH = LLDH;
        this.XC = XC;
        this.LJBH = LJBH;
        this.MC = MC;
        this.GG = GG;
        this.SQSL = SQSL;
        this.DW = DW;
        this.CK = CK;
        this.PH = PH;
        this.PPSL = PPSL;
    }

    public String getLLD() {
        return LLD;
    }

    public String getQYBH() {
        return QYBH;
    }

    public String getYYJD() {
        return YYJD;
    }

    public String getLLDH() {
        return LLDH;
    }

    public String getXC() {
        return XC;
    }

    public String getLJBH() {
        return LJBH;
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

    public String getSQSL() {
        return SQSL;
    }

    public String getCK() {
        return CK;
    }

    public String getPH() {
        return PH;
    }


    @Override
    public String toString() {
        return "DbChtzd{" +
                "LLD='" + LLD + '\'' +
                ", QYBH='" + QYBH + '\'' +
                ", YYJD='" + YYJD + '\'' +
                ", LLDH='" + LLDH + '\'' +
                ", XC='" + XC + '\'' +
                ", LJBH='" + LJBH + '\'' +
                ", MC='" + MC + '\'' +
                ", GG='" + GG + '\'' +
                ", SQSL='" + SQSL + '\'' +
                ", DW='" + DW + '\'' +
                ", CK='" + CK + '\'' +
                ", PH='" + PH + '\'' +
                ", PPSL='" + PPSL + '\'' +
                '}';
    }

    public String getPPSL() {
        return PPSL;
    }

    public static List<Object> toListObject(List<DbChtzd> dbChtzdkList) {
        //0企业编号 1 据点 2挑拨单号 3项次 4料号 5名称 6规格 7 单位8 调拨数量 9 拨出库位 10拨入库位
        List<Object> objList = new ArrayList<>();
        for (DbChtzd pick : dbChtzdkList) {
            List<Object> list = new ArrayList<>();
            list.add(pick.getQYBH());//0企业编号
            list.add(pick.getYYJD());//1据点
            list.add(pick.getLLDH());//2领料单号
            list.add(pick.getXC());//3项次
            list.add(pick.getLJBH());//4料号
            list.add(pick.getMC());//5名称
            list.add(pick.getGG());//6规格
            list.add(pick.getSQSL());//7 申请数量
            list.add(pick.getDW());//8单位
            list.add(pick.getCK());//9 指定仓库
            list.add(pick.getPH());//10 指定批号
            list.add(pick.getPPSL());//11
            objList.add(list);
        }

        return objList;
    }
}
