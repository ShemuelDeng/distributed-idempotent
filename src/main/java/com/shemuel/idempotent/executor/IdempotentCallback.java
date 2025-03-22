package com.shemuel.idempotent.executor;

import com.shemuel.idempotent.model.IdempotentContext;


/**
 * 幂等执行器回调
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public interface IdempotentCallback<R> {

    IdempotentContext<R> initContext() throws Throwable;

    R execute() throws Throwable;

}
