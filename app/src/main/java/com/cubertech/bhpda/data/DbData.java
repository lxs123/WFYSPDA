package com.cubertech.bhpda.data;

import java.util.Objects;

public final class DbData {
    //日期
    private final String RQ;
    //品号
    private final String PH;
    //数量
    private final String SL;
    //托盘码或容器码值
    private final String MZ;
    //仓库编号
    private final String CK;
    //库位编号
    private final String KW;
    //原数量
    private final String YSL;

    public DbData(String mz, String rq, String ph, String sl, String ck, String kw, String ysl) {
        RQ = rq;
        PH = ph;
        SL = sl;
        MZ = mz;
        CK = ck;
        KW = kw;
        YSL=ysl;

    }
    public String getRQ() {
        return RQ;
    }

    public String getPH() {
        return PH;
    }

    public String getSL() {
        return SL;
    }

    public String getMZ() {
        return MZ;
    }

    public String getCK() { return CK; }

    public String getKW() { return KW; }

    public String getYSL() { return YSL; }



    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass())
            return false;
        DbData dbData=(DbData) o;
        return  Objects.equals(MZ,dbData.MZ)&&
                Objects.equals(YSL,dbData.YSL);
    }
}
