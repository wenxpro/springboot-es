package com.example.logbacklogstash.base;

import lombok.Getter;

/**
 * 返回结果enum
 * @author wenx
 * @date 2020-09-30
 */
@Getter
public enum ResultEnum {
    //结果
    success("1"),fail("0"),error("2");

    private String code;

    ResultEnum(String code) {
        this.code = code;
    }

}
