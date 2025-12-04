package com.xiaosa.securityhello.common;

import lombok.Data;

/**
 * 统一返回数据格式
 */
@Data
public class Result<T> {
    /**
     * 响应状态码
     */
    private int code=200;
    /**
     * 响应提示消息
     */
    private String msg="OK";
    /**
     * 响应内容
     */
    private Object data;

    public Result() {
    }

    public Result(Object data) {
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result ok(){
        return new Result();
    }

    public static Result ok(String msg){
        return new Result(200,msg);
    }

    public static Result ok(Object data){
        return new Result(data);
    }
    public static Result error(String msg){
        return new Result(-1,msg);
    }

}
