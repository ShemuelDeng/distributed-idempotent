package com.shemuel.idempotent.util;

import com.shemuel.idempotent.enums.IdemStrategy;
import com.shemuel.idempotent.handler.RespHandler;
import com.shemuel.idempotent.aop.anno.Strategy;
import com.shemuel.idempotent.enums.RespStrategy;
import com.shemuel.idempotent.handler.IdemHandler;
import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.model.IdempotentResult;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.ReflectionUtils;

/**
 * 幂等处理器工具类，提供获取幂等策略处理器和响应策略处理器的静态方法。
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdemHandlerUtils {

    public static <R> IdemHandler<R> getIdemByStrategy(String name) {
        try {
            return SpringContextHolder.getBean(name);
        } catch (Exception e) {
            log.error("幂等组件：获取自定义幂等策略Bean({})失败: 已切换为默认处理器", name, e);
            return SpringContextHolder.getBean(IdemStrategy.DEFAULT.getName());
        }
    }

    public static <R> RespHandler<R> getRespByStrategy(String name) {
        try {
            return SpringContextHolder.getBean(name);
        } catch (Exception e) {
            log.error("幂等组件：获取自定义响应策略Bean({})失败: ", name, e);
            return null;
        }
    }

    public static <R> IdemHandler<R> getIdemHandler(ProceedingJoinPoint joinPoint, Strategy anno) {
        if (!IdemStrategy.CUSTOM.equals(anno.idemStrategy())) {
            return null;
        }
        return ElementType.TYPE.equals(anno.idemType()) ? getIdemByStrategy(anno.idemHandlerName()) : getIdemByMethod(joinPoint, anno);
    }

    public static <R> RespHandler<R> getRespHandler(ProceedingJoinPoint joinPoint, Strategy anno) {
        if (!RespStrategy.CUSTOM.equals(anno.respStrategy())) {
            return null;
        }
        return ElementType.TYPE.equals(anno.respType()) ? getRespByStrategy(anno.respHandlerName()) : getRespByMethod(joinPoint, anno);
    }

    private static <R> IdemHandler<R> getIdemByMethod(ProceedingJoinPoint joinPoint, Strategy anno) {
        try {
            Object target = joinPoint.getTarget();
            Method method = target.getClass().getDeclaredMethod(anno.idemHandlerName(), IdempotentContext.class);
            method.setAccessible(true);
            return invokeIdemHandler(target, method);
        } catch (Exception e) {
            log.error("幂等组件：获取自定义幂等策略 by Method({})失败: ", anno.idemHandlerName(), e);
            return null;
        }
    }

    private static <R> IdemHandler<R> invokeIdemHandler(Object target, Method method) {
        return new IdemHandler<R>() {
            @Override
            public IdempotentResult<R> doHandle(IdempotentContext<R> context) {
                return (IdempotentResult<R>) ReflectionUtils.invokeMethod(method, target, context);
            }
        };
    }

    private static <R> RespHandler<R> getRespByMethod(ProceedingJoinPoint joinPoint, Strategy anno) {
        try {
            Object target = joinPoint.getTarget();
            Method method = target.getClass().getDeclaredMethod(anno.respHandlerName(), IdempotentContext.class);
            method.setAccessible(true);
            return invokeRespHandler(target, method);
        } catch (Exception e) {
            log.error("幂等组件：获取自定义响应策略 by Method({})失败: ", anno.respHandlerName(), e);
            return null;
        }
    }

    private static <R> RespHandler<R> invokeRespHandler(Object target, Method method) {
        return new RespHandler<R>() {
            @Override
            public R doHandle(IdempotentContext<R> context) {
                return (R) ReflectionUtils.invokeMethod(method, target, context);
            }
        };
    }

}