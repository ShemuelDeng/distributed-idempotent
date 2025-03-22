package com.shemuel.idempotent.processor;

import static com.shemuel.idempotent.executor.IdempotentExecutor.IDEM_ENV;
import static com.shemuel.idempotent.executor.IdempotentExecutor.IDEM_SERVER;

import com.alibaba.fastjson2.JSON;
import com.shemuel.idempotent.enums.IdemStrategy;
import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.model.IdempotentRecord;
import com.shemuel.idempotent.model.IdempotentResult;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.util.DigestUtils;

/**
 * 持久化处理器类，负责将幂等结果持久化到Redis中。
 * 包括构建幂等签名、处理幂等记录等逻辑。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Slf4j
public class PersistProcessor {

    @Resource(name = "idemRedissonClient")
    private RedissonClient client;

    // redis的幂等记录key
    public static final String IDEMPOTENT_SIGN = "livechat:idempotent:sign:%s:%s:%s";

    public static String buildSign(String uid, List<Object> idemParam) {
        String unique = uid + JSON.toJSONString(idemParam);
        return String.format(IDEMPOTENT_SIGN, IDEM_ENV, IDEM_SERVER, DigestUtils.md5DigestAsHex(unique.getBytes(StandardCharsets.UTF_8)));
    }

    public final IdempotentResult<?> process(IdempotentContext<?> context) throws Exception {
        RBucket<IdempotentRecord> bucket = client.getBucket(buildSign(context.getUid(), context.getIdemParam()));
        if (bucket.isExists()) {
            IdempotentRecord record = bucket.get();
            if (Objects.nonNull(record)) {
                return IdempotentResult.fail(record.getResult(), Class.forName(record.getResultClassName()));
            }
        }
        return IdempotentResult.success();
    }

    public final void persist(IdempotentContext<?> context) {
        try {
            if (IdemStrategy.CUSTOM.equals(context.getIdemStrategy())) {
                return;
            }
            RBucket<IdempotentRecord> bucket = client.getBucket(buildSign(context.getUid(), context.getIdemParam()));
            if (bucket.isExists()) {
                return;
            }
            IdempotentRecord record = new IdempotentRecord()
                    .setIdemParam(JSON.toJSONString(context.getIdemParam()))
                    .setFailFast(context.isFailFast())
                    .setIdemStrategy(context.getIdemStrategy())
                    .setRespStrategy(context.getRespStrategy())
                    .setResult(JSON.toJSONString(context.getResult()))
                    .setResultClassName(context.getResult() == null ? Object.class.getName() : context.getResult().getClass().getName())
                    .setCreateTime(LocalDateTime.now());
            bucket.set(record, context.getDuration(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("幂等组件缓存幂等结果失败: ", e);
        }
    }

}
