package com.shemuel.idempotent.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.shemuel.idempotent.enums.IdemStrategy;
import com.shemuel.idempotent.enums.RespStrategy;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 幂等记录类，用于存储幂等请求的详细信息。
 * 包括幂等参数、策略、结果等字段，并支持序列化和反序列化。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Data
@Accessors(chain = true)
public class IdempotentRecord implements Serializable {

    /**
     * 主键
     */
//    private Long id;

    /**
     * 环境
     */
//    public String env;

    /**
     * 服务名
     */
//    public String server;

    /**
     * 幂等唯一标志
     */
//    private String uid;

    /**
     * 幂等参数：确定幂等请求的字段值（仅value，无key）
     */
    private String idemParam;

    /**
     * 幂等有效期时间（单位秒）
     */
//    private Integer duration;

    /**
     * 快速失败标志
     */
    private Boolean failFast;

    /**
     * 幂等策略
     */
    private IdemStrategy idemStrategy;

    /**
     * 响应策略
     */
    private RespStrategy respStrategy;

    /**
     * 幂等结果， json字符串
     */
    private String result;

    /**
     * 幂等结果 className
     */
    private String resultClassName;

    /**
     * 创建时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

}
