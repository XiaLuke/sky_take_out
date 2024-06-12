package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 在AOP中，有切面，切点，通知等概念
 *
 * 其中通知用来增强代码，切点用来定义切面的范围，切面用来定义通知和切点的关系
 *
 * 通知分为，前置通知，后置通知，环绕通知，异常通知，最终通知
 * */
@Component
@Slf4j
@Aspect
public class AutoFillAspect{
    // 拦截mapper包中且被@AutoFill注解标记的方法
    @Pointcut(
            value= "execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)"
    )
    public void autoFillField(){

    }

    /**
     * 要为公共字段赋值并入库，需要在前置通知获取进行处理
     * */
    @Before("autoFillField()") // 前置通知，当匹配上autoFillField()切点时，执行before方法
    public void before(JoinPoint joinPoint){
        // 1. 获取mapper方法上注解类型
        // joinPoint.getSignature() 并向下转型获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 2. 获取mapper方法中参数-实体
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        // 获取第一个参数-实体
        Object obj = args[0];

        // 3.处理相应字段，当前登录人，当前时间
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 4. 根据不同的操作类型，为对应属性通过反射赋值
        if(operationType == OperationType.INSERT){
            try {
                obj.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class).invoke(obj, now);
                obj.getClass().getDeclaredMethod("setCreateUser", Long.class).invoke(obj, currentId);
                obj.getClass().getDeclaredMethod("setUpdateUser", Long.class).invoke(obj, currentId);
                obj.getClass().getDeclaredMethod("setUpdateTIme", LocalDateTime.class).invoke(obj, now);
            }catch (Exception e){
                log.error("反射赋值异常",e);
            }
        }else if(operationType == OperationType.UPDATE){
            // 更新操作
            try{
                obj.getClass().getDeclaredMethod("setUpdateUser", Long.class).invoke(obj, currentId);
                obj.getClass().getDeclaredMethod("setUpdateTIme", LocalDateTime.class).invoke(obj, now);
            }catch (Exception e) {
                log.error("反射赋值异常", e);
            }
        }
        log.info("before");
    }
}
