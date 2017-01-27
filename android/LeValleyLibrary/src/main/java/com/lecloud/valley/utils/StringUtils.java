package com.lecloud.valley.utils;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by LizaRao on 2017/1/27.
 */

public class StringUtils {

    public static WritableMap convertJsonStrToWriteMap(String jsonStr) {
        WritableMap map = Arguments.createMap();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator<String> keys = jsonObject.keys();
            String key;
            while (keys.hasNext()) {
                key = keys.next();
                map.putString(key, jsonObject.get(key).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, LogUtils.getTraceInfo() + "JsonStr转换Map失败：" + e);
        }
        return map;
    }


    public static String convertReadMapToJsonStr(ReadableMap map) {
        String jsonStr = null;
        try {
            JSONObject jsonObject = new JSONObject(map.toString());
            String key = "NativeMap";
            JSONObject json = jsonObject.getJSONObject(key);
            jsonStr = json.toString();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, LogUtils.getTraceInfo() + "Map转换JsonStr失败：" + e);
        }
        return jsonStr;
    }
}
