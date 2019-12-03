package com.wepush.wepush.utils;

import com.wepush.wepush.entity.JsonModel;

public class JsonUtils {

    public static JsonModel success(){
        JsonModel jsonModel = new JsonModel();
        jsonModel.setCode(0);
        jsonModel.setMsg("成功");
        return jsonModel;
    }

    public static JsonModel<Object> success(Object data){
        JsonModel<Object> jsonModel = new JsonModel<>();
        jsonModel.setCode(0);
        jsonModel.setMsg("成功");
        jsonModel.setData(data);
        return jsonModel;
    }

    public static JsonModel error(String msg){
        JsonModel jsonModel = new JsonModel();
        jsonModel.setCode(-1);
        jsonModel.setMsg(msg==null?"失败":msg);
        return jsonModel;
    }
}
