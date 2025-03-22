package com.shemuel.idempotent.aop.anno;

import com.shemuel.idempotent.enums.IdemStrategy;
import com.shemuel.idempotent.enums.RespStrategy;
import com.shemuel.idempotent.handler.IdemHandler;
import com.shemuel.idempotent.handler.RespHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等策略
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Strategy {

    /**
     * 幂等策略
     */
    IdemStrategy idemStrategy() default IdemStrategy.DEFAULT;

    /**
     * 自定义幂等策略时，自定义handler获取方式
     * 支持 ElementType.METHOD, ElementType.TYPE
     * ElementType.METHOD： 表示从类中的方法获取自定义实现 public IdempotentResult<R> methodName(IdempotentContext<R> context)
     * ElementType.TYPE： 表示注入bean获取，bean需实现{@link IdemHandler}
     */
    ElementType idemType() default ElementType.TYPE;

    /**
     * 自定义响应策略时，自定义handler获取方式
     * ElementType.METHOD： 方法名
     * ElementType.TYPE： bean名称
     */
    String idemHandlerName() default "";

    /**
     * 响应策略
     */
    RespStrategy respStrategy() default RespStrategy.NULL;

    /**
     * 自定义响应策略时，自定义handler获取方式
     * 支持 ElementType.METHOD, ElementType.TYPE
     * ElementType.METHOD： 表示从类中的方法获取自定义实现 public R methodName(IdempotentContext<R> context)
     * ElementType.TYPE： 表示注入bean获取，bean需实现{@link RespHandler}
     */
    ElementType respType() default ElementType.TYPE;

    /**
     * 自定义响应策略时，自定义handler获取方式
     * ElementType.METHOD： 方法名
     * ElementType.TYPE： bean名称
     */
    String respHandlerName() default "";

}
