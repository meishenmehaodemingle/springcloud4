package com.qf.service;

import com.qf.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;

@FeignClient(name="ms-provider02")
public interface OtherUserService {

    @RequestMapping("/user/{id}")
    public User getOne(@PathVariable("id") Integer integer);
}
