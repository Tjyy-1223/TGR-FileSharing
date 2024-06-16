package com.tjyy.sharing.api.enums.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 登陆方式枚举类
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    WECHAT(0),
    USER_PWD(1);

    private int type;
}
