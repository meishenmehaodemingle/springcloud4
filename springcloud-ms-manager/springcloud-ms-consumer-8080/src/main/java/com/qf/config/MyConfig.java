package com.qf.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.qf.rules.Customer2Rule;
import com.qf.rules.CustomizeRule;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /***/
    @Bean
    public IRule randomRule() {
//        return new RandomRule();
        return new Customer2Rule();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        // 该Servlet是处理Hystrix监控
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();

        ServletRegistrationBean<HystrixMetricsStreamServlet> servlets = new ServletRegistrationBean<>();

        servlets.setServlet(streamServlet);
        servlets.setName("streamServlet");
        servlets.addUrlMappings("/actuator/hystrix.stream");
        servlets.setLoadOnStartup(2);

        /**
        <servet>
            <servlet-name>streamServlet</servlet-name>
            <serlvet-class>HystrixMetricsStreamServlet</servlet-class>
            <load-on-starup>2</load-on-startup>
        </servlet>
         <servlet-mapping>
         <servlet-name>streamServlet</servlet-name>
         <url-pattern>/actuator/hystrix.stream</url-pattern>
         </servlet-mapping>

        */
        return servlets;
    }
}
