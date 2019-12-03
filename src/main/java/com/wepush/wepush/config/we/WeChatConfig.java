package com.wepush.wepush.config.we;

import me.xuxiaoxiao.chatapi.wechat.WeChatApi;
import me.xuxiaoxiao.chatapi.wechat.WeChatContacts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatConfig {

    @Bean(name = "weChatApi")
    public WeChatApi weChatApi(){
        return new WeChatApi();
    }

    @Bean(name = "weChatContacts")
    public WeChatContacts weChatContacts(){
         return new WeChatContacts();
    }
}
