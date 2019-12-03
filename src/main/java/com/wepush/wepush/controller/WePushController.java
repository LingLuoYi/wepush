package com.wepush.wepush.controller;

import com.wepush.wepush.entity.JsonModel;
import com.wepush.wepush.service.WePushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WePushController {

    @Autowired
    private WePushService pushService;

    @RequestMapping("/pushText")
    public JsonModel pushText(String type, String text){
        return pushService.pushText(type,text);
    }
}
