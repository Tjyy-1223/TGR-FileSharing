package com.tjyy.sharing.core.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author: Tjyy
 * @date: 2024-07-06 17:56
 * @description:
 */
@Component
public class SpringUtil implements ApplicationContextAware, EnvironmentAware {
    // context 和 environment 利用 @Component 注解自动注入
    @Getter
    private volatile static ApplicationContext context;
    private volatile static Environment environment;
    @Getter
    private static Binder binder;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringUtil.environment = environment;
    }

    // 获取 Bean
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static Object getBeanOrNull(String beanName) {
        try {
            return context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    // 获取配置
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    public static String getConfigOrElse(String mainKey, String slaveKey) {
        String ans = environment.getProperty(mainKey);
        if (ans == null) {
            return environment.getProperty(slaveKey);
        }
        return ans;
    }

    // 带默认值的获取配置
    public static String getConfig(String key, String val) {
        return environment.getProperty(key, val);
    }


    // 发布事件消息
    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }
}
