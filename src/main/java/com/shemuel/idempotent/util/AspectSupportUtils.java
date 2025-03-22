package com.shemuel.idempotent.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

/**
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AspectSupportUtils {

    private static final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    /**
     * 获取切面方法上的注解
     */
    public static <A extends Annotation> A getAnnoOnMethod(JoinPoint joinPoint, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(((MethodSignature) joinPoint.getSignature()).getMethod(), annotationType);
    }

    /**
     * 获取切面类上的注解
     */
    public static <A extends Annotation> A getAnnoOnType(JoinPoint joinPoint, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(joinPoint.getTarget().getClass(), annotationType);
    }

    /**
     * 解析EL表达式
     */
    public static Object getKeyValue(JoinPoint joinPoint, String keyExpression) {
        return getKeyValue(joinPoint.getTarget(), joinPoint.getArgs(), joinPoint.getTarget().getClass(),
                ((MethodSignature) joinPoint.getSignature()).getMethod(), keyExpression);
    }

    private static Object getKeyValue(Object object, Object[] args, Class<?> clazz, Method method, String keyExpression) {
        if (StringUtils.hasText(keyExpression)) {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(object, clazz, method, args);
            AnnotatedElementKey methodKey = new AnnotatedElementKey(method, clazz);
            return evaluator.key(keyExpression, methodKey, evaluationContext);
        }
        return SimpleKeyGenerator.generateKey(args);
    }

}