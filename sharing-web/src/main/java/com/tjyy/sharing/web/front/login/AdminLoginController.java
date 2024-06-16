package com.tjyy.sharing.web.front.login;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tjyy.sharing.api.vo.ResVo;
import com.tjyy.sharing.api.vo.constants.StatusEnum;
import com.tjyy.sharing.api.vo.user.req.UserPwdLoginReq;
import com.tjyy.sharing.core.util.CookieUtil;
import com.tjyy.sharing.service.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: Tjyy
 * @date: 2024-06-11 21:37
 * @description: 用户注册 - 登录相关接口
 */
@RestController
@Api(value = "用户注册-登陆-退出控制器", tags = "用户登陆退出")
@RequestMapping
public class AdminLoginController {
    private static final Logger log = LoggerFactory.getLogger(AdminLoginController.class);
    @Autowired
    private LoginService loginService;

    /**
     * 用户名 - 密码的方式登录
     * @param username
     * @param password
     * @return
     */
    @ApiOperation("用户登陆")
    @GetMapping(path = "/login/username")
    public ResVo<Boolean> login(@RequestParam(value = "username") String username,
                             @RequestParam(value = "password") String password,
                             HttpServletResponse response) {
        String jwtToken = loginService.loginByUserPwd(username, password);
        if (StringUtils.isNotBlank(jwtToken)) {
            // cookie 中写入用户登陆信息，用于身份识别
            response.addCookie(CookieUtil.newCookie(LoginService.JWT_COOKIE_KEY, jwtToken));
            return ResVo.success(true);
        }else{
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "用户名和密码登录异常，请稍后重试");
        }
    }

    /**
     * 用户名 - 密码 注册方式
     * @param loginReq 登陆请求
     * @param response 响应
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping(path = "/login/register")
    public ResVo<Boolean> register(@RequestBody UserPwdLoginReq loginReq,
                                   HttpServletResponse response){
        String jwtToken = loginService.registerByUserPwd(loginReq);
        if (StringUtils.isNotBlank(jwtToken)) {
            response.addCookie(CookieUtil.newCookie(LoginService.JWT_COOKIE_KEY, jwtToken));
            return ResVo.success(true);
        }else{
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "注册失败，请重试");
        }
    }


    /**
     * 用户注销
     * @param response 注销 jwt
     * @return
     */
    @GetMapping("/logout")
    public ResVo<Boolean> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 释放会话
        request.getSession().invalidate();
        // 移除cookie
        response.addCookie(CookieUtil.delCookie(LoginService.JWT_COOKIE_KEY));
        // 重定向到当前页面
        String referer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referer)) {
            referer = "/";
        }
        response.sendRedirect(referer);
        return ResVo.success(true);
    }

}
