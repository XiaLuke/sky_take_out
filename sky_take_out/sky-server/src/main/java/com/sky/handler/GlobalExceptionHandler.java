package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获BaseException异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQLIntegrityConstraintViolationException异常，数据库插入数据时，违反了唯一约束
     *
     * @param ex
     * @return {@link Result}
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        String result = null;
        if(message.contains("Duplicate entry")){
            //获取唯一约束的字段名
            String[] splits = message.split(" ");
            String key = splits[2];
            result = key + "已存在";
        }else{
            result = "未知错误";
        }
        return Result.error(result);
    }
}
