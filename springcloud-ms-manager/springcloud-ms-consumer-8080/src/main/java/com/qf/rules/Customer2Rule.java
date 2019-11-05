package com.qf.rules;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对多服务负载均衡策略
 */
public class Customer2Rule extends AbstractLoadBalancerRule {

        private int limit = 5;

    // map的key是服务名， Server是对应的服务名调用的服务
    private Map<String, Server> serverMap = new HashMap<>();

    // key是服务名，例如ms-provider, value是这个调用次数
    private Map<String, AtomicInteger> numMap = new HashMap<>();


    @Override
    public Server choose(Object key) {

        ILoadBalancer loadBalancer = getLoadBalancer();

        // 获取当前调用的服务对应的集群列表
        List<Server> serverList = loadBalancer.getReachableServers();   // A  B   B  A


        String serverName = null;

        int size = serverList.size();

        if(serverList != null && size > 0) {
            Server server = serverList.get(0);  //获取到服务
            serverName = server.getMetaInfo().getAppName();  //获取服务名字， 因为serverMap与numMap都用到这个服务名

            server = serverMap.get(serverName);  //获取服务，但是可能为空，表示从来没有调用过

            if(server == null) {
                // 当前服务如果没有，表示第一次调用
                serverMap.put(serverName, serverList.get(0));
                numMap.put(serverName, new AtomicInteger(1));
                return serverList.get(0);
            }else {  //调用的服务之前使用过

                //获取当前服务调用的次数
                int num = numMap.get(serverName).get();

                // 表示已经满了5次
                if(num >= 5) {
                    /**
                     * 有无下一个，如果有直接下一个，如果没有就调用第一个服务，次数归零
                     */
                    for(int i = 0; i < size; i++) { // 判断有没有下一个
                        Server retriverServer = serverList.get(i);
                        if(server.getId().equals(retriverServer.getId())) {  //判断的目的是获取索引信息
                            if(i == (size - 1)) { //表示为最后一个
                                serverMap.put(serverName, serverList.get(0)); //取第一个
                                numMap.put(serverName, new AtomicInteger(1)); //次数重新 回 1
                                return serverList.get(0);
                            }else {
                                serverMap.put(serverName, serverList.get(i + 1));
                                numMap.put(serverName, new AtomicInteger(1)); //次数重新 回 1
                                return serverList.get(i + 1);
                            }
                        }
                    }
                }else {
                    numMap.get(serverName).incrementAndGet(); //将服务调用次数 +1
                    return server; //没满5次直接返回
                }
            }
        }

        return null;
    }
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }
}
