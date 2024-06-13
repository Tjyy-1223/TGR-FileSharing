## Doc-1 TGR - 登录注册功能 - 框架

### 1.1 Entity

数据库表：

注册登陆：主键 id、用户名、密码、登陆方式、是否删除、创建时间、修改时间

```java
CREATE TABLE `user` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `third_account_id` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '第三方用户ID',
    `username` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '密码',
    `login_type` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '登录方式：0-微信登录,1-账号密码',
    `deleted` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY `key_third_account_id` (`third_account_id`),
    KEY `key_user_name` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户登录表';
```

由于 id - create_time - update_time 是无论哪里都要用到的，所以可以拆分为两个类进行：

1. com.tjyy.sharing.api.entity.BaseDO - 基本类
2. com.tjyy.sharing.service.user.respository.entity.UserDO - 用户特定字段

DO 是与数据库直接进行交互的类，这里举例 BaseDO 和 UserDO，之后的各种 DO 类的含义大差不差，可以根据基本类 + 用户特定字段进行扩展。

```java
@Data
public class BaseDO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Date createTime;

    private Date updateTime;
}
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 第三方用户ID
     */
    private String thirdAccountId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码，密文存储
     */
    private String password;

    /**
     * 登录方式: 0-微信登录，1-账号密码登录
     */
    private Integer loginType;

    /**
     * 删除标记
     */
    private Integer deleted;
}
```

### 1.2 Mapper 层 + DAO 层

Mapper 层是最底层，负责 UserDO 类和数据库表 ‘user’ 之间的交互，可以使用 MybatisPlus 天然继承一些功能，但是一些自定义功能还是需要自己去编写的。

BaseMapper 中的方法如下：

- int insert(T entity);
- int deleteById(Serializable id);
- int deleteById(T entity);
- int deleteByMap(@Param("cm") Map<String, Object> columnMap);
- int delete(@Param("ew") Wrapper<T> queryWrapper);
- int deleteBatchIds(@Param("coll") Collection<? extends Serializable> idList);
- int updateById(@Param("et") T entity);
- int update(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);
- T selectById(Serializable id);
- List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList);
- List<T> selectByMap(@Param("cm") Map<String, Object> columnMap);
- default T selectOne(@Param("ew") Wrapper<T> queryWrapper)
- Long selectCount(@Param("ew") Wrapper<T> queryWrapper);
- List<T> selectList(@Param("ew") Wrapper<T> queryWrapper);
- List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<T> queryWrapper);
- List<Object> selectObjs(@Param("ew") Wrapper<T> queryWrapper);
- <P extends IPage<T>> P selectPage(P page, @Param("ew") Wrapper<T> queryWrapper);
- <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, @Param("ew") Wrapper<T> queryWrapper);

在 UserMapper 层中，我们还可以补充一些自己要用到的方法：

```java
/**
 * 用户登录 mapper 接口
 * 用于 UserDO 和 数据库表 - user 进行连接
 */
public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * 根据第三方 id 进行查询
     * @param accountId
     * @return
     */
    @Select("select * from user where third_account_id = #{account_id} limit 1")
    UserDO getByThirdAccountId(@Param("account_id") String accountId);

    /**
     * 遍历用户id
     * @param offsetUserId
     * @param limitUserId
     * @return
     */
    @Select("select id from user where id > #{offsetUserId} order by id asc limit #{size};")
    List<Long> getUserIdsOrderByIdAsc(@Param("offsetUserId") Long offsetUserId, @Param("size") Long limitUserId);
}
```

UserDO 中可以使用 ServiceImpl 来进一步补充一些自动方法：

- saveBatch(Collection<T> entityList, int batchSize)
- String getSqlStatement(SqlMethod sqlMethod)
- saveOrUpdate(T entity)
- saveOrUpdateBatch(Collection<T> entityList, int batchSize)
- updateBatchById(Collection<T> entityList, int batchSize)
- removeById(Serializable id)
- removeByIds(Collection<?> list)
- removeById(Serializable id, boolean useFill)
- removeBatchByIds(Collection<?> list, int batchSize)

