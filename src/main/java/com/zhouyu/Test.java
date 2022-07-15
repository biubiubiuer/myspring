package com.zhouyu;

import com.spring.ZhouyuApplicationContext;

public class Test {
    public static void main(String[] args) {
        ZhouyuApplicationContext applicationContext = new ZhouyuApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));  // map <beanName, bean对象>
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
    }
}
