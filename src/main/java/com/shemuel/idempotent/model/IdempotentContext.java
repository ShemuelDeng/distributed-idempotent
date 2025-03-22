package com.shemuel.idempotent.model;

import com.shemuel.idempotent.enums.IdemStrategy;
import com.shemuel.idempotent.handler.RespHandler;
import com.shemuel.idempotent.util.IdemHandlerUtils;
import com.shemuel.idempotent.enums.RespStrategy;
import com.shemuel.idempotent.handler.IdemHandler;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 幂等上下文类，用于存储幂等操作的上下文信息。
 * 包括幂等唯一标识、幂等参数、有效期、策略等相关信息。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Data
@Accessors(chain = true)
public class IdempotentContext<R> {

    /**
     * 幂等唯一标志，用于区分不同幂等业务，默认使用类名.方法名
     */
    private String uid;

    /**
     * 幂等参数：确定幂等请求的字段值（仅value，无key）
     */
    private List<Object> idemParam;

    /**
     * 参数
     */
    private Map<String, Object> argMap;

    /**
     * 幂等有效期时间（单位秒，默认3秒），若duration<=0则duration=3，需要持久化的使用CUSTOM策略自己实现
     */
    private int duration = 3;

    /**
     * true：并发的情况下，获取锁失败则直接执行respStrategy false：并发的情况下，获取锁失败则等待获取锁，直到获取到锁
     *
     * @warnning true  RespStrategy.PREVIOUS = RespStrategy.NULL
     */
    private boolean failFast = true;

    /**
     * 幂等策略
     */
    private IdemStrategy idemStrategy = IdemStrategy.DEFAULT;

    /**
     * 响应策略
     */
    private RespStrategy respStrategy = RespStrategy.NULL;

    /**
     * 自定义幂等策略
     */
    private IdemHandler<R> idemHandler;

    /**
     * 自定义响应策略
     */
    private RespHandler<R> respHandler;

    /**
     * 幂等结果
     */
    private R result;

    public IdemHandler<R> getIdemHandler() {
        if (IdemStrategy.CUSTOM.equals(this.idemStrategy)) {
            if (idemHandler != null) {
                return idemHandler;
            }
            this.idemStrategy = IdemStrategy.DEFAULT;
        }
        return IdemHandlerUtils.getIdemByStrategy(this.idemStrategy.getName());
    }

    public RespHandler<R> getRespHandler() {
        if (RespStrategy.CUSTOM.equals(respStrategy)) {
            if (respHandler != null) {
                return respHandler;
            }
            this.respStrategy = RespStrategy.NULL;
        }
        return IdemHandlerUtils.getRespByStrategy(respStrategy.getName());
    }

    public int getDuration() {
        return duration <= 0 ? 3 : duration;
    }
}