除此之外，如果可以使用 baseMapper 来使用 UserMapper 中定义好的方法，进行数据库层面的进一步组装，案例如下：

- **需要与数据库进行交互的功能要补充在 UserMapper 中**
- **需要 UserDO 相互组装或进行逻辑判断的功能卸载 UserDao 中**

UserDao 层和 UserMapper 层的相互协同如下：

```java
@Repository
public class UserDao extends ServiceImpl<UserMapper, UserDO> {

    /**
     * 根据传入的 userId 和 size 来获取区间内所有存在的 userId
     * @param userId
     * @param size
     * @return
     */
    public List<Long> scanUserId(Long userId, Long size) {
        return baseMapper.getUserIdsOrderByIdAsc(userId, size == null ? PageParam.DEFAULT_PAGE_SIZE : size);
    }

    /**
     * 第三方账号进行登录，返回用户信息
     * @param accountId
     * @return
     */
    public UserDO getByThirdAccountId(String accountId) {
        return baseMapper.getByThirdAccountId(accountId);
    }

    /**
     * 根据 UserDO 中是否有 userId，插入或者更新 userDO
     * @param userDO
     */
    public void saveUser(UserDO userDO) {
        if (userDO.getId() == null){
            baseMapper.insert(userDO);
        }else{
            baseMapper.updateById(userDO);
        }
    }

    /**
     * 根据传入的用户名返回对应的 UserDO
     * @param username
     * @return
     */
    public UserDO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserDO::getUsername, username)
                .eq(UserDO::getDeleted, YesOrNoEnum.NO.getCode())
                .last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据 UserId 获取对应的 User 类型
     * @param id
     * @return
     */
    public UserDO getUserById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 根据 UserId 跟新对应的 User
     * @param userDO
     */
    public void updateUser(UserDO userDO) {
        baseMapper.updateById(userDO);
    }

}
```

### 1.3 Service 层

Service 主要实现了登陆相关的 LoginService 以及其实现类 LoginServiceImpl

```java
public interface LoginService {
    /**
     * 退出登陆
     * @param session
     */
    void logout(String session);

    /**
     * 根据用户名和密码进行登录
     * @param username
     * @param password
     * @return
     */
    String loginByUserPwd(String username, String password);

    /**
     * 根据用户名和密码进行注册
     * @param username
     * @param password
     * @return
     */
    String registerByUserPwd(String username, String password);
}
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
```

**上面的对应的功能可以先忽略，只是简单的简单的实现，还没有具体设计登陆逻辑**

注意：MybatisPlus 的配置和 MapperScan 需要进行配置

### 1.4 Controller 层

Controller 是前端请求的入口：

```java
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
```

### 1.5 足以支撑项目运行的配置问题

上面只是实现了一个简单的功能，主要目的是要让项目可以运行起来，所以其需要注意下面的地方：

1. 最外层 pom 中项目模块结构 <modules> 要进行配置好
2. application.yml 中的启动后端口以及 mysql 连接配置需要配置好

application.yml:

```java
server:
  port: 8080
  servlet:
    session:
      timeout: 5m # 设置session的有效期为五分钟
  compression:
    enabled: true # 开启返回结果压缩，减少传输耗时
    min-response-size: 2KB

# 引入其他 yaml 文件配置
spring:
  config:
    import: application-dal.yml

# knife4j
knife4j:
  enable: true
  setting:
    language: zh-CN
  openapi:
    title: TGR-FileSharing
    description: 基于 REACT + SPRINGBOOT 开发的文件共享系统
    version: 1.0.0
    concat:
      - tjyy
      - <https://github.com/Tjyy-1223/TGR-FileSharing>
    license: Apache License 2.0
    license-url:
    email: tjyy24@nuaa.edu.cn
    group:
      front:
        group-name: 相关功能实现
        api-rule: package
        api-rule-resources:
          - com.tjyy.sharing.web.front

# mybatis 相关统一配置
mybatis-plus:
  configuration:
    #开启下划线转驼峰
    map-underscore-to-camel-case: true

# 默认的数据库名
database:
  name: file_sharing
```

application-dal.yml:

```java
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${database.name}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 991116
```