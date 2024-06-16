package com.tjyy.sharing.service.user.service.help;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tjyy.sharing.core.mdc.SelfTraceIdGenerator;
import com.tjyy.sharing.core.util.JsonUtil;
import com.tjyy.sharing.core.util.MapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Date;
import java.util.Map;

/**
 * @author: Tjyy
 * @date: 2024-06-15 10:21
 * @description: 使用 jwt 来进行用户登陆
 */
@Slf4j
@Component
public class UserSessionHelper {
    @Component
    @Data
    @ConfigurationProperties("tgr-sharing.jwt")
    public static class JwtProperties{
        /**
         * 签发人
         */
        private String issuer;
        /**
         * 密钥
         */
        private String secret;
        /**
         * 有效期，毫秒时间戳
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    public UserSessionHelper(JwtProperties jwtProperties){
        // 将构造函数中传入的 jwtProperties 参数赋值给类中的 jwtProperties 成员变量
        this.jwtProperties = jwtProperties;

        // 使用 jwtProperties 对象中的密钥 (jwtProperties.getSecret()) 创建了一个 HMAC256 算法对象 algorithm。
        // 这个算法将用于验证和生成 JWT（JSON Web Token）。
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());

        // 创建了一个 JWT 验证器 verifier
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    /**
     * 根据用户 userId 生成 jwt
     * @param userId
     * @return
     */
    public String genJwtToken(Long userId){
        // 1.生成jwt格式的会话，内部持有有效期，用户信息
        String payload = JsonUtil.toStr(MapUtils.create("traceId", SelfTraceIdGenerator.generate(), "userId", userId));
        String jwtToken = JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(payload)
                .sign(algorithm);

        // 使用 jwt 生成的 token 时，后端依赖 jwt 就可以进行身份校验
        // TODO:由于需要考虑用户退出之后，主动失效 token，而 jwt 本身无状态，
        //  所以这里使用 redis 做一个简单的 token -> userId 缓存，用于判断有效性

        return jwtToken;
    }

    /**
     * 用户退出时将 jwt 进行删除
     * @param jwtToken 退出请求时传入 token
     */
    public void removeJwtToken(String jwtToken){
        //TODO: 对 Redis 要进行的操作
    }

    /**
     * 根据传入的 jwtToken 获取 userId - 同时也校验 token 的正确性和实效性
     * @param jwtToken 请求出入的 jwt - token
     * @return userId
     */
    public Long getUserIdByJwtToken(String jwtToken){
        // jwt的校验方式，如果token非法或者过期，则直接验签失败
        try{
            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            String payload = new String(Base64Utils.decodeFromString(decodedJWT.getPayload()));
            // jwt 验证通过 - 获取对应的 userId
            String userId = String.valueOf(JsonUtil.toObj(payload, Map.class).get("userId"));

            // 从 redis 中获取 userId，来判断用户是否已经退出登录

            return Long.valueOf(userId);
        }catch (Exception e){
            log.info("JWT Token 检验失败! token : {}, mag: {}", jwtToken, e.getMessage());
            return null;
        }
    }
}
