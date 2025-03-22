package com.shemuel.idempotent.aop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 幂等注解
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 幂等唯一标志，用于区分不同幂等业务，默认使用类名.方法名
     */
    String uid() default "";

    /**
     * 唯一确定一次请求的字段值串，默认使用所有参数，支持EL表达式
     */
    String[] columns() default {};

    /**
     * true：并发的情况下，获取锁失败则直接执行respStrategy false：并发的情况下，获取锁失败则等待获取锁，直到获取到锁
     *
     * @warnning true：RespStrategy.PREVIOUS = RespStrategy.NULL
     */
    boolean failFast() default true;

    /**
     * 自定义策略
     */
    Strategy custom() default @Strategy();

    /**
     * 幂等有效期时间（单位秒，默认3秒），若duration<=0则duration=3，需要持久化的使用CUSTOM策略自己实现
     */
    int duration() default 3;

}
