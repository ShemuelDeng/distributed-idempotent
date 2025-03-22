package com.shemuel.idempotent.handler;

import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.model.IdempotentResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义幂等策略抽象类，提供幂等处理的通用逻辑。
 * 子类需实现doHandle方法以定义具体的幂等处理逻辑。
 */
@Slf4j
public abstract class IdemHandler<R> implements Handler<IdempotentContext<R>, IdempotentResult<R>> {

    @Override
    public final IdempotentResult<R> handle(IdempotentContext<R> context) throws Exception{
        try {
            return doHandle(context);
        } catch (Exception e) {
            log.error("幂等组件：IdemHandler执行失败: ", e);
            throw  e;
        }
    }

    public abstract IdempotentResult<R> doHandle(IdempotentContext<R> context) throws Exception;

}
