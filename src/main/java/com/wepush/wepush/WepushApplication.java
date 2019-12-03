package com.wepush.wepush;

import com.google.gson.Gson;
import com.wepush.wepush.config.Config;
import com.wepush.wepush.entity.User;
import com.wepush.wepush.utils.RocksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import sun.plugin2.util.PojoUtil;

import java.util.Scanner;

@SpringBootApplication
public class WepushApplication {



    public static ConfigurableApplicationContext ac;

    public static void main(String[] args) {
        WepushApplication.ac = SpringApplication.run(WepushApplication.class, args);
        //启动完成后
        String restart = RocksUtils.get(Config.RESTART);
        if (restart == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("第一次启动，请创建一个初始用户");
            System.out.println("---------------------------------");
            while (true) {
                System.out.println("请输入用户名：");
                String username = scanner.nextLine();
                System.out.println("请输入密码：");
                String password = scanner.nextLine();
                System.out.println("请确认下面信息");
                System.out.println("用户名：" + username + "密码：" + password);
                System.out.println("继续请输入Y，重新输入请用力砸键盘");
                String state = scanner.nextLine();
                if ("Y".equalsIgnoreCase(state)) {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    RocksUtils.set(username, new Gson().toJson(user));
                    RocksUtils.set(Config.RESTART,"0");
                    System.out.println("添加成功，现在可以登录啦");
                    break;
                }
            }
        }else {
            int start = Integer.parseInt(RocksUtils.get(Config.RESTART)) + 1;
            RocksUtils.set(Config.RESTART,String.valueOf(start));
        }
    }


}
