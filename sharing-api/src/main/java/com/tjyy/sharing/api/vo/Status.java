package com.tjyy.sharing.api.vo;

import com.tjyy.sharing.api.vo.constants.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Tjyy
 * @date: 2024-06-12 22:47
 * @description: 业务状态封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    /**
     * 业务状态码
     */
    @ApiModelProperty(value = "状态码, 0 表示成功返回, 其他表示异常", required = true, example = "0")
    private int code;

    /**
     * 业务描述信息
     */
    @ApiModelProperty(value = "正确返回时为 ok，其他表示异常", required = true, example = "ok")
    private String msg;


    public static Status newStatus(int code, String msg){
        return new Status(code, msg);
    }

    public static Status newStatus(StatusEnum status, Object... msgs){
        String msg;
        if (msgs.length > 0){
            msg = String.format(status.getMsg(), msgs);
        }else{
            msg = status.getMsg();
        }
        return newStatus(status.getCode(), msg);
    }
}
