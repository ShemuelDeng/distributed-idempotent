package com.shemuel.idempotent.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public class SpringContextHolder implements ApplicationContextAware {
 
    private static ApplicationContext applicationContext;
 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }
 
    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }
 
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }
 
    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }
 
    private static void assertApplicationContext() {
        if (SpringContextHolder.applicationContext == null) {
            throw new RuntimeException("ApplicationContext属性为null,请检查是否注入了SpringContextHolder!");
        }
    }
 
}