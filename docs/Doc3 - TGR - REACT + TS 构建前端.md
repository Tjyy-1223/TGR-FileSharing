## Doc3 - TGR - REACT + TS 构建前端

## 1 Axios - 前后端交互

使用 REACT + TS 搭建前端界面，并使用 axios 与后端进行数据上的异步交互

异步发送请求的过程主要定义在：web/src/request 中

其中在 index.ts 中创建了 axios 实例，方便 axios 异步请求的发送：

```java
import axios from "axios"

// 创建axios实例
const instance = axios.create({
    // 基本请求路径的抽取
    baseURL:"/api",
    // 这个时间是你每次请求的过期时间，这次请求认为20秒之后这个请求就是失败的
    timeout:20000,
})

// 请求拦截器
instance.interceptors.request.use(config=>{
    return config
},err=>{
    return Promise.reject(err)
});

// 响应拦截器
instance.interceptors.response.use(res=>{
    return res.data
},err=>{
    return Promise.reject(err)
})

export default instance
```

之后在 api.ts 中定义了可能需要用到的异步函数：

```java
import request from "./index"

export const LoginAPI = (params: any):Promise<Res> =>request.get("/login/username?"
    + "username=" + params.username + "&password=" + params.password
);

export const RegisterAPI = (params:RegisterAPIReq):Promise<Res> => request.post("/login/register",params);
```

当我们在登陆界面想要实现相关功能时，则可以通过调用定义好的异步函数来进行操作。

## 2 跨域问题

解决前后端交互导致的跨域问题：

- 跨域问题：https://blog.csdn.net/qq_37896194/article/details/102834574
- 详解：https://blog.csdn.net/Mxq853126/article/details/137441237

跨域问题解决分别需要在前端和后端进行相应的配置：

前端配置：

- 在 vite.config.ts 添加 server 相关配置

  ```java
  server: {
      host: '0.0.0.0', //Ip地址
      port: 3002, //端口号
      hmr: true, //热启动
      open: true, //自动打开浏览器
      //配置代理
      proxy: {
        '/api': {	//指定了要代理的请求路径前缀。这意味着所有以 /api 开头的请求都会被代理到指定的目标地址。
          target: '<http://localhost:8080>',
          // target就是你要访问的目标地址，可以是基础地址，这样方便在这个网站的其他api口调用数据
          changeOrigin: true,//表示是否改变请求头中的 Origin 字段，如果设置为 true，则会把请求头中的 Origin 字段改为目标地址。
          rewrite: (path) => path.replace(/^\\/api/, ''),
          //是对请求路径进行重写的选项，它指定了如何重写请求路径。在这里，path.replace(/^\\/api/, '') 表示将请求路径中的 /api 前缀替换为空，这样就去掉了原始请求路径中的 /api 前缀，使得请求路径符合目标服务器的预期。
          // 要记得加rewrite这句
        }
      }
    }
  ```

- 后端配置，实现一个 com.tjyy.sharing.core.util.CrossUtil 对相应中的跨域字段进行设置，并在拦截器中进行相应的配置

  ```java
  public class CrossUtil {
      /**
       * 支持跨域研究，不进行配置的话无法进行前后端消息交互
       * @param request 请求
       * @param response 响应
       */
      public static void buildCors(HttpServletRequest request, HttpServletResponse response) {
          String origin = request.getHeader("Origin");
          if (StringUtils.isEmpty(origin)) {
              response.setHeader("Access-Control-Allow-Origin", "*");
              response.setHeader("Access-Control-Allow-Credentials", "false");
          }else{
              response.setHeader("Access-Control-Allow-Origin", origin);
              response.setHeader("Access-Control-Allow-Credentials", "true");
          }
          response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
          response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
          response.setHeader("Access-Control-Max-Age", "3600");
          response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-Real-IP, X-Forwarded-For, d-uuid, User-Agent, x-zd-cs, Proxy-Client-IP, HTTP_CLIENT_IP, HTTP_X_FORWARDED_FOR");
      }
  }
  ```

  ```java
  @Slf4j
  @WebFilter(urlPatterns = "/*", filterName = "reqRecordFilter", asyncSupported = true)
  public class ReqRecordFilter implements Filter {
      /**
       * 返回给前端的traceId，用于日志追踪
       */
      private static final String GLOBAL_TRACE_ID_HEADER = "g-trace-id";
  
      @Override
      public void init(FilterConfig filterConfig){
      }
  
      @Override
      public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
          long start = System.currentTimeMillis();
          HttpServletRequest request = null;
          StopWatch stopWatch = new StopWatch("请求耗时");
          // 接受请求之前执行一些操作
          try {
              stopWatch.start("请求参数构建");
              request = this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
              stopWatch.stop();
  
              stopWatch.start("跨域请求处理 cors");
              CrossUtil.buildCors(request, (HttpServletResponse) servletResponse);
              stopWatch.stop();
  
              stopWatch.start("业务执行");
              filterChain.doFilter(request, servletResponse);
              stopWatch.stop();
          }finally { // 执行请求之后补充一些操作
              stopWatch.start("输出请求日志");
              // TODO：输出请求日志
  
              MdcUtil.clear();
              ReqInfoContext.clear();
              stopWatch.stop();
  
              if (!isStaticURI(request) && !EnvUtil.isPro()) {
                  log.info("{} - cost:\\n{}", request.getRequestURI(), stopWatch.prettyPrint());
              }
          }
      }
  
      @Override
      public void destroy() {
      }
      ....
  }
  ```

## 3 SpringUtil 工具类

构建 SpringUtil 工具类方便获取 Bean 信息以及 Config 配置信息，通过继承 ApplicationContextAware, EnvironmentAware 方式实现

```java
@Component
public class SpringUtil implements ApplicationContextAware, EnvironmentAware {
    // context 和 environment 利用 @Component 注解自动注入
    @Getter
    private volatile static ApplicationContext context;
    private volatile static Environment environment;
    @Getter
    private static Binder binder;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringUtil.environment = environment;
    }

    // 获取 Bean
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static Object getBeanOrNull(String beanName) {
        try {
            return context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    // 获取配置
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    public static String getConfigOrElse(String mainKey, String slaveKey) {
        String ans = environment.getProperty(mainKey);
        if (ans == null) {
            return environment.getProperty(slaveKey);
        }
        return ans;
    }

    // 带默认值的获取配置
    public static String getConfig(String key, String val) {
        return environment.getProperty(key, val);
    }

    // 发布事件消息
    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }
}
```

使用 Component 注解可以对静态属性和静态类进行提前注入，后续直接使用就可以获取 Spring 环境中的 Bean 信息和配置信息。