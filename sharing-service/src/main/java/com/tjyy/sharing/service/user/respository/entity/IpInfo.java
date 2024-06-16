package com.tjyy.sharing.service.user.respository.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Tjyy
 * @date: 2024-06-13 23:38
 * @description: ip 信息 - JSON 格式
 */
@Data
public class IpInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstIp;

    private String firstRegion;

    private String latestIp;

    private String latestRegion;
}
