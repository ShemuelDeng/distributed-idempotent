package com.shemuel.idempotent.configuration.factory;

import com.shemuel.idempotent.processor.PersistProcessor;
import com.shemuel.idempotent.handler.IdemHandler;
import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.model.IdempotentResult;

import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;

/**
 * 幂等处理器工厂类，用于创建和管理自定义幂等处理器实例。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public class IdemHandlerFactory {

    @Resource
    private PersistProcessor processor;

    @Bean
    public IdemHandler<?> defaultIdemHandler() {
        return new IdemHandler() {
            @Override
            public IdempotentResult<?> doHandle(IdempotentContext context) throws Exception {
                return processor.process(context);
            }
        };
    }

}
