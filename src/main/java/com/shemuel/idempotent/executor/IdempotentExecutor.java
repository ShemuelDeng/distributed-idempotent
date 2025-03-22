package com.shemuel.idempotent.executor;

import com.alibaba.fastjson2.JSON;
import com.shemuel.idempotent.processor.PersistProcessor;
import com.shemuel.idempotent.configuration.property.IdempotentProperty;
import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.model.IdempotentResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.DigestUtils;

/**
 * 幂等执行器类，负责执行幂等逻辑。
 * 包括获取分布式锁、调用幂等策略处理器、缓存结果等核心逻辑。
 * 使用模板方法设计模式，通过模板方法定义核心逻辑，并允许子类覆盖部分逻辑。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Slf4j
public class IdempotentExecutor {

    @Resource
    private IdempotentProperty property;
    @Resource(name = "idemRedissonClient")
    private RedissonClient client;
    @Resource
    private PersistProcessor processor;

    /**
     * 环境
     */
    public static String IDEM_ENV;

    /**
     * 服务名
     */
    public static String IDEM_SERVER;

    @PostConstruct
    private void init() {
        IDEM_ENV = property.getEnv();
        IDEM_SERVER = property.getServerName();
    }

    // 幂等锁的key定义
    private static final String IDEMPOTENT_LOCK = "livechat:idempotent:lock:%s:%s:%s";

    private static String buildLock(String uid, List<Object> idemParam) {
        String unique = uid + JSON.toJSONString(idemParam);
        return String.format(IDEMPOTENT_LOCK, IDEM_ENV, IDEM_SERVER, DigestUtils.md5DigestAsHex(unique.getBytes(StandardCharsets.UTF_8)));
    }

    public final <R> R execute(IdempotentCallback<R> callback) throws Throwable {

        IdempotentContext<R> context = callback.initContext();
        String uid = context.getUid();
        if (StringUtils.isBlank(uid)) {
            throw new Exception("幂等组件: uid不能为空");
        }
        List<Object> param = context.getIdemParam();
        if (CollectionUtils.isEmpty(param)) {
            throw new Exception("幂等组件: idemParam不能为空");
        }
        RLock lock = client.getLock(buildLock(uid, param));
        return context.isFailFast() ? doFast(callback, context, lock) : doLazy(callback, context, lock);
    }

    private <R> R doLazy(IdempotentCallback<R> callback, IdempotentContext<R> context, RLock lock) throws Throwable {
        lock.lock();
        try {
            IdempotentResult<R> result = context.getIdemHandler().handle(context);
            if (result.isFlag()) {
                //执行
                R r = callback.execute();
                //缓存结果，自定义策略不缓存
                processor.persist(context.setResult(r));
                return r;
            }
            return context.setResult(result.getResult()).getRespHandler().handle(context);
        } finally {
            lock.unlock();
        }
    }

    private <R> R doFast(IdempotentCallback<R> callback, IdempotentContext<R> context, RLock lock) throws Throwable {
        if (lock.tryLock()) {
            try {
                //获取到锁，执行逻辑
                IdempotentResult<R> result = context.getIdemHandler().handle(context);
                if (result.isFlag()) {
                    //执行
                    R r = callback.execute();
                    //缓存结果，自定义策略不缓存
                    processor.persist(context.setResult(r));
                    return r;
                }
            } finally {
                lock.unlock();
            }
        }
        return context.getRespHandler().handle(context);
    }

}