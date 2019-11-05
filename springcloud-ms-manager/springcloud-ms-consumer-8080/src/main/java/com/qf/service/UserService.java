package com.qf.service;

import com.qf.fallback.UserServiceFallback;
import com.qf.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name="ms-provider", fallback = UserServiceFallback.class)
public interface UserService {

    @RequestMapping("/user")
    public List<User> getAll();
}
