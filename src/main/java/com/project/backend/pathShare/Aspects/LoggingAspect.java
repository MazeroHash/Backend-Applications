package com.project.backend.pathShare.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Aspect
public class LoggingAspect {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.project.backend.pathShare..*(..)")
    public void applicationPackagePointcut(){}

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint){
        logger.info("🔎 [BEFORE] Executing method: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature(),
                Arrays.toString(joinPoint.getArgs()));
    }
    @AfterReturning(value = "applicationPackagePointcut()",returning = "result")
    public void logAfterReturning(JoinPoint joinPoint,Object result){
        logger.info("[RETURN] Method: {}.{}() returned: {}",
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName(),
                result);
    }
    @AfterThrowing(value = "applicationPackagePointcut()",throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint,Throwable exception){
        logger.error(" [EXCEPTION] Method: {}.{}() threw: {}",
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName(),
                exception.getMessage(),exception);
    }
    @After("applicationPackagePointcut()")
    public void logAfter(JoinPoint joinPoint){
        logger.info("[AFTER] Completed method: {}.{}()",
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName());
    }
}
