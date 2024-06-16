package com.tjyy.sharing.api.context;

import com.tjyy.sharing.api.vo.seo.Seo;
import com.tjyy.sharing.api.vo.user.dto.BaseUserInfoDTO;
import lombok.Data;

import java.security.Principal;

/**
 * @author: Tjyy
 * @date: 2024-06-14 22:43
 * @description: 请求上下文，用于携带用户身份相关信息
 */
public class ReqInfoContext {
    private static ThreadLocal<ReqInfo> contexts = new InheritableThreadLocal<>();

    public static void addReqInfo(ReqInfo reqInfo){
        contexts.set(reqInfo);
    }

    public static void clear(){
        contexts.remove();
    }

    public static ReqInfo getReqInfo(){
        return contexts.get();
    }

    /**
     * ReqInfo 类：表示请求信息
     */
    @Data
    public static class ReqInfo implements Principal{
        /**
         * app - key
         */
        private String appKey;

        /**
         * 访问域名
         */
        private String host;

        /**
         * 访问路径
         */
        private String path;

        /**
         * 客户端 ip
         */
        private String clientIp;

        /**
         * referer
         */
        private String referer;

        /**
         * post 表单参数
         */
        private String payload;

        /**
         * 设备信息
         */
        private String userAgent;

        /**
         * 登录的会话 - session - jwt
         */
        private String session;

        /**
         * 用户id
         */
        private Long userId;

        /**
         * 用户信息 - UserInfo
         */
        private BaseUserInfoDTO user;

        /**
         * 消息数量
         */
        private Integer msgNum;

        private Seo seo;

        private String deviceId;

        @Override
        public String getName() {
            return session;
        }
    }
}
