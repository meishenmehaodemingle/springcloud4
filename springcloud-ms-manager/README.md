## Spring Cloud

### 一. Eureka简介

​        Eureka是Netflix开发的服务发现框架，本身是一个基于REST的服务，主要用于定位运行在AWS域中的中间层服务，以达到负载均衡和中间层服务故障转移的目的。SpringCloud将它集成在其子项目spring-cloud-netflix中，以实现SpringCloud的服务发现功能。

​	Eureka包含两个组件：Eureka Server和Eureka Client。

​	Eureka Server提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。

​	Eureka Client是一个java客户端，用于简化与Eureka Server的交互，客户端同时也就别一个内置的、使用轮询(round-robin)负载算法的负载均衡。

![](images/0.png)

#### 1.1 Eureka Server的搭建

##### 1.1.1 依赖配置

```
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.1.6.RELEASE</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-dependencies</artifactId>
           <!-- 在写版本的时候，不能像官网那样写 Greenwich SR2， -->
           <version>Greenwich.SR2</version>
           <type>pom</type>
           <scope>import</scope>
         </dependency>
     </dependencies>
</dependencyManagement>
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			<configuration>
				<mainClass>com.example.Application</mainClass>
			</configuration>
			<executions>
				<execution>
					<goals>
						<goal>repackage</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

##### 1.1.2 application.yml配置

```
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:${server.port}/eureka/
```

##### 1.1.3  代码

```
@SpringBootApplication
@EnableEurekaServer               //启用Eureka的服务
public class EurekaServerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

##### 1.1.4 访问页面

![](images/1.png)

#### 1.2 服务提供方

##### 1.2.1 依赖配置

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

##### 1.2.2 application.yml配置

```
eureka:
  instance:
    prefer-ip-address: true
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

##### 1.2.3 代码编写

```
@SpringBootApplication
@EnableEurekaClient   //启动Eureka客户端
public class ShopProviderApplication {
    public static void main( String[] args){
        SpringApplication.run(ShopProviderApplication.class, args);
    }
}
```

##### 1.2.4 页面查看

![](images/2.png)

#### 1.3 服务消费方

##### 1.3.1 application.yml配置

```
spring:
  application:
    name: shop-consumer
server:
  port: 8080
eureka:
  client:
    register-with-eureka: false
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

##### 1.3.2 代码的编写

启动类代码：

```
@SpringBootApplication
public class ShopConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopConsumerApplication.class, args);
    }
}
```

调用类代码：

```
@RestController
@RequestMapping(value="/user")
public class UserController {

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping(value = "/ticket/{id}", method = RequestMethod.GET)
    public Object getTicket(@PathVariable(value = "id") Integer id) {


        Person person = new Person();
        person.setId(23);
        person.setName("张三三");
       
        List<Ticket> ticketList = restTemplate.postForEntity("http://localhost:666/ticket", person, List.class).getBody();
        return ticketList;
    }
}
```

RestTemplate的注入：

```
@Configuration
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### 二. Ribbon

​	Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。其主要功能是提供客户端的负载均衡算法，并提供了完善的配置项如连接超时，重试等。简单的说，就是配置文件中列出Load Balancer后面所有的机器，Ribbon会自动的基于某种规则(如简单轮询，随机连接等)去连接这些机器，当然我们也可以使用Ribbon自定义负载均衡算法。Ribbon的实现需要使用的Eureka，消费方需要在Eureka注册中心找到要调用的服务的相关信息。

#### 2.1 配置依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

#### 2.2 实现负载均衡

​	Ribbon只是一个客户端的负载均衡器工具，实现起来非常的简单，我们只需要在注入RestTemplate的bean上加上@LoadBalanced就可以了。如下：

```
@Configuration
public class BeanConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### 2.3 启动类配置

```
@SpringBootApplication
@EnableEurekaClient
public class ShopConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopConsumerApplication.class, args);
    }
}
```

#### 2.4 服务的调用

​	在服务的消费方，不再采用主机名+端口的形式进行调用，而是直接采用服务名的方式进行调用。

