package com.tjyy.sharing.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Tjyy
 * @date: 2024-06-13 21:10
 * @description: MybatisPlus-Mapper 自动扫描
 */
@Configuration
// @ComponentScan("com.tjyy.sharing.service")
@MapperScan(basePackages = {
        "com.tjyy.sharing.service.user.respository.mapper",
})
public class ServiceAutoConfig {
}
