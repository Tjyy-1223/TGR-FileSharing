package com.tjyy.sharing.service.user.respository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tjyy.sharing.api.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: Tjyy
 * @date: 2024-06-11 23:09
 * @description: 用户登录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 第三方用户ID
     */
    private String thirdAccountId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码，密文存储
     */
    private String password;

    /**
     * 登录方式: 0-微信登录，1-账号密码登录
     */
    private Integer loginType;

    /**
     * 删除标记
     */
    private Integer deleted;
}
