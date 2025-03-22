package com.shemuel.idempotent.configuration.factory;

import com.shemuel.idempotent.handler.RespHandler;
import com.shemuel.idempotent.model.IdempotentContext;
import org.springframework.context.annotation.Bean;

/**
 * @description: 响应处理器工厂
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public class RespHandlerFactory {

    @Bean
    public RespHandler<?> exceptionRespHandler() {
        return new RespHandler<Object>() {
            @Override
            public Object doHandle(IdempotentContext<Object> context) throws Exception {
                throw new Exception("重复请求");
            }
        };
    }

    @Bean
    public RespHandler<?> nullRespHandler() {
        return new RespHandler<Object>() {
            @Override
            public Object doHandle(IdempotentContext<Object> context) {
                return null;
            }
        };
    }

    @Bean
    public RespHandler<?> previousRespHandler() {
        return new RespHandler<Object>() {
            @Override
            public Object doHandle(IdempotentContext<Object> context) {
                return context.getResult();
            }
        };
    }

}
