package com.cubertech.bhpda.connect;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import com.cubertech.bhpda.connect.encrypt.AESUtilMy;
import com.cubertech.bhpda.connect.encrypt.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*import com.cubertech.dbconstruction.connect.encrypt.AESUtilMy;
import com.cubertech.dbconstruction.connect.encrypt.DigestUtil;*/

/**
 * 调取Webservice统一入口，对参数和header的封装与加密
 */

public class WebService {
    private HashMap<String, Object> params;
    private String methodName;
    private SoapObject soapObject;
    private String url;
    private String headerData;
    private String paramsData;

    private SimpleArrayMap paramsDataMap;

    //private String keyForHmac = "574584H38Msx80980026QKzbX588Zv0xh95ph8ZG67dj7x69k5091xvm0013";
    // AESUtil加密与解密的密钥，截取商户密钥的前16位
    //private String keyForAes = keyForHmac.substring(0, 16);

    private String KeyForHmac="tjcubertechyinlongjituanWLG01Zv0xh95ph8ZG67dj7";
    private String keyForAes = KeyForHmac.substring(0, 16);

    public WebService(String url, HashMap<String, Object> params, String methodName) {
        this.url = url;
        this.params = params;
        this.methodName = methodName;
    }

    public WebServiceResultEntity call() {
        headerData = makeUpHeaderDate();
        paramsData = encryptParams();
        Log.i("observeOn","ddddd");

        //执行第一遍
        soapObject = LastestWebServiceUtils.callWebService(
                url, methodName, paramsData, "http://www.tjcubertech.com/", headerData);
        Log.i("observeOn", "url:"+url+";");

        if (soapObject != null) {

            if(soapObject.getName().equals("error")&&soapObject.getNamespace().equals("error")){
                //执行第二遍
                soapObject = LastestWebServiceUtils.callWebService(
                        url, methodName, paramsData, "http://www.tjcubertech.com/", headerData);
                if (soapObject != null) {
                    String jsonStr = soapObject.getProperty(methodName + "Result")
                            .toString();
                    Log.i("observeOn", jsonStr);
                    WebServiceResultEntity resultEntity = new WebServiceResultEntity();
                    try {
                        JSONObject dataObject = new JSONObject(jsonStr);

                        try {
                            resultEntity.setResult(dataObject.getString("Entity"));
                        } catch (JSONException e) {
                            resultEntity.setResult(dataObject.getJSONArray("Entity").toString());
                        }
                        resultEntity.setSuccess(dataObject.getBoolean("Success"));
                        resultEntity.setTotal(dataObject.getInt("Total"));
                        resultEntity.setMsgInfo(dataObject.getString("MsgInfo"));
                        resultEntity.setObj(dataObject.getString("Obj"));

                    } catch (JSONException e) {
                        resultEntity.setSuccess(false);
                        resultEntity.setMsgInfo("错误代码：1003，返回值格式错误");
                    }
                    return resultEntity;
                }
            }else{
                String jsonStr = soapObject.getProperty(methodName + "Result")
                        .toString();
                Log.i("observeOn", jsonStr);
                WebServiceResultEntity resultEntity = new WebServiceResultEntity();
                try {
                    JSONObject dataObject = new JSONObject(jsonStr);

                    try {
                        resultEntity.setResult(dataObject.getString("Entity"));
                    } catch (JSONException e) {
                        resultEntity.setResult(dataObject.getJSONArray("Entity").toString());
                    }
                    resultEntity.setSuccess(dataObject.getBoolean("Success"));
                    resultEntity.setTotal(dataObject.getInt("Total"));
                    resultEntity.setMsgInfo(dataObject.getString("MsgInfo"));
                    resultEntity.setObj(dataObject.getString("Obj"));

                } catch (JSONException e) {
                    resultEntity.setSuccess(false);
                    resultEntity.setMsgInfo("错误代码：1003，返回值格式错误");
                }
                return resultEntity;
            }




        }

        WebServiceResultEntity resultEntity = new WebServiceResultEntity();
        resultEntity.setSuccess(false);
        resultEntity.setMsgInfo("错误代码：9001，连接服务器失败");
        return resultEntity;
    }

