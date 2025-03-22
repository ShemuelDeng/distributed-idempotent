package com.shemuel.idempotent.configuration.config;

import lombok.Data;

/**
 * redis配置
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Data
public class RedisConfig {

    /**
     * redis host
     */
    private String host;

    /**
     * redis port
     */
    private int port = 6379;

    /**
     * redis password
     */
    private String password;

    /**
     * redis database
     */
    private int database = 0;

    /**
     * If pooled connection not used for a <code>timeout</code> time
     * and current connections amount bigger than minimum idle connections pool size,
     * then it will closed and removed from pool.
     * Value in milliseconds.
     */
    private int idleConnectionTimeout = 10000;

    /**
     * Timeout during connecting to any Redis server.
     * Value in milliseconds.
     */
    private int connectTimeout = 10000;

    /**
     * Redis server response timeout.
     * Starts to countdown when Redis command was succesfully sent.
     * Value in milliseconds.
     */
    private int timeout = 3000;

    /**
     * Minimum idle Redis connection amount
     */
    private int connectionMinimumIdleSize = 24;

    /**
     * Redis connection maximum pool size
     */
    private int connectionPoolSize = 64;

}
