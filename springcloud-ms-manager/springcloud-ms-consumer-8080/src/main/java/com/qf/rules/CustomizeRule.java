package com.qf.rules;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// 针对单服务的负载均衡策略
public class CustomizeRule extends AbstractLoadBalancerRule {

    private int limit = 5;
    private Server currentServer; //当前调用的服务
    private AtomicInteger num = new AtomicInteger(0); //调用次数

    @Override
    public Server choose(Object key) {

        ILoadBalancer loadBalancer = getLoadBalancer();


//        loadBalancer.getAllServers();  //获取所有服务

        /**
         * List<User> list = userService.getAll();  //ms-provider   1
         *
         * System.out.println(otherUserService.getOne(18).getName());  //ms-provider02
         */

        List<Server> serverList = loadBalancer.getReachableServers(); //获取可用服务


        synchronized (this) {
            if(null == currentServer && serverList != null && serverList.size() > 0) {
                this.currentServer = serverList.get(0);  // 0
                num.getAndIncrement();
                return currentServer;  // 第一次  6001 -> ms-provider
            }
        }


        /**
         * 1.当前服务是否服务是否满5次，如果是调用下一个。
         * 2.如果没有下一个服务，再回到第一个服务。
         */
        int size = serverList.size();
        for(int i = 0; i < size; i++) {

            Server server = serverList.get(i);
            // 判断当前服务是否为正在循环的服务
            if(server.getId().equals(currentServer.getId())) {
                synchronized (this) {
                    if (num.get() >= limit) {
                        num.set(0);  // 调用下一个服务，次数要置零， 重新开始计算

                        //表示当前服务为最后一个服务
                        if (i == (size - 1)) {
                            currentServer = serverList.get(0); //再从头开始调用
                            num.getAndIncrement(); //次数要 +1
                            return currentServer;
                        } else {
                            currentServer = serverList.get(i + 1); //走下一个服务
                            num.getAndIncrement();
                            return currentServer;
                        }
                    } else{
                        num.getAndIncrement();
                        return currentServer;
                    }
                }
            }
        }
        return null;
    }

    // 初始化配置
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }
}
