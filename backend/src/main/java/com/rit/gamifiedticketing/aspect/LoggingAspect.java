package com.rit.gamifiedticketing.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Logs execution of all REST controllers
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    // Logs execution of all public methods in services
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    // Before execution of controller methods
    @Before("restControllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("➡️ Incoming API Call: {}.{}() with arguments = {}", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // After execution of controller methods
    @AfterReturning(value = "restControllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("✅ Completed API Call: {}.{}() - Response: {}", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    // Around execution of service methods (logs execution time)
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        logger.info("⚡ Executed Service: {}.{}() in {} ms", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                duration);
        return result;
    }

    // Exception logging
    @AfterThrowing(value = "restControllerMethods() || serviceMethods()", throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Exception ex) {
        logger.error("❌ Exception in {}.{}() - Error: {}", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}
