package com.songdesy.common.interceptor;


import com.songdesy.untils.IpUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Date: 18-5-5 下午7:00
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class IpInterceptor extends HandlerInterceptorAdapter {
    private static final String X_FORWARDED_FOR = "x-forwarded-for";

    public IpInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.handleClientIp(request);
        return true;
    }

    private void handleClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (null == ip) {
            ip = request.getRemoteAddr();
        }

        String realIp = ip.split(",")[0];
        IpUtils.setClientIp(realIp);
    }
}
