package com.dikkak.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class AspectCustom {

    @Pointcut("within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *)")
    public void beanPointcut() {}

    // Repository, Service, RestController 어노테이션이 붙은 클래스의 메서드에 로깅 추가
    @Around("beanPointcut()")
    public Object loggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("{} args={}", joinPoint.getSignature(), joinPoint.getArgs());
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("{} args={} error={}", joinPoint.getSignature(), joinPoint.getArgs(), e.toString());
            throw e;
        }
    }

}
