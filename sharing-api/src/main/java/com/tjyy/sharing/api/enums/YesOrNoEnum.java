package com.tjyy.sharing.api.enums;

import lombok.Getter;

/**
 * @author: Tjyy
 * @date: 2024-06-12 21:36
 * @description: 状态枚举
 */
@Getter
public enum YesOrNoEnum {
    NO(0, "N", "否", "no"),
    YES(1, "Y", "是", "yes");

    private final int code;
    private final String desc;
    private final String cnDesc;
    private final String enDesc;

    YesOrNoEnum(int code, String desc, String cnDesc, String enDesc) {
        this.code = code;
        this.desc = desc;
        this.cnDesc = cnDesc;
        this.enDesc = enDesc;
    }

    /**
     * 根据传入的 code 类型返回对应的枚举类型
     * @param code
     * @return
     */
    public static YesOrNoEnum formCode(int code) {
        for (YesOrNoEnum yesOrNoEnum : YesOrNoEnum.values()){
            if (yesOrNoEnum.getCode() == code){
                return yesOrNoEnum;
            }
        }
        return YesOrNoEnum.NO;
    }

    /**
     * 是否为是和否，用于某些场景字段为赋值的情况
     * @param code
     * @return
     */
    public static boolean equalYN(Integer code){
        if (code == null)
            return false;
        if (code != null && (code.equals(YES.code) || code.equals(NO.code))){
            return true;
        }
        return false;
    }

    /**
     * 判断是否为 yes
     * @param code
     * @return
     */
    public static boolean isYes(Integer code){
        if (code == null)
            return false;
        return code.equals(YES.getCode());
    }
}
