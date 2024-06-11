package com.tjyy.sharing.api.vo.constants;

import lombok.Getter;

/**
 * @author: Tjyy
 * @date: 2024-06-12 23:02
 * @description: 定义异常码规范
 * 异常码规范:
 * xxx - xxx - xxx : 业务 - 状态 - code
 * 业务：
 * - 100 全局业务
 * - 200 文件相关业务
 * - 300 分享相关业务
 * - 400 用户相关业务
 * 状态：基于 http status 含义进行状态
 * - 4xx 调用方使用问题
 * - 5xx TGR 文件内部问题
 * code：具体的业务 code
 */
@Getter
public enum StatusEnum {
    SUCCESS(0, "OK"),

    // 用户相关异常
    LOGIN_FAILED_MIXED(400_403_001, "登陆失败:%s"),
    USER_NOT_EXISTS(400_404_001, "用户不存在:%s"),
    USER_EXISTS(400_404_002, "用户已存在:%s"),
    USER_LOGIN_NAME_REPEAT(400_404_003, "用户登录名重复:%s"),
    USER_PWD_ERROR(400_500_002, "用户名 or 密码错误");

    private int code;
    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static boolean is5xx(int code){
        return code % 1000_000 / 1000 >= 500;
    }

    public static boolean is403(int code){
        return code % 1000_000 / 1000 == 403;
    }

    public static boolean is4xx(int code){
        return code % 1000_000 / 1000 < 500;
    }
}
