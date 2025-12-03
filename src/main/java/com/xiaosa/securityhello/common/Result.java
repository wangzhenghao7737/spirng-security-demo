package com.xiaosa.securityhello.common;

import lombok.Data;

/**
 * 统一返回数据格式
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> ok(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
