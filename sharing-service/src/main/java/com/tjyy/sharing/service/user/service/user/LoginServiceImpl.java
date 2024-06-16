package com.tjyy.sharing.service.user.service.user;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tjyy.sharing.api.context.ReqInfoContext;
import com.tjyy.sharing.api.enums.user.LoginTypeEnum;
import com.tjyy.sharing.api.exception.ExceptionUtil;
import com.tjyy.sharing.api.vo.constants.StatusEnum;
import com.tjyy.sharing.api.vo.user.req.UserPwdLoginReq;
import com.tjyy.sharing.service.user.respository.dao.UserDao;
import com.tjyy.sharing.service.user.respository.dao.UserInfoDao;
import com.tjyy.sharing.service.user.respository.entity.UserDO;
import com.tjyy.sharing.service.user.respository.entity.UserInfoDO;
import com.tjyy.sharing.service.user.service.LoginService;
import com.tjyy.sharing.service.user.service.RegisterService;
import com.tjyy.sharing.service.user.service.help.UserPwdEncoder;
import com.tjyy.sharing.service.user.service.help.UserSessionHelper;
import lombok.experimental.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Override
    public void logout(String jwtToken) {
        userSessionHelper.removeJwtToken(jwtToken);
    }

    /**
     * 用户名密码方式 - 登陆
     * @param username 用户名
     * @param password 密码
     * @return 登陆成功返回登陆成功的 JWT - token
     */
    @Override
    public String loginByUserPwd(String username, String password) {
        // 主要就是校验密码是否和存在的密码相同即可
        UserDO user = userDao.getUserByUsername(username);
        if (user == null){
            // 报错异常，不存在该用户 - 使用全局异常处理器处理异常
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "username=" + username);
        }

        if(!userPwdEncoder.match(password, user.getPassword())){
            // 密码错误验证
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        Long userId = user.getId();
        // 登陆成功，返回对应的 jwt Token
        // ReqInfoContext.getReqInfo().setUserId(userId);
        return userSessionHelper.genJwtToken(userId);
    }

    /**
     * 用户名密码注册 - 逻辑操作
     * @param loginReq 输入的注册请求
     * @return
     */
    @Override
    public String registerByUserPwd(UserPwdLoginReq loginReq) {
        // 1.前置校验：判断用户名和密码不能为空
        if (StringUtils.isBlank(loginReq.getUsername()) || StringUtils.isBlank(loginReq.getPassword())){
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        // 2.判断用户名是否已经被使用过
        UserDO user = userDao.getUserByUsername(loginReq.getUsername());
        if(user != null){
            // 用户名已经存在
            throw ExceptionUtil.of(StatusEnum.USER_EXISTS, loginReq.getUsername());
        }

        // 3.用户注册流程 -> 直接登陆
        Long userId = registerService.registerByUserNameAndPassword(loginReq);
        return userSessionHelper.genJwtToken(userId);
    }

}
