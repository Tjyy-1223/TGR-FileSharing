package com.tjyy.sharing.api.vo.user.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Tjyy
 * @date: 2024-06-15 17:31
 * @description: 基于用户名密码登录的相关请求信息
 */
@Data
@Accessors(chain = true)
public class UserPwdLoginReq {
    private static final long serialVersionUID = 1L;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 登陆用户名
     */
    private String username;

    /**
     * 登陆密码
     */
    private String password;

}
