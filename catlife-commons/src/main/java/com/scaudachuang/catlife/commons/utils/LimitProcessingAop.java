package com.scaudachuang.catlife.commons.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hiluyx
 * @since 2021/8/30 10:11
 **/
@Aspect
@Component
public class LimitProcessingAop {

    private volatile ConcurrentHashMap<String, RateLimiter> stringLimiterMap;

    private ConcurrentHashMap<String, RateLimiter> getStringLimiterMap() {
        if (stringLimiterMap == null) {
            synchronized (LimitProcessingAop.class) {
                if (stringLimiterMap == null) {
                    stringLimiterMap = new ConcurrentHashMap<>();
                }
            }
        }
        return stringLimiterMap;
    }

    /*
    * 切点
    * */
    @Pointcut("@annotation(com.scaudachuang.catlife.commons.utils.LimitProcessing)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LimitProcessing annotation = signature.getMethod().getAnnotation(LimitProcessing.class);
        if (annotation != null) {
            RateLimiter rateLimiter = checkRateLimiterContains(annotation);
            boolean b = rateLimiter.tryAcquire();
            if (!b) {
                errorLimitResponse(annotation);
                return null;
            }
        }
        return joinPoint.proceed();
    }

    private RateLimiter checkRateLimiterContains(LimitProcessing annotation) {
        RateLimiter rateLimiter;
        if (!getStringLimiterMap().containsKey(annotation.name())) {
            rateLimiter = RateLimiter.create(annotation.ratePerSec());
            getStringLimiterMap().put(annotation.name(), rateLimiter);
        }
        return getStringLimiterMap().get(annotation.name());
    }

    private void errorLimitResponse(LimitProcessing annotation) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        if (response != null) {
            HttpHelper.errMsgResponse(response, 503, annotation.msg(), null);
        } else {
            throw new Throwable(annotation.msg());
        }
    }
}
