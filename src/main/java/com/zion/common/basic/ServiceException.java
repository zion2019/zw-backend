package com.zion.common.basic;

import lombok.Getter;

/**
 * 自定义异常格式
 */
@Getter
public class ServiceException extends RuntimeException {
    private int code;
    private String msg;

    public ServiceException() {
        this(499, "业务处理错误");
    }

    public ServiceException(String msg) {
        this(499, msg);
    }

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
