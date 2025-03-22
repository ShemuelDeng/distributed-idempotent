package com.shemuel.idempotent.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Getter
@AllArgsConstructor
public class ExpressionRootObject {

    private final Object object;

    private final Object[] args;

}