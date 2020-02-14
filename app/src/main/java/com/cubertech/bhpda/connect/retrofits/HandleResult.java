package com.cubertech.bhpda.connect.retrofits;

import org.json.JSONException;
import org.json.JSONObject;

public class HandleResult {
    public static ServiceResultEntity handleData(String response) {
        ServiceResultEntity resultEntity = new ServiceResultEntity();
        try {
            JSONObject dataObject = new JSONObject(response);

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
