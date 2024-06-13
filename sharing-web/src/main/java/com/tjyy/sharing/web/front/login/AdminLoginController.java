package com.tjyy.sharing.web.front.login;

import com.tjyy.sharing.api.vo.ResVo;
import com.tjyy.sharing.api.vo.constants.StatusEnum;
import com.tjyy.sharing.service.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Tjyy
 * @date: 2024-06-11 21:37
 * @description: 用户注册 - 登录相关接口
 */
@RestController
@Api(value = "用户注册-登陆-退出控制器", tags = "用户登陆退出")
@RequestMapping
public class AdminLoginController {
    @Autowired
    private LoginService loginService;

    /**
     * 用户名 - 密码的方式登录
     * @param username
     * @param password
     * @return
     */
    @ApiOperation("用户登陆")
    @PostMapping(path = "/login/username")
    public ResVo<String> login(@RequestParam(value = "username") String username,
                               @RequestParam(value = "password") String password) {
        String msg = loginService.loginByUserPwd(username, password);
        if (msg != null){
            return ResVo.success(msg);
        }else{
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "登陆失败，请重试");
        }
    }

    /**
     * 用户名 - 密码 注册方式
     * @param username
     * @param password
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping(path = "/login/register")
    public ResVo<String> register(@RequestParam(value = "username") String username,
                                   @RequestParam(value = "password") String password){
        String msg = loginService.registerByUserPwd(username, password);
        if (msg != null){
            return ResVo.success(msg);
        }else{
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "注册失败，请重试");
        }
    }


}
