package com.tjyy.sharing.core.util;

import org.springframework.util.Assert;

/**
 * @author: Tjyy
 * @date: 2024-07-06 17:37
 * @description:
 */
public class EnvUtil {
    private static volatile EnvEnum env;

    public enum EnvEnum{
        DDEV("dev", false),
        TEST("test", false),
        PRE("pre", false),
        PROD("prod", true);

        private String env;
        private boolean prod;

        EnvEnum(String env, boolean prod){
            this.env = env;
            this.prod = prod;
        }

        public static EnvEnum nameOf(String name){
            for (EnvEnum env: values()){
                if(env.env.equalsIgnoreCase(name)){
                    return env;
                }
            }
            return null;
        }
    }

    public static boolean isPro() {
        return getEnv().prod;
    }

    public static EnvEnum getEnv() {
        if (env == null){
            synchronized (EnvUtil.class){
                if (env == null){
                   env = EnvEnum.nameOf(SpringUtil.getConfig("env.name"));
                }
            }
        }
        Assert.isTrue(env != null, "env.name环境配置必须存在!");
        return env;
    }


}
