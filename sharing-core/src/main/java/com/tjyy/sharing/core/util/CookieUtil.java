package com.tjyy.sharing.core.util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: Tjyy
 * @date: 2024-06-15 23:30
 * @description: Cookie 相关工具类
 */
public class CookieUtil {
    private  static final int COOKIE_MAX_AGE = 60*60*24*30;

    /**
     * 在HTTP Cookie 中，可以通过设置 Cookie 的 path 属性来限制哪些路径下的请求可以发送该 Cookie。
     * 如果一个 Cookie 设置了 path=/blog，那么它只会在访问路径以 /blog 开头的请求中被发送到服务器。
     * @param key
     * @param value
     * @return
     */
    public static Cookie newCookie(String key, String value) {
        return newCookie(key, value, "/", COOKIE_MAX_AGE);
    }

    public static Cookie newCookie(String key, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static Cookie delCookie(String key) {
        return delCookie(key, "/");
    }

    public static Cookie delCookie(String key, String path) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        return cookie;
    }

    /**
     * 根据 key 查找对应的 cookie
     * @param request 请求
     * @param name 名字
     * @return
     */
    public static Cookie findCookieByName(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0){
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> StringUtils.equalsAnyIgnoreCase(cookie.getName(), name))
                .findFirst().orElse(null);
    }

    public static String findCookieByName(ServerHttpRequest request, String name){
        List<String> list = request.getHeaders().get("cookie");
        if (CollectionUtils.isEmpty(list)){
            return null;
        }

        for (String sub : list) {
            String[] elements = StringUtils.split(sub, ";");
            for (String element : elements) {
                String[] subs = StringUtils.split(element, "=");
                if (subs.length == 2 && StringUtils.equalsAnyIgnoreCase(subs[0].trim(), name)){
                    return subs[1].trim();
                }
            }
        }
        return null;
    }
}
