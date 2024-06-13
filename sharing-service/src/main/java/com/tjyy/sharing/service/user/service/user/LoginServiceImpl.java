package com.tjyy.sharing.service.user.service.user;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tjyy.sharing.api.exception.ExceptionUtil;
import com.tjyy.sharing.api.vo.constants.StatusEnum;
import com.tjyy.sharing.service.user.respository.dao.UserDao;
import com.tjyy.sharing.service.user.respository.entity.UserDO;
import com.tjyy.sharing.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Tjyy
 * @date: 2024-06-12 22:22
 * @description: 用户登录相关的服务实现类
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserDao userDao;

    @Override
    public void logout(String session) {
        // session 失效的操作
    }

    /**
     * 用户名密码方式 - 登陆
     * @param username
     * @param password
     * @return 登陆成功返回登陆成功的 JWT - Session
     */
    @Override
    public String loginByUserPwd(String username, String password) {
        // 主要就是校验密码是否和存在的密码相同即可
        UserDO user = userDao.getUserByUsername(username);
        if (user == null){
            // 报错异常，不存在该用户
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "username=" + username);
        }

        if(!user.getPassword().equals(password)){
            // 密码错误验证
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        Long userId = user.getId();
        // 登陆成功，生成对应的 JWT 并返回
        return "登陆成功";
    }

    /**
     * 用户名密码注册 - 若用户不存在，则进行注册
     * @param username
     * @param password
     * @return
     */
    @Override
    public String registerByUserPwd(String username, String password) {
        // 1.前置校验：判断用户名和密码不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        // 2.判断用户名是否已经被使用过
        UserDO user = userDao.getUserByUsername(username);
        if(user != null){
            throw ExceptionUtil.of(StatusEnum.USER_EXISTS);
        }

        // 3.用户注册流程 -> 直接登陆
        user = new UserDO();
        user.setUsername(username);
        user.setPassword(password);
        userDao.saveUser(user);

        return "注册成功";
    }
}
