package com.wepush.wepush.service;

import com.google.gson.Gson;
import com.wepush.wepush.entity.User;
import com.wepush.wepush.utils.RocksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Service
public class LoginService {

    @Autowired
    private Gson gson;

    public String login(HttpServletRequest httpServletRequest,String username, String password){
        String userData =  RocksUtils.get(username);
        if (userData == null){
            return "当前用户不存在";
        }
        try {
            User user = gson.fromJson(userData,User.class);
            if (user.getPassword().equals(password)){
                HttpSession httpSession = httpServletRequest.getSession();
                httpSession.setAttribute("user",user.getUsername());
                return "登录成功";
            }else {
                return "密码错误";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "获取用户信息失败";
        }
    }
}
