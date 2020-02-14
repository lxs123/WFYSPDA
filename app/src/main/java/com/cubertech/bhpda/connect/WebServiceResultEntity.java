package com.cubertech.bhpda.connect;

/**
 * Created by Administrator on 2017/5/10.
 */

public class WebServiceResultEntity {

    private Object result;
    private Boolean Success;
    private int Total;
    private String MsgInfo;
    private String Obj;



    public String getObj() {
        return Obj;
    }

    public void setObj(String obj) {
        Obj = obj;
    }

    public String getMsgInfo() {
        return MsgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        MsgInfo = msgInfo;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public Boolean getSuccess() {
        return Success;
    }

    public void setSuccess(Boolean success) {
        Success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "WebServiceResultEntity{" +
                "result=" + result +
                ", Success=" + Success +
                ", Total=" + Total +
                ", MsgInfo='" + MsgInfo + '\'' +
                ", Obj='" + Obj + '\'' +
                '}';
    }
}
