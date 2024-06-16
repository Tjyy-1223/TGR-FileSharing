## TGR - File Sharing

TGR - 基于 REACT + SPRINGBOOT 的文件共享平台

### 项目功能

开发中 ...

### 开发过程文档

**1 - TGR - 登录注册功能 - 框架**

+ 用户登录数据库表以及 entity 设计
+ Mapper 层 + DAO 层 + Service + Controller 搭建
+ 项目全局架构: sharing - web/core/api/service
+ 运行配置 application.yml

**2 - TGR - 登录注册功能 - 进阶(完善)**

+ 对于用户信息进行扩展 - 即数据库表进行补充
+ 对于 Mapper 和 DAO 层根据新增添的数据表进行相互扩展
+ 对于 Service 进行更为全面的补充和考虑
  + 输入密码使用 **md5 加密**并考虑密码验证
  + 对于注册登陆等跨多个数据表进行操作的过程，需要考虑 **Transaction 事务和回滚**
  + 登陆和注册成功使用 **JWT + Cookie 的方式进行验证**，考虑 JWT 放置在 response 中传回去
  + **全局异常处理器**，统一处理对应的异常，否则引发的异常没有传回数据的话会报 http - 500 - 服务器错误，即服务过程中抛出异常