package com.tjyy.sharing.api.vo;

import com.tjyy.sharing.api.vo.constants.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: Tjyy
 * @date: 2024-06-13 20:26
 * @description: Controller 返回的统一封装
 */
@Data
public class ResVo<T> implements Serializable {
    private final static long serialVersionUID = 123123123L;
    @ApiModelProperty(value = "返回结果状态", required = true)
    private Status status;
    @ApiModelProperty(value = "返回结果对象", required = true)
    private T result;

    public ResVo(){}

    public ResVo(Status status){
        this.status = status;
    }

    public ResVo(T result){
        this.status = Status.newStatus(StatusEnum.SUCCESS);
        this.result = result;
    }

    public static <T> ResVo<T> success(T t){
        return new ResVo(t);
    }

    public static <T> ResVo<T> fail(Status status){
        return new ResVo<>(status);
    }

    public static <T> ResVo<T> fail(StatusEnum status,Object... args){
        return new ResVo<>(Status.newStatus(status, args));
    }
}
