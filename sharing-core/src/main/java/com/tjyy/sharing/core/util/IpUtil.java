package com.tjyy.sharing.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * @author: Tjyy
 * @date: 2024-06-15 14:35
 * @description: IP地址 - 相关工具类
 */
@Slf4j
public class IpUtil {
    private static final String UNKNOWN = "unknown";
    private static final String DEFAULT_IP = "127.0.0.1";
    private static String LOCAL_IP = null;

    /**
     * 获取本地 ip 地址
     * @return 本机 ip 地址 - ipv4
     */
    public static String getLocalIp4Address() throws SocketException {
        if (LOCAL_IP != null){
            return LOCAL_IP;
        }

        final List<Inet4Address> inet4Addresses = getLocalIp4AddressFromNetWorkInterface();
        if (inet4Addresses.size() != 1){
            final Optional<Inet4Address> ipBySocket = getIpBySocket();
            LOCAL_IP = ipBySocket.map(Inet4Address::getHostAddress).orElseGet(() -> inet4Addresses.isEmpty() ? DEFAULT_IP : inet4Addresses.get(0).getHostAddress());
            return LOCAL_IP;
        }
        LOCAL_IP = inet4Addresses.get(0).getHostAddress();
        return LOCAL_IP;
    }

    /**
     * 获取本机所有网卡信息 - 得到所有 ipv4 信息
     * @return
     */
    private static List<Inet4Address> getLocalIp4AddressFromNetWorkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);

        // 获取所有网络接口信息
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (ObjectUtils.isEmpty(networkInterfaces)){
            return addresses;
        }

        while (networkInterfaces.hasMoreElements()){
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            // 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
            if (!isValidInterface(networkInterface)){
                continue;
            }

            // 所有网络接口的 ip 地址信息
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()){
                InetAddress inetAddress = inetAddresses.nextElement();
                // 判断是否是IPv4，并且内网地址并过滤回环地址.
                if (inetAddress instanceof Inet4Address){
                    addresses.add((Inet4Address) inetAddress);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     * @param networkInterface 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface networkInterface) throws SocketException {
        return !networkInterface.isLoopback() && !networkInterface.isPointToPoint() && !networkInterface.isUp()
                && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     * @param address ip 地址
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidAddress(InetAddress address){
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    /**
     * 通过Socket 唯一确定一个IP
     * 当有多个网卡的时候，使用这种方式一般都可以得到想要的IP。
     * @return IP 地址
     * @throws SocketException 抛出异常
     */
    private static Optional<Inet4Address> getIpBySocket() throws SocketException{
        try (final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address){
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public static String getClientIp(HttpServletRequest request){
        try {
            String xIp = request.getHeader("X-Real-IP");
            String xFor = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)) {
                //多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = xFor.indexOf(",");
                if (index != -1) {
                    return xFor.substring(0, index);
                } else {
                    return xFor;
                }
            }
            xFor = xIp;
            if (StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)) {
                return xFor;
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isBlank(xFor) || UNKNOWN.equalsIgnoreCase(xFor)) {
                xFor = request.getRemoteAddr();
            }

            if ("localhost".equalsIgnoreCase(xFor) || "127.0.0.1".equalsIgnoreCase(xFor) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(xFor)) {
                return getLocalIp4Address();
            }
            return xFor;
        } catch (Exception e) {
            log.error("get remote ip error!", e);
            return "x.0.0.1";
        }
    }
}
