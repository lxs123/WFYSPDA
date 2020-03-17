package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/18.
 */

public class DbPickItem {

    private final String PH;   //[0]品号
    private final String PM;   //[1]品名
    private final String GG;   //[2]规格
    private final String CK;   //[3]仓库
    private final String KW;   //[4]库位
    private final String PC;   //[5]批次
    private final String DW;   //[6]单位
    private final String KCSL; //[7]库存数量
    private final String TIME; //[8]最后更新时间   //kjdb 时间更替为转移类型  拨出-1，拨入 1
    private final String ID;   // 9标识id 领料单号+ 料号+position  (lld+lh+position)
    private final String STATE;//10选择状态
    private final String FLSL;

    public DbPickItem(String PH, String PM, String GG, String CK, String KW, String PC,
                      String DW, String KCSL, String TIME, String ID, String STATE, String FLSL) {
        this.PH = PH;
        this.PM = PM;
        this.GG = GG;
        this.CK = CK;
        this.KW = KW;
        this.PC = PC;
        this.DW = DW;
        this.KCSL = KCSL;
        this.TIME = TIME;
        this.ID = ID;
        this.STATE = STATE;
        this.FLSL = FLSL;
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

    public String getKW() {
        return KW;
    }

    public String getPC() {
        return PC;
    }

    public String getDW() {
        return DW;
    }

    public String getKCSL() {
        return KCSL;
    }

    public String getTIME() {
        return TIME;
    }

    public String getID() {
        return ID;
    }

    public String getSTATE() {
        return STATE;
    }

    public String getFLSL() {
        return FLSL;
    }

    @Override
    public String toString() {
        return "DbPickItem{" +
                "PH='" + PH + '\'' +
                ", PM='" + PM + '\'' +
                ", GG='" + GG + '\'' +
                ", CK='" + CK + '\'' +
                ", KW='" + KW + '\'' +
                ", PC='" + PC + '\'' +
                ", DW='" + DW + '\'' +
                ", KCSL='" + KCSL + '\'' +
                ", TIME='" + TIME + '\'' +
                ", ID='" + ID + '\'' +
                ", STATE='" + STATE + '\'' +
                ", FLSL='" + FLSL + '\'' +
                '}';
    }

    public static List<Object> toListObject(List<DbPickItem> dbPickList) {
        //0企业编号 1 据点 2挑拨单号 3项次 4料号 5名称 6规格 7 单位8 调拨数量 9 拨出库位 10拨入库位
        List<Object> objList = new ArrayList<>();
        for (DbPickItem pick : dbPickList) {
            List<Object> list = new ArrayList<>();
            list.add(pick.getPH());//[0]品号
            list.add(pick.getPM());//[1]品名
            list.add(pick.getGG());//[2]规格
            list.add(pick.getCK());////[3]仓库
            list.add(pick.getKW());//[4]库位
            list.add(pick.getPC());////[5]批次
            list.add(pick.getDW());////[6]单位
            list.add(pick.getKCSL());////[7]库存数量
            list.add(pick.getTIME());//[8]最后更新时间
            list.add(pick.getSTATE());//9
            list.add(pick.getFLSL());//10发料数量
            objList.add(list);
        }

        return objList;
    }
}
