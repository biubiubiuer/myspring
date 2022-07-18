package com.zhouyu;

import com.spring.ZhouyuApplicationContext;
import com.zhouyu.service.UserService;
import com.zhouyu.service.UserServiceImpl;

public class Test {
    public static void main(String[] args) {
        ZhouyuApplicationContext applicationContext = new ZhouyuApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));  // map <beanName, bean对象>
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));

        System.out.println("--------------------------------------------------------------------");
        
        
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();  // 1. 代理对象     2. 业务test
    }
}
