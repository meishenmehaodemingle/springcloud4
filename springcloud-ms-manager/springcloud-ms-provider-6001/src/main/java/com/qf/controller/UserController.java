package com.qf.controller;

//import com.qf.dao.UserMapper;
import com.qf.dao.UserMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * mybatisplus
 * J2EE: 是由一系列组规范成用于企业级开发的规则。
 * JSP、Servlet、JPA、JMS、JTA
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

//    @Resource
//    private UserRepository userRepository;

    @RequestMapping
    public Object getAll() {
        return userMapper.selectAll();
//        return userRepository.findAll();
    }
}
