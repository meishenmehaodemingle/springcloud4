package com.qf.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * gbk
 * utf-8
 * iso8859-1
 * gb2312
 */
@Component
public class MyZuulFilter extends ZuulFilter {

    /**
     * pre     在达到具体的服务之前
     * post    在达到具体的服务之后
     * error   抛出异常
     * route   具体服务在执行的过程中
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器执行的顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 是否过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 执行具体的过滤功能
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        // token
        //获取RequestContext对象，目的是只能通过该对象来获取 request、response对象
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        String token = request.getHeader("token");

        if(token == null || "".equals(token.trim())) {
            // 将其设置为false, 目的是用我们自己定义的响应格式
            context.setSendZuulResponse(false);

            context.setResponseBody("一个不合法的请求.");
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("gbk");
        }

        return null;
    }
}
