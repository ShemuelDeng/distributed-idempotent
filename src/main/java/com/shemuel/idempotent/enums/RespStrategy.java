package com.shemuel.idempotent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 幂等校验返回值处理策略
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Getter
@AllArgsConstructor
public enum RespStrategy {
    //返回NULL
    NULL(1, "nullRespHandler"),
    //返回上次的请求结果
    PREVIOUS(2, "previousRespHandler"),
    //抛出异常：提醒重复请求
    EXCEPTION(3, "exceptionRespHandler"),
    //自定义 @IdemRespHandler
    CUSTOM(4, "");

    private final Integer value;

    private final String name;

}