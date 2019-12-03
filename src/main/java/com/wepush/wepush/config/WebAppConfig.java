package com.wepush.wepush.config;

import com.google.gson.Gson;
import com.wepush.wepush.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截的管理器
        InterceptorRegistration registration = registry.addInterceptor(loginHandler());     //拦截的对象会进入这个类中进行判断
        registration.addPathPatterns("/**");                    //所有路径都被拦截
        registration.excludePathPatterns("/isWxLogin");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/layui/**");
        registration.excludePathPatterns("/login");//添加不拦截路径
    }

    @Bean
    public LoginHandler loginHandler(){
        return new LoginHandler();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
