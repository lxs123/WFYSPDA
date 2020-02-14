package com.cubertech.bhpda.connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.cubertech.bhpda.utils.ListUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/17.
 */


public class SiteService {
    public ProgressDialog progressDialog;
    private static SiteService siteService;

    //private Context context = null;

    public static SiteService getInstants() {
        if (siteService == null) {
            siteService = new SiteService();
        }
        return siteService;
    }

    /**
     * 用户登录（返回账套）
     * 传输String
     */
    public Observable<String> loginString(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "login"));
    }

    /**
     * 修改密码
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> updataPwd(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "updataPwd"));
    }

    /**
     * 传入账套数据
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> logindata(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "logindata"));
    }

    /**
     * 工序转移
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> AddZymxBh(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "AddZymxBh"));
    }

    /**
     * 返工转移  AddZymxFgBh
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> AddZymxFgBh(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "AddZymxFgBh"));
    }

    /**
     * 质检
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> AddzjmxZJBH(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "AddzjmxZJBH"));
    }

    /**
     * 完工入库
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> Addwgrk(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "Addwgrk"));
    }


    /**
     * 工序转移中的条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> DMcodeGxzyBh(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "DMcodeGxzyBh"));
    }

    /**
     * 根据箱唛头或托盘唛头获取数量,仓库,库位
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getXH_KW(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getXH_KW"));
    }

    /**
     * 工序转移中的条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getYg(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getYg"));
    }

    public Observable<List<Object>> getXMT(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(List.class, new WebService(url, params, "getXMT"));
    }

    /**
     * 唛箱头复制扫描获取数据
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getFZMT(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(List.class, new WebService(url, params, "getFZMT"));
    }

    /**
     * 唛头码复制确定按钮
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> updataFZMT(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(List.class, new WebService(url, params, "updataFZMT"));
    }

    /**
     * 箱唛头打印中添加容器码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getRqm(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getRqm"));
    }

    /**
     * 箱唛头打印中添加容器码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> getNumRQ(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "getNumRQ"));
    }


    /**
     * 容器码打印中的条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getRqsc(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getRqsc"));
    }

    /**
     * 销货操作判断先进先出
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getXH_XJXC(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getXH_XJXC"));
    }

    /**
     * 销货操作
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> saveXH(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) progressDialog.dismiss();
        progressDialog = ProgressDialog.show(context, "", "正在加载....", false, false);
        String url = "";
        if (TextUtils.isEmpty(ip)) {
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "saveXH"));
    }

    /**
     * 生成容器码并保存
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> getDM_Save(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) progressDialog.dismiss();
        progressDialog = ProgressDialog.show(context, "", "正在加载....", false, false);
        String url = "";
        if (TextUtils.isEmpty(ip)) {
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "getDM_Save"));
    }

    /**
     * 生成箱唛头码并保存
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getMT_Save(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) progressDialog.dismiss();
        progressDialog = ProgressDialog.show(context, "", "正在加载....", false, false);
        String url = "";
        if (TextUtils.isEmpty(ip)) {
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(List.class, new WebService(url, params, "getMT_Save"));
    }
    //getTP_Save

    /**
     * 生成箱托盘唛头码并保存
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getTP_Save(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) progressDialog.dismiss();
        progressDialog = ProgressDialog.show(context, "", "正在加载....", false, false);
        String url = "";
        if (TextUtils.isEmpty(ip)) {
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(List.class, new WebService(url, params, "getTP_Save"));
    }


    /**
     * 箱唛头打印,扫描二维码获取数据
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> GetMTList(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "GetMTList"));
    }


    /**
     * 完工入库
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> QRcodeWgrk(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "QRcodeWgrk"));
    }

    /**
     * 返工入库
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> QRcodeFgrk(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "QRcodeFgrk"));
    }

    /**
     * 获取销货数据 getXH_new
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getXH(HashMap<String, Object> params, Context context, String ip, String port) {
        /*if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);*/
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getXH"));
    }

    public Observable<List<Object>> getXH_new(HashMap<String, Object> params, Context context, String ip, String port, boolean isProgress) {
        if (isProgress) {
            if (!(null == progressDialog)) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "正在加载....",
                    false, false);
        }

        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getXH_new"));
    }

    /**
     * getXH_Query
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getXH_Query(HashMap<String, Object> params, Context context, String ip, String port, boolean isProgress) {
        if (isProgress) {
            if (!(null == progressDialog)) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "正在加载....",
                    false, false);
        }

        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getXH_Query"));
    }
    public Observable<List<Object>> getCH(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getCH"));
    }

    /**
     * 返工转移中的条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> DMcodeFgzyBh(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "DMcodeFgzyBh"));
    }

    /**
     * 合并箱操作
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> SaveHb_X(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveHb_X"));
    }

    /**
     * SaveHB_T
     * 合并托盘操作
     */
    public Observable<String> SaveHB_T(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveHB_T"));
    }

    /**
     * 拆解箱操作
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> SaveCj_X(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveCj_X"));
    }

    /**
     * 移库操作保存数据
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> SaveYK(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveYK"));
    }

    /**
     * 上架操作保存
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> SaveSJ(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveSJ"));
    }


    /**
     * 拆解托保存
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> SaveCj_T(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "SaveCj_T"));
    }

    /**
     * 合箱操作扫描容器码
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getHX(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getHX"));
    }

    /**
     * 拆箱操作扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getCX(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getCX"));
    }

    /**
     * 扫描库位码
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getKW(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getKW"));
    }


    /**
     * 移库操作扫描箱唛头或托盘唛头
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getYK(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getYK"));
    }


    /**
     * 上架操作扫描箱唛头或托盘唛头码
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getSJ(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getSJ"));
    }

    /**
     * 合托唛头码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getHT(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getHT"));
    }

    /**
     * 拆托操作扫描码
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getCT(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getCT"));
    }

    /**
     * 质检中的条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> DMcodeZjBh(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "DMcodeZjBh"));
    }

    /**
     * 获取供应商列表
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getPURMAA(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getPURMAA"));
    }

    /**
     * 获取仓库列表
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getCMSMC(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getCMSMC"));
    }

    /**
     * 工单条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getgd(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getgd"));
    }

    public Observable<String> logPring(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "logPring"));

    }

    /**
     * 主件条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getzj(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getzj"));
    }

    /**
     * 元件条码扫描
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> getyj(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "getyj"));
    }


    /**
     * 提交绑定
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<String> addlist(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/PWS.asmx";
        }
        return (Observable<String>) callWebService(String.class, new WebService(url, params, "addlist"));
    }



    /**
     * 元件供应商查询
     *
     * @param params
     * @param context
     * @param ip
     * @param port
     * @return
     */
    public Observable<List<Object>> query(HashMap<String, Object> params, Context context, String ip, String port) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(context, "", "正在加载....",
                false, false);
        String url = "";
        if (ip.equals("")) {
            //新建一个连接对象为了获取url
            ConnWebService connWebService = new ConnWebService(context);
            url = connWebService.getUrl();
        } else {
            url = "http://" + ip + ":" + port + "/VWebService.asmx";
        }
        return (Observable<List<Object>>) callWebService(Object.class, new WebService(url, params, "query"));
    }


    public Observable<?> callWebService(final Class<?> subclass, final WebService webService) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                //订阅者回调 onNext 和 onCompleted

                WebServiceResultEntity result = webService.call();
                if (result.getSuccess()) {
                    if (subclass == String.class) {
                        subscriber.onNext(result.getResult());
                    } else if (subclass == List.class) {
                        subscriber.onNext(jsonParser(subclass, (String) result.getResult()));
                    } else if (subclass == Object.class) {
                        subscriber.onNext(jsonParserClass(subclass, (String) result.getResult()));
                    } else {
                        subscriber.onNext(jsonParserClass(subclass, (String) result.getResult()));
                    }

                    //subscriber.onNext(jsonParser(subclass, (String) result.getResult()));
                } else {
                    subscriber.onError(new Exception(result.getMsgInfo()));
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Object jsonParser(Type mType, String jsonStr) {
        Gson gson = new Gson();
        Object object = gson.fromJson(jsonStr, mType);
        return object;
    }

    public Object jsonParserClass(final Class<?> mtype, String jsonStr) {
        Gson gson = new Gson();
        Type listType = null;
        if (mtype.getName().equals(UserInfo.class.getName())) {
            listType = new TypeToken<ArrayList<UserInfo>>() {
            }.getType();//类型转一下
        }

      /*  if(mtype.getName().equals(TDBBoard.class.getName())){
            listType=new TypeToken<ArrayList<TDBBoard>>(){
            }.getType();
        }*/
        if (mtype.getName().equals(Object.class.getName())) {
            listType = new TypeToken<ArrayList<Object>>() {
            }.getType();
        }
        Object object = gson.fromJson(jsonStr, listType);
        return object;
    }

    /*
     * 关闭加载页面
     */
    public void closeParentDialog() {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }

    }


}