    public String makeUpHeaderDate() {

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("Name", "www.tjcubertech.com");
        dataMap.put("Password", "tjcubertechWLG");
        dataMap.put("Version", "1.0");
        dataMap.put("Times", new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date()));
        dataMap.put("ActionType", "1001");
        Gson gson = new Gson();
        Type listType = new TypeToken<Map<String, String>>() {
        }.getType();
        String dataJsonString = gson.toJson(dataMap, listType);
        String hmac= DigestUtil.hmacSign(dataJsonString, KeyForHmac);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("Mac", hmac);
            jsonObject.put("Data", dataJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data=null;
        Log.i("请求header:", jsonObject.toString());
        try {
            data= AESUtilMy.encrypt(jsonObject.toString(),keyForAes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public String encryptParams()  {
        String data = null;
        if (params != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<Map<String, String>>() {
            }.getType();
            String dataJsonString = gson.toJson(params, listType);
            String hmac= DigestUtil.hmacSign(dataJsonString, KeyForHmac);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("Mac", hmac);
                jsonObject.put("Data", dataJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("请求params:", jsonObject.toString());
            try {
                data=AESUtilMy.encrypt(jsonObject.toString(),keyForAes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }




    /*public WebServiceResultEntity call()  {
        headerData = makeUpHeaderDate();
        paramsData = encryptParams();
        soapObject = LastestWebServiceUtils.callWebService(
                url, methodName, paramsData, "http://open.tiniban.cn/", headerData);
        if (soapObject != null) {
            String jsonStr = soapObject.getProperty(methodName + "Result")
                    .toString();
            Log.i("observeOn", jsonStr);
            WebServiceResultEntity resultEntity = new WebServiceResultEntity();
            try {
                JSONObject object = new JSONObject(jsonStr);
                if (object.getBoolean("IFAes")) {
                    //不加密
                    jsonStr = object.getString("Data");
                } else {
                    //加密
                    //jsonStr = AESUtil.decrypt(object.getString("Data"), keyForAes);
                }
                JSONObject dataObject = new JSONObject(jsonStr);
                try {
                    resultEntity.setResult(dataObject.getString("Entity"));
                } catch (JSONException e) {
                    resultEntity.setResult(dataObject.getJSONArray("Entity").toString());
                }
                resultEntity.setSuccess(dataObject.getBoolean("Success"));
                resultEntity.setTotal(dataObject.getInt("Total"));
                resultEntity.setMsgInfo(dataObject.getString("MsgInfo"));
                resultEntity.setObj(dataObject.getString("Obj"));

            } catch (JSONException e) {
                resultEntity.setSuccess(false);
                resultEntity.setMsgInfo("错误代码：1003，返回值格式错误");
            }
            return resultEntity;
        }
        WebServiceResultEntity resultEntity = new WebServiceResultEntity();
        resultEntity.setSuccess(false);
        resultEntity.setMsgInfo("错误代码：1001，连接服务器失败");
        return  resultEntity;
    }

    public String makeUpHeaderDate() {

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("Name", "www.tiniban.cn918");
        dataMap.put("Password", "www.wgao.cn918");
        dataMap.put("Version", "1.0");
        dataMap.put("Times", new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date()));
        dataMap.put("ActionType", "1001");
        Gson gson = new Gson();
        Type listType = new TypeToken<Map<String, String>>() {
        }.getType();
        String dataJsonString = gson.toJson(dataMap, listType);
        String hmac = Digest.hmacSign(dataJsonString, keyForHmac);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("Mac", hmac);
            jsonObject.put("Data", dataJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // String data = AESUtil.encrypt(jsonObject.toString(), keyForAes);
        //return data;
        return "";
    }

    public String encryptParams() {
        String data = null;
        if (params != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<Map<String, String>>() {
            }.getType();
            String dataJsonString = gson.toJson(params, listType);
            String hmac = Digest.hmacSign(dataJsonString, keyForHmac);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("Mac", hmac);
                jsonObject.put("Data", dataJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("请求params:", jsonObject.toString());
            //data = AESUtil.encrypt(jsonObject.toString(), keyForAes);
        }
        return data;
    }*/


}
