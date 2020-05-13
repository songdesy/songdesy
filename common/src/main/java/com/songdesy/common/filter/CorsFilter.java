package com.songdesy.common.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 少   年   辛   苦   终   身   事
 * 莫   向   光   阴   惰   寸   功
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 *
 * @author : songsong.wu
 * github: https://github.com/songdesy
 * email: callwss@qq.com
 * <p>
 * Date: 2016/4/1516:25
 * Description:
 * Copyright(©) 2015 by songsong.wu.
 **/
@Component
public class CorsFilter implements Filter {


    /**
     * 初始化
     *
     * @param filterConfig filterConfig
     * @throws ServletException ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * CORS 过滤器
     *
     * @param req   rq
     * @param res   res
     * @param chain chin
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, If-Modified-Since");
        chain.doFilter(req, res);
    }


    /**
     * 销毁对象
     */
    @Override
    public void destroy() {
    }
}
