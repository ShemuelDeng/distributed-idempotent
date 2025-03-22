package com.shemuel.idempotent.aop.aspect;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.shemuel.idempotent.aop.anno.Idempotent;
import com.shemuel.idempotent.executor.IdempotentCallback;
import com.shemuel.idempotent.executor.IdempotentExecutor;
import com.shemuel.idempotent.model.IdempotentContext;
import com.shemuel.idempotent.util.AspectSupportUtils;
import com.shemuel.idempotent.util.IdemHandlerUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

/**
 * 切面类
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@Aspect
@Order(-1)
@Slf4j
public class IdempotentAspect {

    @Resource
    private IdempotentExecutor executor;

    @Around("@annotation(anno)")
    public final Object proceed(ProceedingJoinPoint joinPoint, Idempotent anno) throws Throwable {
        // 所有的参数值的数组
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 幂等增强 委托给IdempotentExecutor
        Object retObj = executor.execute(new IdempotentCallback<Object>() {
            @Override
            public IdempotentContext<Object> initContext() throws Throwable {
                return new IdempotentContext<>()
                        .setUid(buildUid(joinPoint, signature, anno))
                        .setDuration(anno.duration())
                        .setArgMap(buildArgMap(joinPoint, signature))
                        .setIdemParam(buildIdemParam(joinPoint, anno))
                        .setFailFast(anno.failFast())
                        .setIdemStrategy(anno.custom().idemStrategy())
                        .setIdemHandler(IdemHandlerUtils.getIdemHandler(joinPoint, anno.custom()))
                        .setRespStrategy(anno.custom().respStrategy())
                        .setRespHandler(IdemHandlerUtils.getRespHandler(joinPoint, anno.custom()));
            }

            @Override
            public Object execute() throws Throwable {
                return joinPoint.proceed();
            }
        });
        return JSON.parseObject(JSON.toJSONString(retObj), (Class<?>) signature.getReturnType());
    }

    private String buildUid(ProceedingJoinPoint joinPoint, MethodSignature signature, Idempotent idempotent) throws Exception {
        if (StringUtils.isNotBlank(idempotent.uid())) {
            return idempotent.uid();
        }
        Class<?> typeClz = joinPoint.getTarget().getClass();
        Method method = typeClz.getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        //获取目标方法(类全名+方法名)
        return typeClz.getName() + ":" + method.getName();
    }

    private Map<String, Object> buildArgMap(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        Object[] args = joinPoint.getArgs();
        String[] params = signature.getParameterNames();
        Map<String, Object> map = Maps.newHashMap();
        if (ArrayUtils.isEmpty(params)) {
            return map;
        }
        for (int i = 0; i < params.length; i++) {
            map.put(params[i], args[i]);
        }
        return map;
    }

    private List<Object> buildIdemParam(ProceedingJoinPoint joinPoint, Idempotent anno) {
        return isEmpty(anno.columns()) ? generateIdemParam(joinPoint.getArgs()) : generateIdemParam(joinPoint, anno.columns());
    }

    /**
     * 获取所有的参数信息
     */
    private List<Object> generateIdemParam(Object[] args) {
        return args == null || args.length == 0 ? Collections.emptyList() : Arrays.asList(args);
    }

    /**
     * 获取指定的幂等参数信息
     */
    private List<Object> generateIdemParam(JoinPoint joinPoint, String[] columns) {
        return Arrays.stream(columns)
                .filter(StringUtils::isNotBlank)
                .map(column -> AspectSupportUtils.getKeyValue(joinPoint, column))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 判断IdempotentColumns是不是空的（如果里边的字符串都是空的，也认为是空的）
     */
    private static boolean isEmpty(String[] columns) {
        return ArrayUtils.isEmpty(columns) || Arrays.stream(columns).allMatch(StringUtils::isBlank);
    }

}