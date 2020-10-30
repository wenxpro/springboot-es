package com.example.kafkalogstash.controller;


import com.example.kafkalogstash.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试controller 拟定接口模板
 * @author wenx
 * @date 2020-07-16
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping("/api/test")
    public Result testForm(@RequestParam(value = "param",required = false,defaultValue = "")String param){
       log.debug("param:{}",param);
       return new Result().success();
    }

}
