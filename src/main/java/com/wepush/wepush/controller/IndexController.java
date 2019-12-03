package com.wepush.wepush.controller;

import com.google.gson.Gson;
import com.wepush.wepush.entity.User;
import com.wepush.wepush.service.IndexService;
import com.wepush.wepush.utils.LoginUserUtils;
import com.wepush.wepush.utils.RocksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private Gson gson;

    @Autowired
    private IndexService indexService;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request){
        model.addAttribute("user", gson.fromJson(RocksUtils.get(LoginUserUtils.loginUsername(request)), User.class));
        model.addAttribute("wxQrCode",indexService.qrCode(request));
        model.addAttribute("friends",indexService.friends());
        model.addAttribute("getGroup",indexService.getGroup());
        model.addAttribute("allPush",indexService.allPush());
        model.addAttribute("me",indexService.me());
        return "index";
    }


    @ResponseBody
    @RequestMapping("/isWxLogin")
    public String isLogin(HttpServletRequest httpServletRequest){
        return indexService.isWxLogin(httpServletRequest);
    }


    @ResponseBody
    @RequestMapping("/setPush")
    public String setPush(String type,String id){
        return indexService.setPush(type,id);
    }
}
