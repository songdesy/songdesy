package com.songdesy.common.filter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 * Date: 18-5-5 下午6:53
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class XssFilter implements Filter {
    private Set<String> excludeUrls = new HashSet();

    public XssFilter() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String servletPath = ((HttpServletRequest) httpServletRequest).getServletPath();
        Stream var10000 = this.excludeUrls.stream();
        servletPath.getClass();
        boolean flag = var10000.noneMatch(s -> servletPath.matches(s.toString()));
        if (flag) {
            httpServletRequest = new XssFilter.XssHttpServletRequestWrapper((HttpServletRequest) request);
        }

        chain.doFilter((ServletRequest) httpServletRequest, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void setExcludeUrls(String urls) {
        if (StringUtils.isNotBlank(urls)) {
            this.addExcludeUrls(org.springframework.util.StringUtils.trimArrayElements(org.springframework.util.StringUtils.tokenizeToStringArray(urls, ",")));
        }

    }

    public void addExcludeUrls(String... urls) {
        if (urls != null && urls.length > 0) {
            this.excludeUrls.addAll((Collection) Arrays.stream(urls).map((u) -> {
                return u.replace("/*", "/[\\.\\w]*");
            }).collect(Collectors.toSet()));
        }

    }

    private class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public String getHeader(String name) {
            return StringEscapeUtils.escapeHtml4(super.getHeader(name));
        }

        public String getQueryString() {
            return StringEscapeUtils.escapeHtml4(super.getQueryString());
        }

        public String getParameter(String name) {
            return StringEscapeUtils.escapeHtml4(super.getParameter(name));
        }

        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            return null != values && values.length > 0 ? (String[]) Arrays.stream(values).map(StringEscapeUtils::escapeHtml4).toArray((x$0) -> {
                return new String[x$0];
            }) : values;
        }
    }
}
