package com.tjyy.sharing.web.global;

import com.tjyy.sharing.api.exception.ForumException;
import com.tjyy.sharing.api.vo.ResVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: Tjyy
 * @date: 2024-06-14 21:42
 * @description: 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理器,对于抛出的异常进行 fail 封装后返回
     * @param e 抛出的异常
     * @return
     */
    @ExceptionHandler(value = ForumException.class)
    public ResVo<String> handleForumException(ForumException e){
        return ResVo.fail(e.getStatus());
    }
}
