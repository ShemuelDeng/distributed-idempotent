package com.shemuel.idempotent.handler;

import com.shemuel.idempotent.model.IdempotentContext;


/**
 * 自定义响应策略抽象类
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public abstract class RespHandler<R> implements Handler<IdempotentContext<R>, R> {

    @Override
    public final R handle(IdempotentContext<R> context) throws Exception {
        return doHandle(context);
    }

    public abstract R doHandle(IdempotentContext<R> context) throws Exception;

}
