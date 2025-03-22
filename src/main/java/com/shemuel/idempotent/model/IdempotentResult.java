package com.shemuel.idempotent.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 幂等结果类，用于封装幂等操作的结果信息。
 * 包括幂等标志、执行结果等字段。
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Data
@Accessors(chain = true)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdempotentResult<R> {

    /**
     * 幂等标志
     *
     * true：幂等校验成功（即不重复），继续执行逻辑 false：幂等校验失败（重复），不再执行，执行响应策略
     */
    private boolean flag;

    /**
     * 执行结果
     */
    private R result;

    public static <R> IdempotentResult<R> success() {
        return new IdempotentResult<R>().setFlag(true);
    }

    public static <R> IdempotentResult<R> fail(String str, Class<R> clz) {
        if (StringUtils.isBlank(str)) {
            return fail(null);
        }
        return fail(JSONObject.parseObject(str, clz));
    }

    public static <R> IdempotentResult<R> fail(R r) {
        return new IdempotentResult<R>().setFlag(false).setResult(r);
    }

}
