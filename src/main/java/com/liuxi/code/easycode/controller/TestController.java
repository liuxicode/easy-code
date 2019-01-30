package com.liuxi.code.easycode.controller;

import com.liuxi.code.easycode.dao.User;
import com.liuxi.code.easycode.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: liuxi
 * @Date: 2019/1/18 17:24
 * @Description:
 */
@RestController
@RequestMapping("test")
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    @GetMapping("")
    public User getUser(){

        return testService.test();
    }

    @PostMapping("add")
    public Integer addUser(@RequestBody User user){

        return testService.add(user);
    }

    @GetMapping("testSupprt")
    public Integer testSupprt() throws Exception {

        return testService.testDaoSupport();
    }

    @GetMapping("testSupportUpdate")
    public Integer testSupportUpdate() throws Exception{

        return testService.updateSupport();
    }

    @GetMapping("testDaoSupportQuery")
    public Object testDaoSupportQuery() throws Exception{

        Object o = testService.testDaoSupportQuery();

        logger.info("testDaoSupportQuery:{}",o);

        return o;
    }



}
