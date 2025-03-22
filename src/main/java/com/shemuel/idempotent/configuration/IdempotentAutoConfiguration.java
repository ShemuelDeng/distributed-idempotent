package com.shemuel.idempotent.configuration;

import com.shemuel.idempotent.aop.aspect.IdempotentAspect;
import com.shemuel.idempotent.executor.IdempotentExecutor;
import com.shemuel.idempotent.configuration.factory.IdemHandlerFactory;
import com.shemuel.idempotent.configuration.factory.RespHandlerFactory;
import com.shemuel.idempotent.processor.PersistProcessor;
import com.shemuel.idempotent.util.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类，用于加载幂等组件的相关配置。
 * 通过条件注解控制是否启用幂等组件，并导入Redis配置、幂等处理器工厂和响应处理器工厂。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Configuration
@ConditionalOnProperty(prefix = "idempotent", name = "enable", havingValue = "true")
@Import({IdempotentRedisConfiguration.class, IdemHandlerFactory.class, RespHandlerFactory.class})
public class IdempotentAutoConfiguration {

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public PersistProcessor persistProcessor() {
        return new PersistProcessor();
    }

    @Bean
    public IdempotentExecutor idempotentExecutor() {
        return new IdempotentExecutor();
    }

    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

}