```
@RestController
@RequestMapping(value="/user")
public class UserController {
    @Resource
    private RestTemplate restTemplate;

    @RequestMapping(value = "/ticket/{id}", method = RequestMethod.GET)
    public Object getTicket(@PathVariable(value = "id") Integer id) {

        Person person = new Person();
        person.setId(23);
        person.setName("张三三");
       
        // shop-provider 是服务名，不需要使用ip:端口的形式进行调用
        List<Ticket> ticketList = restTemplate.getForObject("http://shop-provier/ticket", List.class, person);
        return ticketList;
    }
}
```

#### 2.5 负载均衡策略

​	Ribbon提供了一个很重要的接口叫做IRule，其中定义了很多的负载均衡策略，默认的是轮询的方式，以下是Ribbon的负载均衡策略：

| 类名                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| RoundRobbinRule           | 轮询                                                         |
| RandomRule                | 随机挑选                                                     |
| RetryRule                 | 按照轮询的方式去调用服务，如果其中某个服务不可用，但是还是会尝试几次，如果尝试过几次都没有成功，那么就不在调用该服务，会轮询调用其他的可用服务。 |
| AvailabilityFilteringRule | 会先过滤掉因为多次访问不可达和并发超过阈值的服务，然后轮询调用其他的服务 |
| WeightedResponseTimeRule  | 根据平均响应时间计算权重，响应越快权重越大，越容易被选中。服务刚重启的时候，还未统计出权重会按照轮询的方式；当统计信息足够的时候，就会按照权重信息访问 |
| ZoneAvoidanceRule         | 判断server所在的区域性能和可用性选择服务器                   |
| BestAvailableRule         | 会过滤掉多次访问都不可达的服务，然后选择并发量最小的服务进行调用，默认方式 |

​	改变Ribbon的负责均衡策略：

```
@Bean
public IRule getRule() {
    return new RandomRule();
}
```

#### 2.6 自定义负载均衡策略

​	我们自定义的负载均衡策略需要继承AbstractLoadBalancerRule这个类，然后重写choose方法，然后将其注入到容器中，如下所示：

```
public class Customize_Rule extends AbstractLoadBalancerRule {

    private static Logger logger = LoggerFactory.getLogger(Customize_Rule.class);

    private int currentIndex = 0; //当前调用的索引
    private int num = 1; //次数
    private int limit = 5;

    /**
     * 初始化工作
     * @param iClientConfig
     */
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        ILoadBalancer balancer = getLoadBalancer();
        return choose(balancer, key);
    }

    private Server choose(ILoadBalancer balancer, Object key) {
        Server server = null;

        while(null == server) {
            //获取所有可用的服务
            List<Server> reachableServers = balancer.getReachableServers();
            if (0 == reachableServers.size()) {
                logger.error("没有可用的服务");
                return null;  //退出while循环
            }

            int total = reachableServers.size(); //可用服务的数量

            synchronized (this) {
                /**
                 * 有种极端情况，当我们在使用最后一个服务的时候，其他的服务都不可用，可能导致索引越界异常
                 */
                if (currentIndex + 1 > total) {
                    currentIndex = 0;
                    server = reachableServers.get(currentIndex);  //获取第一个服务
                    num = 0;
                    num++;
                } else {
                    if(limit == num) {
                        currentIndex++;
                        num = 0;
                        if(currentIndex == total) {
                            currentIndex=0;
                            server = reachableServers.get(currentIndex);  //获取第一个服务
                            num++;
                        }else{
                            server = reachableServers.get(currentIndex);
                            num++;
                        }
                    }else {
                        server = reachableServers.get(currentIndex);
                        num++;
                    }
                }
            }
        }
        return server;
    }
}
```

​	将其注入到容器中，如下所示：

```
@Bean
public IRule getRule() {
    return new Customize_Rule();
}
```

### 三. Feign负载均衡

​	feign是基于Ribbon的另外一个负载均衡的客户端框架，只需要在接口上定义要调用的服务名即可，使用起来非常的简单。

3.1 添加依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

3.2 启动类的配置

