package com.cubertech.bhpda.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 采购Javabean类
 * Created by Administrator on 2019/12/16.
 */

public class DbCgtzd {
    //[0] 供应商编号
    //[1]采购供应商
    //[2]单别
    //[3]单号
    //[4] 品号
    //[5]品名
    //[6]序号
    //[7]规格
    //[8] 待检数量
    //[9] 是否急料


    private final String CgdhScan;//采购扫描/出货
    private final String QYBH;//企业编号 //[0] 企业编号
    private final String PGLX;//营业据点 //[10]据点 品管类型
    private final String Cgdh;//采购单号 //[2] 收货/采购单号
    private final String Cggys;// //[3]采购供应商
    private final String JYMC;//[11] 检验名称
    private final String DB;//单别 //[4]单别
    private final String DH;//料件编号 //[5]单号
    private final String PH;//库位 //[7]规格
    private final String Result;//批号 //[8]是否急料
    private final String Djsl;//调拨数量 //[9] 待检数量
    private final String MC;//名称 //[10] 名称
    private final String GG;//规格 //[11] 规格
    private final String XH;//序号
    private final String HGSL;//合格数量
    private final String BHGSL;//不合格数量
    private final String BHGYY;// 不合格原因
    private final String STATE;
//                0到货单别，1到货单号，2供应商编号，3供应商名称，4序号，5品号，6品名,7规格，8交货仓库，
//                9仓库名称10合格数量，11验退数量

    //0单别
    //1单号
    //2供应商编号
    //3采购供应商 名称
    //4//序号
//5//品号
    //6名称
//7规格
//8//待验数量
//9判定结果 是否急料
    //10品管类型
    //11检验名称
//12合格数量
    //13不合格数量
    //14不良原因
    //15状态

    /**
     * 到货验收     》  采购入库
     *
     * @param CgdhScan //
     * @param QYBH     //0 供应商编号》2
     * @param PGLX     //
     * @param Cgdh     //
     * @param Cggys    //1采购供应商》3
     * @param JYMC     //
     * @param DB       //2单别 》0
     * @param DH       //3单号 》1
     * @param PH       //4品号 》5
     * @param Result   //9是否急料  》9 仓库名称
     * @param Djsl     ////8待检数量 》8 交货仓库
     * @param MC       //5品名 》6
     * @param GG       //7规格 》7
     * @param XH       //6序号 》4
     * @param HGSL     //10合格数量  》10合格数量
     * @param BHGSL    //11不合格数量  》11验退数量
     * @param BHGYY    //12不良原因
     * @param STATE    //13状态
     */
    public DbCgtzd(String CgdhScan, String QYBH, String PGLX, String Cgdh, String Cggys, String JYMC, String DB
            , String DH, String PH, String Result, String Djsl, String MC, String GG, String XH, String HGSL
            , String BHGSL, String BHGYY, String STATE) {
        this.CgdhScan = CgdhScan;
        this.QYBH = QYBH;
        this.PGLX = PGLX;
        this.Cgdh = Cgdh;
        this.Cggys = Cggys;
        this.JYMC = JYMC;
        this.DB = DB;
        this.DH = DH;
        this.PH = PH;
        this.Result = Result;
        this.Djsl = Djsl;
        this.MC = MC;
        this.GG = GG;
        this.XH = XH;
        this.HGSL = HGSL;
        this.BHGSL = BHGSL;
        this.BHGYY = BHGYY;
        this.STATE = STATE;
    }

    public String getCgdhScan() {
        return CgdhScan;
    }

    public String getQYBH() {
        return QYBH;
    }

    public String getYYJD() {
        return PGLX;
    }

    public String getCgdh() {
        return Cgdh;
    }

    public String getCggys() {
        return Cggys;
    }

    public String getZDY() {
        return JYMC;
    }

    public String getXC() {
        return DB;
    }

    public String getLJBH() {
        return DH;
    }

    public String getKW() {
        return PH;
    }


