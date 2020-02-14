package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/16.
 */

public class DbZszf {
    private final String ZSZFDH;//杂收杂发单号
    private final String QYBH;//企业编号
    private final String YYJD;//营业据点
    private final String DJBH;//单据编号
    private final String DJLB;//单据类别
    private final String ZDY;//自定义
    private final String XC;//项次
    private final String LJBH;//料件编号
    private final String KW;//库位
    private final String CW;//储位
    private final String PH;//批号
    private final String YDSL;//调拨数量
    private final String MC;//名称
    private final String GG;//规格
    private final String DW;//单位

    public DbZszf(String ZSZFDH, String QYBH, String YYJD, String DJBH, String DJLB, String ZDY, String XC
            , String LJBH, String KW, String CW, String PH, String YDSL, String MC, String GG, String DW) {
        this.ZSZFDH = ZSZFDH;
        this.QYBH = QYBH;
        this.YYJD = YYJD;
        this.DJBH = DJBH;
        this.DJLB = DJLB;
        this.ZDY = ZDY;
        this.XC = XC;
        this.LJBH = LJBH;
        this.KW = KW;
        this.CW = CW;
        this.PH = PH;
        this.YDSL = YDSL;
        this.MC = MC;
        this.GG = GG;
        this.DW = DW;
    }

    public String getZSZFDH() {
        return ZSZFDH;
    }

    public String getQYBH() {
        return QYBH;
    }

    public String getYYJD() {
        return YYJD;
    }

    public String getDJBH() {
        return DJBH;
    }

    public String getDJLB() {
        return DJLB;
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

    public String getKW() {
        return KW;
    }

    public String getCW() {
        return CW;
    }

    public String getPH() {
        return PH;
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

    public String getYDSL() {
        return YDSL;
    }

    public static List<Object> toListObject(List<DbZszf> dbZszfList) {
        List<Object> objList = new ArrayList<>();
        for (DbZszf zszf : dbZszfList) {
            List<Object> list = new ArrayList<>();
            list.add(zszf.getQYBH());//0
            list.add(zszf.getYYJD());//1
            list.add(zszf.getDJLB());//2
            list.add(zszf.getDJBH());//3
            list.add(zszf.getXC());//4
            list.add(zszf.getLJBH());//5
            list.add(zszf.getMC());//6
            list.add(zszf.getGG());//7
            list.add(zszf.getDW());//8
            list.add(zszf.getYDSL());//9
            list.add(zszf.getKW());//10
            list.add(zszf.getZDY());//杂收杂发数量11
            list.add(zszf.getPH());//杂收杂发pc12
            objList.add(list);
        }

        return objList;
    }

    @Override
    public String toString() {
        return "DbZszf{" +
                "QYBH='" + QYBH + '\'' +
                ", YYJD='" + YYJD + '\'' +
                ", DJBH='" + DJBH + '\'' +
                ", DJLB='" + DJLB + '\'' +
                ", ZDY='" + ZDY + '\'' +
                ", XC='" + XC + '\'' +
                ", LJBH='" + LJBH + '\'' +
                ", KW='" + KW + '\'' +
                ", CW='" + CW + '\'' +
                ", PH='" + PH + '\'' +
                ", YDSL='" + YDSL + '\'' +
                '}';
    }
}
