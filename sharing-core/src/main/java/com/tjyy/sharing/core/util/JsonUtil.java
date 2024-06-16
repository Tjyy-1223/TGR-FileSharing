package com.tjyy.sharing.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author: Tjyy
 * @date: 2024-06-15 11:10
 * @description: JSON - 相关工具类
 */
public class JsonUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * String str -> Object obj
     * @param str 传入 json 字符串
     * @param clazz 解析后的对象类型
     * @return
     * @param <T> 泛型标记
     */
    public static <T> T toObj(String str, Class<T> clazz) {
        try {
            return jsonMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据传入对象返回生成的 JSON 字符串
     * @param obj 传入的对象
     * @return
     * @param <T> 泛型
     */
    public static <T> String toStr(T obj) {
        try {
            return jsonMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列换成json时,将所有的 long(bitInt) 变成string
     * 因为js中得数字类型不能包含所有的java long值
     */
    public static SimpleModule bigIntToStrsimpleModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, newSerializer(s -> String.valueOf(s)));
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(long[].class, newSerializer((Function<Long, String>) String::valueOf));
        simpleModule.addSerializer(Long[].class, newSerializer((Function<Long, String>) String::valueOf));
        simpleModule.addSerializer(BigDecimal.class, newSerializer(BigDecimal::toString));
        simpleModule.addSerializer(BigDecimal[].class, newSerializer(BigDecimal::toString));
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger[].class, newSerializer((Function<BigInteger, String>) BigInteger::toString));
        return simpleModule;
    }

    /***
     * 将传入的 K 类型转化为 String 类型
     * @param function 序列化转化函数
     * @return
     * @param <T> String
     * @param <K> 各种 BigInt 类型
     */
    public static <T,K> JsonSerializer<T> newSerializer(Function<K,String> function) {
        return new JsonSerializer<T>() {
            @Override
            public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                // 对象为空时，写入null
                if (t == null) {
                    jsonGenerator.writeNull();
                    return;
                }

                // 判断对象是否为数组
                if (t.getClass().isArray()) {
                    jsonGenerator.writeStartArray(); // 写入JSON数组的起始标记

                    // 将数组转为流，并对每个元素进行处理
                    Stream.of(t).forEach(s -> {
                        try {
                            // 调用传入的函数function，将数组元素转换为字符串，并写入JSON生成器
                            jsonGenerator.writeString(function.apply((K) s));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    jsonGenerator.writeEndArray(); // 写入JSON数组的结束标记
                } else {
                    // 对象不是数组，直接将对象转为字符串，并写入JSON生成器
                    jsonGenerator.writeString(function.apply((K) t));
                }
            }
        };
    }
}
