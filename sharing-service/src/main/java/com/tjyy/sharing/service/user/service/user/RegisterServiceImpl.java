package com.tjyy.sharing.service.user.service.user;

import com.tjyy.sharing.api.enums.user.LoginTypeEnum;
import com.tjyy.sharing.api.vo.user.req.UserPwdLoginReq;
import com.tjyy.sharing.service.user.respository.dao.UserDao;
import com.tjyy.sharing.service.user.respository.dao.UserInfoDao;
import com.tjyy.sharing.service.user.respository.entity.UserDO;
import com.tjyy.sharing.service.user.respository.entity.UserInfoDO;
import com.tjyy.sharing.service.user.service.RegisterService;
import com.tjyy.sharing.service.user.service.help.UserPwdEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Tjyy
 * @date: 2024-06-15 22:54
 * @description: 用户注册相关服务实现
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    /**
     * 根据用户名和密码进行注册
     * @param loginReq 登陆请求
     * @return userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByUserNameAndPassword(UserPwdLoginReq loginReq) {
        // 保存用户登录信息
        UserDO userDO = new UserDO();
        userDO.setUsername(loginReq.getUsername());
        userDO.setPassword(userPwdEncoder.encodePwd(loginReq.getPassword()));
        userDO.setThirdAccountId("");
        userDO.setLoginType(LoginTypeEnum.USER_PWD.getType());
        userDao.saveUser(userDO);

        // 保存用户信息
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUserId(userDO.getId());
        userInfoDO.setUsername(loginReq.getUsername());
        userInfoDao.save(userInfoDO);

        processAfterUserRegister(userDO.getId());
        return userDO.getId();
    }

    /**
     * 用户注册之后会出发的动作 - 事件驱动 - 逐层(之后补充)
     * @param userId 用户 - id
     */
    private void processAfterUserRegister(Long userId){

    }
}
