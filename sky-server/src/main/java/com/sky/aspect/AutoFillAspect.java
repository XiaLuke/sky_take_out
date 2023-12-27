package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;

@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /*
     * 定义切入点
     * */
    @Pointcut("execution(* com.sky.service.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /*
     * 定义通知
     * 在执行insert和update方法之前进行自动填充
     * */
    @Before("autoFillPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("开始自动填充");
        // 1.获取注解中操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotations = signature.getMethod().getAnnotation(AutoFill.class);
//        String operateType = annotations.value().name();
        OperationType operateType = annotations.value();

        // 获取拦截方法中的操作实体
        Object entity = joinPoint.getTarget();

        Long currentUserId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        // 对字段赋值
        if (OperationType.INSERT.equals(operateType)) {
            try {
                Method setCreateTime = entity.getClass().getMethod(SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getMethod(SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getMethod(SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getMethod(SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentUserId);
                setUpdateUser.invoke(entity, currentUserId);
            } catch (Exception e) {

            }
        } else if (OperationType.UPDATE.equals(operateType)) {
            try {
                Method setUpdateTime = entity.getClass().getMethod(SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getMethod(SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentUserId);
            } catch (Exception e) {

            }
        }
    }

}
