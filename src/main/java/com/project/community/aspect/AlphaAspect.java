package com.project.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 15:18
 */
@Component
@Aspect
public class AlphaAspect {

//    @Pointcut("execution(* com.project.community.service.*.*(..))")
//    public void pointcut(){
//
//    }
//
//    @Before("pointcut()")
//    public void before() {
//        System.out.println("before...");
//    }
//
//    @After("pointcut()")
//    public void after() {
//        System.out.println("after...");
//    }
//
//    @AfterReturning("pointcut()")
//    public void afterReturning() {
//        System.out.println("after returning...");
//    }
//
//    @AfterThrowing("pointcut()")
//    public void afterThrowing() {
//        System.out.println("after throwing...");
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint pjp) throws Throwable {
//        System.out.println("around before...");
//        Object obj = pjp.proceed();
//        System.out.println("around after...");
//        return obj;
//    }

}
