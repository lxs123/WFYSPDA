package com.cubertech.bhpda.connect.retrofits;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cubertech.bhpda.connect.encrypt.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ServiceUtils {
    public static ServiceUtils instance = null;

    public ProgressDialog progressDialog;
    private CompositeSubscription mCompositeSubscription = null;
    private RetrofitService mRetrofitService = null;

    public static ServiceUtils getInstance() {
        if (instance == null) {
            instance = new ServiceUtils();
        }
        return instance;
    }

    public void callService(Context context, String url, HashMap<String, Object> params, boolean controlbool, final ServiceCallBack
            serviceCallBack) {
        if (!(null == progressDialog)) {
            progressDialog.dismiss();
        }

        ConnWebService connWebService = new ConnWebService(context);

        if (controlbool) {
            progressDialog = ProgressDialog.show(context, "", "正在加载....",
                    false, false);
        }
//lxs 注释,url可能变换,不能只用一次
        //wlg修改
        if (null == mRetrofitService || !TextUtils.equals(connWebService.getUrl(), RetrofitHelper.urlRecord)) {
            mRetrofitService = RetrofitHelper.getInstance(connWebService.getUrl()).getServer();
        }
        if (null == mCompositeSubscription) {
            mCompositeSubscription = new CompositeSubscription();
        }
        String str = DataHelper.encryptParams(params);

        mCompositeSubscription.add(
                //mRetrofitService.Mytest(url, str)
                mRetrofitService.JQ(url, str)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {

                            @Override
                            public void onCompleted() {
                                serviceCallBack.onCompleted();
                                /*if (!(null == progressDialog)) {
                                    progressDialog.dismiss();
                                }*/

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("####",e.toString());
                                String es = CustomException.handleException(e);
                                serviceCallBack.onError(es);
                                /*if (!(null == progressDialog)) {
                                    progressDialog.dismiss();
                                }*/
                            }

                            @Override
                            public void onNext(String response) {
                                //处理一些解密数据
                                //包含“{”和“}”的数据为未加密，否则为已经加密
                                String responses="";
                                if(response.indexOf('{')>=0&&response.indexOf('}')>=0){
                                    responses=response;
                                }else{
                                    String KeyForHmac="tjcubertechyinlongjituanWLG01Zv0xh95ph8ZG67dj7";
                                    String keyForAes = KeyForHmac.substring(0, 16);

                                    //AESUtilMy.encrypt(jsonObject.toString(),keyForAes);
                                    try {
                                        String r=AESUtilMy.decrypt(response,keyForAes);//解密response//
                                        JSONObject jsonObject= JSON.parseObject(r);
                                        String Data = jsonObject.getString("Data");
                                        String Mac = jsonObject.getString("Mac");
                                        String hmacs= DigestUtil.hmacSign(Data, KeyForHmac);
                                        if(hmacs.equals(Mac)){
                                            responses=Data;
                                        }else{
                                            serviceCallBack.onFailure("数据传输错误");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                ServiceResultEntity resultEntity = HandleResult.handleData(responses);
                                if (resultEntity.getSuccess()) {
                                    Object o = jsonParserClass(Object.class, (String) resultEntity.getResult());
                                    serviceCallBack.onResponse(o);
                                } else {
                                    String s = resultEntity.getMsgInfo();
                                    serviceCallBack.onFailure(s);
                                }
                            }
                        })
        );
    }

    public interface ServiceCallBack {
        //请求成功时回调
        void onResponse(Object o);

        //请求失败时回调
        void onFailure(String str);

        //请求异常时候回调
        void onError(String error);

        //结束
        void onCompleted();
        //注意 onError onCompleted 不能同时执行即只有一个被执行到

    }

    public Object jsonParserClass(final Class<?> mtype, String jsonStr) {
        Gson gson = new Gson();
        Type listType = null;

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
