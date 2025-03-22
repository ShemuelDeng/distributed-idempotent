package com.shemuel.idempotent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 幂等策略
 * 用于幂等校验后的持久化处理
 * 默认将接口接口存储到redis
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Getter
@AllArgsConstructor
public enum IdemStrategy {
    //默认策略
    DEFAULT(1, "defaultIdemHandler"),
    //自定义 @IdemHandler
    CUSTOM(0, "");

    private final Integer value;

    private final String name;

}