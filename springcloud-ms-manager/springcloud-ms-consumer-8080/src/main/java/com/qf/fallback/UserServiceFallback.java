package com.qf.fallback;

import com.qf.pojo.User;
import com.qf.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserServiceFallback implements UserService {

    @Override
    public List<User> getAll() {
        User user = new User();
        user.setId(-1);
        return Arrays.asList(user);
    }
}
