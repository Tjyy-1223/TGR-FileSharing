package com.tjyy.sharing.core.mdc;

import org.slf4j.MDC;

/**
 * @author: Tjyy
 * @date: 2024-07-06 16:53
 * @description:
 */
public class MdcUtil {
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 利用 MDC 添加自定义信息
     * 其底层原理借助了 ThreadLocal<Map<String, String>> copyOnThreadLocal 来进行线程隔离
     * @param key 存储的键
     * @param val 存储的值
     */
    public static void add(String key, String val) {
        MDC.put(key, val);
    }

    /**
     * 向 MDC 中添加 traceId 属性
     */
    public static void addTraceId() {
        // traceId的生成规则，技术派提供了两种生成策略，可以使用自定义的也可以使用SkyWalking; 实际项目中选择一种即可
        MDC.put(TRACE_ID_KEY, SelfTraceIdGenerator.generate());
    }

    /**
     * 获取 MDC 存在的 traceId 属性
     * @return
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 对 MDC 中的所有内容进行清空之后将 traceId 放入
     * 即当前 MDC 拥有的 HashMap 只有一条键值对
     */
    public static void reset() {
        String traceId = MDC.get(TRACE_ID_KEY);
        MDC.clear();
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 对 MDC 中的所有内容进行清空
     * 即释放 ThreadLocalMap 中由 MDC 负责的 ThreadLocal 的键值对
     */
    public static void clear() {
        MDC.clear();
    }
}
