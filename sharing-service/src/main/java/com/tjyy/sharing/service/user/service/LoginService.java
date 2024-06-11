package com.tjyy.sharing.service.user.service;

/**
 * 登陆相关的服务接口
 */
public interface LoginService {
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
     * @param username
     * @param password
     * @return
     */
    String registerByUserPwd(String username, String password);
}
