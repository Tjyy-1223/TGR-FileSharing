package com.tjyy.sharing.service.user.service;

import com.tjyy.sharing.api.vo.user.req.UserPwdLoginReq;

/**
 * 用户注册服务接口
 */
public interface RegisterService {
    /**
     * 根据用户名和密码进行注册
     * @param loginReq 登陆请求
     * @return userId
     */
    Long registerByUserNameAndPassword(UserPwdLoginReq loginReq);
}
