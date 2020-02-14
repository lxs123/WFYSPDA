package com.cubertech.bhpda.utils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/4.
 */

public class ListUtils {

    public static int getSize(List list) {
        if (list == null || list.size() == 0) {
            return 0;
        } else {
            return list.size();
        }
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 格式 json=["好吧","好吧","好吧"...]
     *
     * @param json 一维数组转化成list
     * @return
     */

    public static List toList(String json) {
        List list = new ArrayList();
        if (TextUtils.isEmpty(json)) {
            return list;
        }
        try {
//            JSONArray jsonArray = new JSONArray(json);
//            if (jsonArray != null) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    list.add(jsonArray.get(i).toString());
//                }
//            }
            String replace = json.replace("[", "").replace("]", "");
            String[] split = replace.split(",");
            for (int i = 0; i < split.length; i++) {
                list.add(split[i]);
            }
        } catch (Exception e) {
            Log.e("##list", e.toString());
        }


        return list;
    }
}