    public String getResult() {
        return Result;
    }

    public String getMC() {
        return MC;
    }

    public String getGG() {
        return GG;
    }

    public String getDW() {
        return XH;
    }

    public String getCgsl() {
        return Djsl;
    }

    public String getHGSL() {
        return HGSL;
    }

    public String getBHGSL() {
        return BHGSL;
    }

    public String getBHGYY() {
        return BHGYY;
    }

    public String getSTATE() {
        return STATE;
    }

    @Override
    public String toString() {
        return "DbCgtzd{" +
                "CgdhScan='" + CgdhScan + '\'' +
                ", QYBH='" + QYBH + '\'' +
                ", PGLX='" + PGLX + '\'' +
                ", Cgdh='" + Cgdh + '\'' +
                ", Cggys='" + Cggys + '\'' +
                ", JYMC='" + JYMC + '\'' +
                ", DB='" + DB + '\'' +
                ", DH='" + DH + '\'' +
                ", PH='" + PH + '\'' +
                ", Result='" + Result + '\'' +
                ", Djsl='" + Djsl + '\'' +
                ", MC='" + MC + '\'' +
                ", GG='" + GG + '\'' +
                ", XH='" + XH + '\'' +
                ", HGSL='" + HGSL + '\'' +
                ", BHGSL='" + BHGSL + '\'' +
                ", BHGYY='" + BHGYY + '\'' +
                ", STATE='" + STATE + '\'' +
                '}';
    }

    public static List<Object> toListObject(List<DbCgtzd> dbZszfList) {
        List<Object> objList = new ArrayList<>();
        for (DbCgtzd zszf : dbZszfList) {
            List<Object> list = new ArrayList<>();
            list.add(zszf.getQYBH());     //0供应商编号
            list.add(zszf.getCggys());     //1采购供应商 名称
            list.add(zszf.getXC());       //2单别
            list.add(zszf.getLJBH());     //3单号
            list.add(zszf.getKW());     //4//品号
            list.add(zszf.getMC());      //5名称
            list.add(zszf.getDW());     //6//序号
            list.add(zszf.getGG());     //7规格
            list.add(zszf.getCgsl());   //8//待验数量
            list.add(zszf.getResult()); //9判定结果 是否急料
            list.add(zszf.getYYJD());//品管类型10
            list.add(zszf.getZDY());//检验名称11
            list.add(zszf.getHGSL());   //12合格数量
            list.add(zszf.getBHGSL());   //13不合格数量
            list.add(zszf.getBHGYY());   //14不良原因
            list.add(zszf.getSTATE());   //15状态
            objList.add(list);
        }

        return objList;
    }

    public static List<Object> toListObjectCg(List<DbCgtzd> dbZszfList) {
        List<Object> objList = new ArrayList<>();
        for (DbCgtzd zszf : dbZszfList) {
            List<Object> list = new ArrayList<>();
            list.add(zszf.getXC());       //2单别  ==>0
            list.add(zszf.getLJBH());     //3单号  ==>1
            list.add(zszf.getQYBH());     //0供应商编号  ==>2
            list.add(zszf.getCggys());     //1采购供应商 名称  ==>3
            list.add(zszf.getDW());     //6//序号==>4
            list.add(zszf.getKW());     //4//品号  ==>5
            list.add(zszf.getMC());      //5名称==>6
            list.add(zszf.getGG());     //7规格
            list.add(zszf.getCgsl());   //8//交货仓库
            list.add(zszf.getResult()); //9仓库名称
            list.add(zszf.getHGSL());   //10合格数量
            list.add(zszf.getBHGSL());   //11不合格数量
            list.add(zszf.getYYJD());//12条码类别
            list.add(zszf.getCgdh());//13品号属性
            list.add(zszf.getZDY());//14是否供应商
            list.add(zszf.getSTATE());   //15d单位
            list.add(zszf.getBHGYY());   //16不良原因
            list.add("");//17

            objList.add(list);
        }

        return objList;
    }
}
