## Doc2 - TGR - 登录注册功能 - 进阶(完善)

登陆注册功能进阶：

1. 对于用户信息进行扩展 - 即数据库表进行补充
2. 对于 Mapper 和 DAO 层根据新增添的数据表进行相互扩展
3. 对于 Service 进行更为全面的补充和考虑
   1. 输入密码使用 md5 加密并考虑密码验证
   2. 对于注册登陆等跨多个数据表进行操作的过程，需要考虑 **Transaction 事务和回滚**
   3. 登陆和注册成功使用 **JWT + Cookie 的方式进行验证**，考虑 JWT 放置在 response 中传回去
   4. **全局异常处理器**，统一处理对应的异常，否则引发的异常没有传回数据的话会报 http - 500 - 服务器错误，即服务过程中抛出异常

**登陆注册补充功能（没有在第2小节中待实现，后续补充）：**

1. 对 Controller 进行完善：注册 + 登陆 + 退出，注意对于 jwt + redis 的操作
   1. 用户登录：将 token 保存在 redis 中进行记录
   2. 用户退出：将 token 从 redis 中进行删除
2. ThreadLocal + 拦截器 的方式实现整个请求过程中的信息保存
3. 事件驱动：注册成功之后要使用事件驱动来发布事件

### 1 登陆注册 - 数据库表补充

补充：用户个人信息表

登陆成功的时候，

1. 同时进行登陆表 + 个人信息表的增加
2. 跨表操作需要使用事务来保证 ACID 原则

```java
CREATE TABLE `user_info` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` INT(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户名',
    `photo` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '用户图像',
    `position` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '职位',
    `company` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '公司',
    `profile` VARCHAR(225) NOT NULL DEFAULT '' COMMENT '个人简介',
    `user_role` INT(4) NOT NULL DEFAULT '0' COMMENT '0 普通用户 1 管理员',
    `extend` VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '扩展字段',
    `ip` JSON NOT NULL COMMENT '用户的ip信息',
    `deleted` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY `key_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户个人信息表';
```

对于用户个人信息的 DO 实体类设计：

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_info", autoResultMap = true)
public class UserInfoDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户图像
     */
    private String photo;

    /**
     * 职位
     */
    private String position;

    /**
     * 公司
     */
    private String company;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 0 普通用户
     * 1 管理员用户
     */
    private Integer userRole;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * ip信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IpInfo ip;

    public IpInfo getIp(){
        if (ip == null){
            ip = new IpInfo();
        }
        return ip;
    }
}
```

## 2 补充对应的 Mapper 层和 DAO 层

对应的 mapper 层：

```java
/**
 * 用户个人信息对应的 Mapper 接口
 */
public interface UserInfoMapper extends BaseMapper<UserInfoDO> {
}
```

对应的 dao 层：

```java
/**
 * @author: Tjyy
 * @date: 2024-06-14 00:02
 * @description: 用户详情 - Dao 层函数
 */
@Repository
public class UserInfoDao extends ServiceImpl<UserInfoMapper, UserInfoDO> {

    /**
     * 根据 username 进行姓名相似的读取
     * @param userName 用户名
     * @return
     */
    public List<UserInfoDO> getByUsernameLike(String userName) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(UserInfoDO::getUserId, UserInfoDO::getUsername, UserInfoDO::getPhoto, UserInfoDO::getProfile)
                .and(!StringUtils.isEmpty(userName), v -> v.like(UserInfoDO::getUsername, userName))
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户 id 来寻找用户，注意不能读取已经删除的用户
     * @param userId 用户-id
     * @return
     */
    public UserInfoDO queryByUserId(String userId) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserInfoDO::getUserId, userId)
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户 id 来获取 - UserInfoDO 列表
     * @param userIds
     * @return
     */
    public List<UserInfoDO> getByUserIds(Collection<Integer> userIds){
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(UserInfoDO::getUserId, userIds)
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 获取用户数量
     * @return
     */
    public Long getUserCount(){
        return lambdaQuery()
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count();
    }

    /**
     * 更新对应的用户信息
     * @param user
     */
    public void updateUserInfo(UserInfoDO user){
        UserInfoDO record = baseMapper.selectById(user.getUserId());
        if (record.equals(user)) {
            return;
        }
        if (StringUtils.isEmpty(user.getPhoto())) {
            user.setPhoto(null);
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            user.setUsername(null);
        }

        user.setId(record.getId());
        updateById(user);
    }
}
```

## 3 Service 层功能补充

1. 输入密码使用 md5 加密并考虑密码验证
2. 对于注册登陆等跨多个数据表进行操作的过程，需要考虑 **Transaction 事务和回滚**
3. 登陆和注册成功使用 **JWT + Cookie 的方式进行验证**，考虑 JWT 放置在 response 中传回去
4. **全局异常处理器**，统一处理对应的异常，否则引发的异常没有传回数据的话会报 http - 500 - 服务器错误，即服务过程中抛出异常

### 3.1 密码存储 - 加密和解密

application.yml 配置文件中，配置对应的属性：

```java
# 密码加密配置
security:
  salt: tangerine
  salt-index: 3
