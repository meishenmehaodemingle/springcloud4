package com.qf.controller;

import com.netflix.discovery.converters.jackson.EurekaXmlJacksonCodec;
import com.qf.pojo.User;
import com.qf.service.OtherUserService;
import com.qf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/cuser")
public class ConsumerUserController {

//    @Autowired
//    private RestTemplate restTemplate;

    @Resource
    private UserService userService;

    @Resource
    private OtherUserService otherUserService;

    @RequestMapping
    public Object getRemoteUsers() {

//        List<User> list = restTemplate.getForObject("http://ms-provider/user", List.class);
        List<User> list = userService.getAll();

        System.out.println(otherUserService.getOne(18).getName());

        return list;
    }

    /**
    public static void main(String[] args) {
        String str = null;
        try {

            new Thread(() -> {
                System.out.println("-------------------");
                int a = 10 / 0;

            }).start();

            new Thread(() -> {
                System.out.println("*********************");
                str.substring(2);
            }).start();

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     */
}
