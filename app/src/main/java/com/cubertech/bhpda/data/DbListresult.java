package com.cubertech.bhpda.data;

import java.util.Objects;

public final class DbListresult {
  /*      o.add(tvCustomerId.getText().toString().trim());//客户编号[0]
                    o.add(PH);// 品号[1]
                    o.add(oo.get(1).toString().trim());//数量[2]
                    o.add(oo.get(2).toString().trim());//仓库[3]
                    o.add(tvChtzdb.getText().toString().trim());//出货通知单别[4]
                    o.add(tvChtzdh.getText().toString().trim());// 出货通知单号[5]
                    o.add(tzxh);// 出货通知序号[6]
                    o.add(oo.get(3).toString().trim());//库位[7]

    //2018-11-02新增箱子唛头或托唛头传递后端处理数据
                    o.add(oo.get(0).toString().trim());//[8]箱唛头码或托盘唛头码

                    listresult.add(o);*/

    //主键id
  private final String ID;
  //客户编号
  private final String KHBH;
  //品号
  private final String PH;
  //数量
  private final String SL;
  //仓库
  private final String CK;
  //出货通知单别
  private final String DB;
  //出货通知单号
  private final String DH;
  //出货通知序号
  private final String TZXH;
  //库位
  private final String KW;
  //箱唛头码或托唛头码
  private final String MT;

    public DbListresult(String id, String khbh, String ph, String sl, String ck, String db, String dh, String tzxh, String kw, String mt) {
        ID = id;
        KHBH = khbh;
        PH = ph;
        SL = sl;
        CK = ck;
        DB = db;
        DH = dh;
        TZXH = tzxh;
        KW = kw;
        MT = mt;
    }


    public String getID() {
        return ID;
    }

    public String getKHBH() {
        return KHBH;
    }

    public String getPH() {
        return PH;
    }

    public String getSL() {
        return SL;
    }

    public String getCK() {
        return CK;
    }

    public String getDB() {
        return DB;
    }

    public String getDH() {
        return DH;
    }

    public String getTZXH() {
        return TZXH;
    }

    public String getKW() {
        return KW;
    }

    public String getMT() {
        return MT;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DbListresult dbListresult=(DbListresult) o;

        return  Objects.equals(KHBH,dbListresult.KHBH)&&
                Objects.equals(PH,dbListresult.PH)&&
                Objects.equals(SL,dbListresult.SL)&&
                Objects.equals(CK,dbListresult.CK)&&
                Objects.equals(DB,dbListresult.DB)&&
                Objects.equals(DH,dbListresult.DH)&&
                Objects.equals(TZXH,dbListresult.TZXH)&&
                Objects.equals(KW,dbListresult.KW)&&
                Objects.equals(MT,dbListresult.MT);
    }
}
