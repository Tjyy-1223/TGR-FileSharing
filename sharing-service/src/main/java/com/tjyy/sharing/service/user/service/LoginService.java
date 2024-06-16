package com.tjyy.sharing.service.user.service;

import com.tjyy.sharing.api.vo.user.req.UserPwdLoginReq;

/**
 * 登陆相关的服务接口
 */
public interface LoginService {
    String JWT_COOKIE_KEY = "f-jwt";
    String USER_DEVICE_KEY = "f-device";

    /**
     * 退出登陆
     * @param session
     */
    void logout(String session);

    /**
     * 根据用户名和密码进行登录
     * @param username
     * @param password
     * @return
     */
    String loginByUserPwd(String username, String password);

    /**
     * 根据用户名和密码进行注册
     * @param loginReq 输入的注册请求
     * @return
     */
    String registerByUserPwd(UserPwdLoginReq loginReq);
}
