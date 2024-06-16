package com.tjyy.sharing.core.util;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Tjyy
 * @date: 2024-06-15 13:14
 * @description: Map - 构建工具类
 */
public class MapUtils {
    /**
     * 通过静态方法 create 创建一个 HashMap，
     * 并向其中添加初始的键值对 (k, v) 和通过可变参数 kvs 添加更多的键值对。
     * return map
     */
    public static <K,V> Map<K, V> create(K k, V v, Object... kvs) {
        Map<K, V> map = Maps.newHashMapWithExpectedSize(kvs.length + 1);
        map.put(k, v);
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((K) kvs[i], (V) kvs[i + 1]);
        }
        return map;
    }

    /**
     * 通过静态方法 toMap 将集合 Collection 转化为哈希表 map
     * 其中 keyFunction 和 valueFunction 代表对 T 对象进行处理之后当作 Map 的 kv
     */
    public static <T,K,V> Map<K,V> toMap(Collection<T> list, Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (CollectionUtils.isEmpty(list)){
            return Maps.newHashMapWithExpectedSize(0);
        }
        return list.stream().collect(Collectors.toMap(keyFunction, valueFunction));
    }
}
