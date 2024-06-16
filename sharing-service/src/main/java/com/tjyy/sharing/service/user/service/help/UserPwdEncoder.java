package com.tjyy.sharing.service.user.service.help;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author: Tjyy
 * @date: 2024-06-14 21:56
 * @description: 对用户密码进行加密操作
 *
 */
@Component
public class UserPwdEncoder {
    @Value("${security.salt}")
    private String salt;
    @Value("${security.salt-index}")
    private Integer saltIndex;

    /**
     * 判断输入密码和加密后的密码是否相同
     * @param plainPwd
     * @param encodedPwd
     * @return
     */
    public boolean match(String plainPwd, String encodedPwd) {
        return Objects.equals(encodePwd(plainPwd), encodedPwd);
    }

    /**
     * 对密码进行明文处理
     * @param plainPwd 明文密码
     * @return
     */
    public String encodePwd(String plainPwd){
        if (plainPwd.length() > saltIndex){
            plainPwd = plainPwd.substring(0, saltIndex) + salt + plainPwd.substring(saltIndex);
        }else{
            plainPwd = plainPwd + salt;
        }
        return DigestUtils.md5DigestAsHex(plainPwd.getBytes(StandardCharsets.UTF_8));
    }
}
