package com.tjyy.sharing.api.exception;

import com.tjyy.sharing.api.vo.constants.StatusEnum;

/**
 * @author: Tjyy
 * @date: 2024-06-12 22:45
 * @description: 异常类 - 工具类
 */
public class ExceptionUtil {
    /**
     * 抛出自定义异常的工具类
     * @param status
     * @param args
     * @return
     */
    public static ForumException of(StatusEnum status, Object... args){
        return new ForumException(status, args);
    }
}