```

com.tjyy.sharing.service.user.service.help.UserPwdEncoder 实现加密解密相关类：

```java
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
```

### 3.2 Transction 事务 - ACID

Transaction 注解基于动态代理实现作用

由于 Transcation **自调用问题**会让事务进行失效，所以这里将 Transaction 注解方法新建了一个服务类：com.tjyy.sharing.service.user.service.user.RegisterServiceImpl

```java
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
```

### 3.3 JWT + Cookie 登录校验

JWT-JSON Web Token 是目前最流行的跨域认证解决方案，他是一种基于Token令牌的认证授权机制；JWT本身也是一种Token，只不过是一种规范化之后的JSON格式的Token。

在服务端不需要保存任何信息（如Session），JWT中包含了身份验证所需要的全部信息，更轻量化，减轻了服务端的压力

**JWT的原理：**

- JWT本质上是一个字符串，通过.切分成三个 Base64 编码的部分：

  - Header：描述JWT的原数据，定义了生成签名的算法以及Token的类型
  - Payload：存储实际需要传递的数据（一般是不会加密的）
  - Signature（签名）：Signature 由 Payload、Header 和 Secret(密钥)通过特定的计算公式和加密算法得到，目的是防止JWT被篡改（）

- 基于JWT进行身份验证的程序中，其主要的的工作流程，也可以说是主要的原理：

  - 用户向服务器发送用户名、密码以及验证码用户登陆系统
  - 验证成功的话，服务端会返回已经签名的Token，也就是返回一个JWT
  - 用户以后每次向后端发送请求都在Header中带上这个JWT
  - 服务端检查JWT，服务端会根据 Header、Payload、密钥再次生成一个 Signature。拿新生成的 Signature 和 JWT 中的 Signature 作对比。
  - 从 JWT 中获取用户id →从数据库中取出数据，可以进行相关操作或获取用户状态

- 使用 JWT 的注意事项是什么：

  - 安全的核心在于密钥，密钥不能泄漏出去

  - ```WT一般存放在客户端的cookie里，隐私数据不要放在
    
    ```

  - 用户退出登陆如何让JWT失效

    - JWT放在Redis中，每次请求前需要查找Redis中是否有JWT，失效就从Redis中删除
    - Redis维护一个黑名单，失效的话JWT直接加入到黑名单中

  - JWT的有效期建议采用过期+有效期延长的方式（发现快过期-返回一个新的JWT）

JWT 生成函数 com.tjyy.sharing.service.user.service.help.UserSessionHelper

```java
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
        // TODO:由于需要考虑用户退出之后，主动失效 token，而 jwt 本身无状态，所以这里使用 redis 做一个简单的 token -> userId 缓存，用于判断有效性

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
```

登陆方式的最后进行补充：

```java
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
```

注册方式的最后补充：

```java
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
```

退出之后要及时把 Redis 请求中的 Cookie 删除掉：

```java
@Override
public void logout(String jwtToken) {
    userSessionHelper.removeJwtToken(jwtToken);
}
```

### 3.4 全局异常处理器

全局异常处理器可以捕获抛出的异常 - 进行统一处理：com.tjyy.sharing.web.global.GlobalExceptionHandler

```java
/**
 * @author: Tjyy
 * @date: 2024-06-14 21:42
 * @description: 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理器,对于抛出的异常进行 fail 封装后返回
     * @param e 抛出的异常
     * @return
     */
    @ExceptionHandler(value = ForumException.class)
    public ResVo<String> handleForumException(ForumException e){
        return ResVo.fail(e.getStatus());
    }
}
```

## 4 Controller 功能补充 - 完善

LoginController 共有三个相关功能：

1. 用户名 - 密码的方式 - 登录

   ```java
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
   ```

2. 用户名 - 密码的方式 - 注册

   ```java
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
   ```

3. 用户退出登陆：取消 JWT + 删除 redis 数据

   ```java
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
   ```