package com.cubertech.bhpda.connect.retrofits;

import android.util.Log;

import com.cubertech.bhpda.connect.encrypt.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DataHelper {

    private static String KeyForHmac="tjcubertechyinlongjituanWLG01Zv0xh95ph8ZG67dj7";
    private static String keyForAes = KeyForHmac.substring(0, 16);

    public static String encryptParams(HashMap<String, Object> params)  {
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
}
