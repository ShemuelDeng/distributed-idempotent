package com.shemuel.idempotent.configuration;

import com.shemuel.idempotent.configuration.config.RedisConfig;
import com.shemuel.idempotent.configuration.property.IdempotentProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置类，用于初始化Redisson客户端。
 * 根据配置属性创建Redis连接，并设置相关参数。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Configuration
@EnableConfigurationProperties({IdempotentProperty.class})
public class IdempotentRedisConfiguration {

    private final IdempotentProperty prop;

    public IdempotentRedisConfiguration(IdempotentProperty prop) {
        this.prop = prop;
    }

    @Bean
    public RedissonClient idemRedissonClient() {
        RedisConfig redis = prop.getRedis();
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec())
                .useSingleServer()
                .setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
                .setPassword(redis.getPassword())
                .setDatabase(redis.getDatabase())
                .setConnectionPoolSize(redis.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redis.getConnectionMinimumIdleSize())
                .setIdleConnectionTimeout(redis.getIdleConnectionTimeout())
                .setConnectTimeout(redis.getConnectTimeout())
                .setTimeout(redis.getTimeout());
        return Redisson.create(config);
    }

}
