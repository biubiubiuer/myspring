package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class ZhouyuApplicationContext {

    private static final String SINGLETON = "singleton";

    private Class configClass;
    
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();  // 单例池
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ZhouyuApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 解析配置类
        // ComponentScan注解--->扫描路径--->扫描--->BeanDefinition--->BeanDefinitionMap
        scan(configClass);

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(SINGLETON)) {
                Object bean = createBean(beanDefinition);  // 单例Bean
                singletonObjects.put(beanName, bean);
            }
        }
    }
    
    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass) {
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value(); // 扫描路径
        System.out.println(path);
        path = path.replace(".", "/");

        // 扫描
        // Bootstrap--->jre/lib
        // Ext--------->jre/ext/lib
        // App--------->classpath
        ClassLoader classLoader = ZhouyuApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                System.out.println(f);

                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("/", ".");
                    System.out.println(className);

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            // 表示当前这个类是一个Bean
                            // ....? Class-->bean ?  No
                            // 解析类--->BeanDefinition, 判断当前bean是单例bean, 还是prototype的bean

                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope(SINGLETON);
                            }
                            
                            beanDefinitionMap.put(beanName, beanDefinition);
                            
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(SINGLETON)) {
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                // 创建Bean对象?
                Object bean = createBean(beanDefinition);
                return bean;
            }
        } else {
            // 不存在对应的Bean
            throw new NullPointerException("不存在对应的Bean");
        }
    }
}
