package com.demo.allframework.bareness.exception;

/**
 * 自定义异常
 */
public class BizException extends RuntimeException{

    public BizException(String message) {
        super(message);
    }
}
