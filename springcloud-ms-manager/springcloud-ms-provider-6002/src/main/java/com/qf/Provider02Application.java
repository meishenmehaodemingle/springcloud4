package com.qf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableEurekaClient  //表示当前的服务为eureka的客户端
public class Provider02Application {
    public static void main(String[] args) {
        SpringApplication.run(Provider02Application.class, args);
    }
}
