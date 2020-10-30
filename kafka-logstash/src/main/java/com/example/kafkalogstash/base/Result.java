package com.example.kafkalogstash.base;

import lombok.Data;

/**
 * 通用结果
 * @author wenx
 * @date 2020-09-30
 */
@Data
public class Result {

    private String code;

    private String msg;
//    private Object data;

    public Result() {
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result error(){
        return this.render(ResultEnum.error.getCode(),"error");
    }

    public Result error(String msg){
        return this.render(ResultEnum.error.getCode(),msg);
    }

    public Result fail(String msg){
        return this.render(ResultEnum.fail.getCode(),msg);
    }

    public Result fail(){
        return this.render(ResultEnum.fail.getCode(),"fail");
    }

    public Result fail(Object data){
        return this.render(ResultEnum.fail.getCode(),"fail",data);
    }

    public Result success(){
        return this.render(ResultEnum.success.getCode(),"success");
    }
    public Result success(Object data){
        return this.render(ResultEnum.success.getCode(),"success",data);
    }
    public Result success(String msg){
       return this.render(ResultEnum.success.getCode(),msg);
    }

    public Result render(String code, String msg){
        this.code = code;
        this.msg = msg;
        return this;
    }

    public Result render(String ret, String msg, Object data){
        this.code = ret;
        this.msg = msg;
//        this.data = data;
        return this;
    }
}
