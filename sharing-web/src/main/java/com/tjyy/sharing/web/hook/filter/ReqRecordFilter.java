package com.tjyy.sharing.web.hook.filter;

import com.google.common.base.Stopwatch;
import com.sun.javafx.event.EventUtil;
import com.tjyy.sharing.api.context.ReqInfoContext;
import com.tjyy.sharing.core.mdc.MdcUtil;
import com.tjyy.sharing.core.util.CookieUtil;
import com.tjyy.sharing.core.util.CrossUtil;
import com.tjyy.sharing.core.util.EnvUtil;
import com.tjyy.sharing.core.util.IpUtil;
import com.tjyy.sharing.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: Tjyy
 * @date: 2024-07-06 16:34
 * @description:
 * 1. 设置跨域属性
 * 2. 对于每个请求，判断用户是否登陆
 */
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
                log.info("{} - cost:\n{}", request.getRequestURI(), stopWatch.prettyPrint());
            }
        }
    }

    @Override
    public void destroy() {
    }


    /**
     *  请求参数的构建
     * @param request 传入请求
     * @param response 响应
     * @return 构建后的 HTTP 请求，实际执行操作是根据该请求执行
     */
    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {
        // 静态资源直接放行
        if (isStaticURI(request)){
            return request;
        }

        StopWatch stopWatch = new StopWatch("请求参数构建");
        try {
            // 添加全链路的traceId
            stopWatch.start("traceId");
            MdcUtil.addTraceId();
            stopWatch.stop();

            // 添加请求基本信息
            stopWatch.start("请求基本信息");
            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());
            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
            reqInfo.setHost(request.getHeader("host"));
            reqInfo.setPath(request.getPathInfo());
            if (reqInfo.getPath() == null) {
                String url = request.getRequestURI();
                int index = url.indexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                reqInfo.setPath(url);
            }
            reqInfo.setReferer(request.getHeader("referer"));
            reqInfo.setClientIp(IpUtil.getClientIp(request));
            reqInfo.setUserAgent(request.getHeader("User-Agent"));
            reqInfo.setDeviceId(getOrInitDeviceId(request, response));
            // request = this.wrapperRequest(request, reqInfo);
            stopWatch.stop();

            // 返回头中记录的 traceId 信息
            stopWatch.start("回写traceId");
            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
            stopWatch.stop();
        }catch (Exception e){
            log.error("init reqInfo error!", e);
        }finally {
            if (!EnvUtil.isPro()){
                log.info("{} -> 请求构建耗时: \n{}", request.getRequestURI(), stopWatch.prettyPrint());
            }
        }

        return request;
    }

    private boolean isStaticURI(HttpServletRequest request){
        return request == null
                || request.getRequestURI().endsWith("css")
                || request.getRequestURI().endsWith("js")
                || request.getRequestURI().endsWith("png")
                || request.getRequestURI().endsWith("ico")
                || request.getRequestURI().endsWith("svg")
                || request.getRequestURI().endsWith("min.js.map")
                || request.getRequestURI().endsWith("min.css.map");
    }

    // 初始化设备 Id
    private String getOrInitDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isNotBlank(deviceId) && !"null".equalsIgnoreCase(deviceId)) {
            return deviceId;
        }

        Cookie device = CookieUtil.findCookieByName(request, LoginService.USER_DEVICE_KEY);
        if (device == null) {
            deviceId = UUID.randomUUID().toString();
            if (response != null) {
                response.addCookie(CookieUtil.newCookie(LoginService.USER_DEVICE_KEY, deviceId));
            }
            return deviceId;
        }
        return device.getValue();
    }
}
