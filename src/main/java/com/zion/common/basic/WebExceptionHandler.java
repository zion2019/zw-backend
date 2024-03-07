package com.zion.common.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public R apiExceptionHandler(ServiceException e) {
        log.error("service exception", e);
        // 注意哦，这里返回类型是自定义响应体
        return R.error(e.getCode(),e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        // 构建ErrorResponse对象并返回合适的HTTP状态码
        log.error("service exception", e);
        // 注意哦，这里返回类型是自定义响应体
        return R.error(e.getMessage());
    }
}
