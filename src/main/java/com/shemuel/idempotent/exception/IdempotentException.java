package com.shemuel.idempotent.exception;

/**
 * @author: 公众号： 加瓦点灯
 * @date: 2025/3/22 16:08
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }
}