package com.laqun.laqunserver.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class config {
    static JSONObject conJo = null;

    public static void init() {
        conJo = JSON.parseObject(utils.rlFromF("globalConf.json"));
        if (conJo == null) {
            conJo = new JSONObject();
        }
    }

    public static String get(String key) {
        if (conJo == null) {
            init();
        }
        return conJo.getString(key) == null ?  "": conJo.getString(key);

    }

    public static void set(String key, String val) {
        if (conJo == null) {
            init();
        }
        conJo.put(key, val);
        utils.wlToF("globalConf.json", conJo.toString());
    }
}
