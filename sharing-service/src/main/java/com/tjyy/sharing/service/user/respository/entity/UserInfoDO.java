package com.tjyy.sharing.service.user.respository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.tjyy.sharing.api.entity.BaseDO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: Tjyy
 * @date: 2024-06-13 23:21
 * @description: 用户个人信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_info", autoResultMap = true)
public class UserInfoDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户图像
     */
    private String photo;

    /**
     * 职位
     */
    private String position;

    /**
     * 公司
     */
    private String company;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 0 普通用户
     * 1 管理员用户
     */
    private Integer userRole;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * ip信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IpInfo ip;

    public IpInfo getIp(){
        if (ip == null){
            ip = new IpInfo();
        }
        return ip;
    }
}
