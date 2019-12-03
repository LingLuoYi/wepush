package com.wepush.wepush.utils;

import com.google.gson.Gson;
import com.wepush.wepush.entity.User;

import javax.servlet.http.HttpServletRequest;

public class LoginUserUtils {

    public static String loginUsername(HttpServletRequest request){
        return (String) request.getSession().getAttribute("user");
    }

    public static User loginUser(HttpServletRequest request){
        try {
            return new Gson().fromJson(RocksUtils.get(loginUsername(request)),User.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
