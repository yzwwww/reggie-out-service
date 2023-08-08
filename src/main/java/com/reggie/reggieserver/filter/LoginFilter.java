package com.reggie.reggieserver.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.reggieserver.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private boolean check(String[] urls, String uri) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match)
                return true;
        }
        return false;
    }

    //定义不需要被拦截的请求
    String[] urls = new String[]{
            "/employee/login/**",
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //2.判断本次请求是否需要处理
        boolean letPass = check(urls, requestURI);
        if (letPass) {
            log.info("本次请求：{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，id为{}", request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

    }
}
