package com.tjyy.sharing.core.mdc;

import com.tjyy.sharing.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.SocketException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author: Tjyy
 * @date: 2024-06-15 13:57
 * @description: 自定义 traceID 生成器
 */
public class SelfTraceIdGenerator {
    private final static Integer MIN_AUTO_NUMBER = 1000;
    private final static Integer MAX_AUTO_NUMBER = 10000;
    private static final Logger log = LoggerFactory.getLogger(SelfTraceIdGenerator.class);
    private static volatile Integer autoIncreaseNumber = MIN_AUTO_NUMBER;

    /**
     * 生成32位traceId，规则是 服务器 IP + 产生ID时的时间 + 自增序列 + 当前进程号
     * IP 8位：39.105.208.175 -> 2769d0af
     * 产生ID时的时间 13位： 毫秒时间戳 -> 1403169275002
     * 当前进程号 5位： PID
     * 自增序列 4位： 1000-9999循环
     * @return ac13e001.1685348263825.095001000
     */
    public static String generate(){
        StringBuilder traceId = new StringBuilder();
        try {
            traceId.append(convertIp(IpUtil.getLocalIp4Address())).append(".");
            traceId.append(Instant.now().toEpochMilli()).append(".");
            traceId.append(getProcessId());
            
        } catch (Exception e) {
            log.error("generate trace id error", e);
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        return traceId.toString();
    }

    /**
     * 将传入的 ip 转换为十六进制
     * @param ip 9.105.208.175
     * @return 2769d0af
     */
    private static String convertIp(String ip){
        return Arrays.stream(ip.split("\\."))
                .map(s -> String.format("02x", Integer.valueOf(s)))
                .collect(Collectors.joining());
    }


    /**
     * 使得自增序列在1000-9999之间循环  - 4位
     * @return 生成的自增序列
     */
    private static int getAutoIncreaseNumber() {
        if (autoIncreaseNumber >= MAX_AUTO_NUMBER){
            autoIncreaseNumber = MIN_AUTO_NUMBER;
            return autoIncreaseNumber;
        }else{
            return autoIncreaseNumber++;
        }
    }

    /**
     * @return 当前 5 位的进程数
     */
    private static String getProcessId(){
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String processId = runtime.getName().split("@")[0];
        return String.format("%05d", Integer.parseInt(processId));
    }
}
