package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2020/1/2.
 */

public class DbDdxhItem {
    private final String ID;
    private final String XH; //序号
    private final String PH;//品号
    private final String PM;//品名
    private final String GG;//规格
    private final String CK;//仓库
    private final String YJCHL;//预计出货量
    private final String PPSL;//匹配数量
    private final String XMBH;//项目编号
    private final String XMMC;//项目名称
    private final String IF;//判断条件

    public DbDdxhItem(String ID, String XH, String PH, String PM, String GG, String CK,
                      String YJCHL, String PPSL, String XMBH, String XMMC, String IF) {
        this.ID = ID;
        this.XH = XH;
        this.PH = PH;
        this.PM = PM;
        this.GG = GG;
        this.CK = CK;
        this.YJCHL = YJCHL;
        this.PPSL = PPSL;
        this.XMBH = XMBH;
        this.XMMC = XMMC;
        this.IF = IF;
    }

    public String getID() {
        return ID;
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

    public String getGG() {
        return GG;
    }

    public String getCK() {
        return CK;
    }

    public String getYJCHL() {
        return YJCHL;
    }

    public String getPPSL() {
        return PPSL;
    }

    public String getXMBH() {
        return XMBH;
    }

    public String getXMMC() {
        return XMMC;
    }

    @Override
    public String toString() {
        return "DbDdxhItem{" +
                "ID='" + ID + '\'' +
                ", XH='" + XH + '\'' +
                ", PH='" + PH + '\'' +
                ", PM='" + PM + '\'' +
                ", GG='" + GG + '\'' +
                ", CK='" + CK + '\'' +
                ", YJCHL='" + YJCHL + '\'' +
                ", PPSL='" + PPSL + '\'' +
                ", XMBH='" + XMBH + '\'' +
                ", XMMC='" + XMMC + '\'' +
                ", IF='" + IF + '\'' +
                '}';
    }

    public String getIF() {
        return IF;
    }

    public static List<Object> toObjectList(List<DbDdxhItem> items) {
        List<Object> list = new ArrayList<>();
        for (DbDdxhItem ddxhItem : items) {
            List<Object> objectsList = new ArrayList<>();
            objectsList.add(ddxhItem.getXH());
            objectsList.add(ddxhItem.getPH());
            objectsList.add(ddxhItem.getPM());
            objectsList.add(ddxhItem.getGG());
            objectsList.add(ddxhItem.getCK());
            objectsList.add(ddxhItem.getYJCHL());
            objectsList.add(ddxhItem.getXMBH());
            objectsList.add(ddxhItem.getXMMC());
            objectsList.add(ddxhItem.getIF());
            objectsList.add(ddxhItem.getPPSL());
            list.add(objectsList);
        }
        return list;
    }
}
