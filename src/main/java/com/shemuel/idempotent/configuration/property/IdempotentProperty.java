package com.shemuel.idempotent.configuration.property;

import com.shemuel.idempotent.configuration.config.RedisConfig;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 幂等组件的配置属性类，映射application.yml中的配置项。
 * 包括环境、Redis配置等信息，并在初始化时进行必要的校验。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Data
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentProperty implements InitializingBean {

    @Getter(AccessLevel.PRIVATE)
    @Value("${spring.profiles.active:test}")
    private String activeProfile;

    @Value("${spring.application.name}")
    private String serverName;

    /**
     * 是否启用幂等组件
     */
    private Boolean enable;

    /**
     * 环境（用于区分test0-test10环境），默认值：spring.profiles.active
     */
    private String env;

    /**
     * redis配置
     */
    @NestedConfigurationProperty
    private RedisConfig redis;

    @Override
    public void afterPropertiesSet() throws Exception {
        env = StringUtils.isBlank(env) ? activeProfile : env;
        if (StringUtils.isBlank(env)) {
            throw new Exception("幂等组件加载失败: {idempotent.env} 为空");
        }
        if (StringUtils.isBlank(serverName)) {
            throw new Exception("幂等组件加载失败: {spring.application.name} 为空");
        }
        if (Objects.isNull(redis)) {
            throw new Exception("幂等组件加载失败: {idempotent.redis} 为空");
        }
        if (StringUtils.isBlank(redis.getHost())) {
            throw new Exception("幂等组件加载失败： {idempotent.redis.host} 为空");
        }
    }
}