​	需要在启动类上加上@EnableFeignClients注解即可开启feign，如下所示：

```
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ShopConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopConsumerApplication.class, args);
    }
}
```

3.3 服务接口配置与调用方式

```
@Service
@FeignClient(name = "shop-provier")
public interface TicketService {

    @RequestMapping(value = "ticket", method = RequestMethod.GET)
    public List<Ticket> getAllTicket(Person person);
}
```

### 四. Hystrix断路器

​	分布式系统中一个微服务需要依赖于很多的其他的服务，那么服务就会不可避免的失败。例如A服务依赖于B、C、D等很多的服务，当B服务不可用的时候，会一直阻塞或者异常，更不会去调用C服务和D服务。同时假设有其他的服务也依赖于B服务，也会碰到同样的问题，这就及有可能导致雪崩效应。

​	如下案例：一个用户通过通过web容器访问应用，他要先后调用A、H、I、P四个模块，一切看着都很美好。

![](images/3.png)

​      由于某些原因，导致I服务不可用，与此同时我们没有快速处理，会导致该用户一直处于阻塞状态。

![](images/4.png)

​      当其他用户做同样的请求，也会面临着同样的问题，tomcat支持的最大并发数是有限的，资源都是有限的，将整个服务器拖垮都是有可能的。

![](images/5.png)

​	Hystrix是一个用于分布式系统的延迟和容错的开源库，在分布式系统中，许多依赖会不可避免的调用失败，例如超时，异常等，Hystrix能保证在一个依赖出现问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。

​	断路器本身是一种开关装置，当某个服务单元发生故障后，通过断路器的故障监控（类似于保险丝），向调用者返回符合预期的，可处理的备选响应，而不是长时间的等待或者抛出无法处理的异常，这样就保证了服务调用的线程不会被长时间，不必要的占用，从而避免故障在分布式系统中的蔓延，乃至雪崩。

​	Hystrix在网络依赖服务出现高延迟或者失败时，为系统提供保护和控制;可以进行快速失败，缩短延迟等待时间；提供失败回退（Fallback）和相对优雅的服务降级机制；提供有效的服务容错监控、报警和运维控制手段。

#### 4.1 配置依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### 4.2 application.yml配置

```
feign:
  hystrix:
    enabled: true  #开启feign的熔断机制
```

#### 4.3 启动类配置

​	只需要在启动类上加上@EnableCircuitBreaker注解即可，如下所示：

```
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
public class ShopConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopConsumerApplication.class, args);
    }
}
```

#### 4.4 接口的编写

​	在接口的@FeignClient注解中加入fallback参数，该参数为Class类型的对象，我们将该接口实现，作为服务降级后的快速响应，然后提供给fallback作为参数的值，如下所示：

```
@Service
@FeignClient(name = "shop-provier", fallback = TicketServiceFallback.class)
public interface TicketService {

    @RequestMapping(value = "ticket", method = RequestMethod.GET)
    public List<Ticket> getAllTicket(Person person);
}
```

TicketServiceFallback是对TicketService这个接口的实现，用于在服务降级后的一个快速响应，代码如下：

```
@Component
public class TicketServiceFallback implements TicketService {

    @Override
    public List<Ticket> getAllTicket(Person person) {
        return Arrays.asList(new Ticket());
    }
}
```

#### 4.5 Hystrix监控

##### 4.5.1 配置依赖

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

##### 4.5.2 启动类配置

​	在启动类上加上@EnableHystrixDashboard注解，如下图所示：

```
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
public class ShopConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopConsumerApplication.class, args);
    }
}
```

##### 4.5.3 编写servlet入口

```
@Bean
public ServletRegistrationBean getServlet() {

    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
    ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
    registrationBean.setLoadOnStartup(1);
    registrationBean.addUrlMappings("/actuator/hystrix.stream");
    registrationBean.setName("HystrixMetricsStreamServlet");
    return registrationBean;
}
```

##### 4.5.4 访问Hystrix Dashboard

![](images/6.png)

​	在输入框中输入：http://locahost:8080/actuator/hystrix.stream

![](images/7.png)









