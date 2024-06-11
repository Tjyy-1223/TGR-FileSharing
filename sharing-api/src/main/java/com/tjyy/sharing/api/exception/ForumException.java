package com.tjyy.sharing.api.exception;

import com.tjyy.sharing.api.vo.Status;
import com.tjyy.sharing.api.vo.constants.StatusEnum;
import lombok.Getter;

/**
 * @author: Tjyy
 * @date: 2024-06-12 22:46
 * @description: 自定义业务异常
 */
public class ForumException extends RuntimeException{
    @Getter
    private Status status;

    public ForumException(Status status){
        this.status = status;
    }

    public ForumException(int code, String msg){
        this.status = Status.newStatus(code, msg);
    }

    public ForumException(StatusEnum statusEnum, Object... args){
        this.status = Status.newStatus(statusEnum, args);
    }
}
