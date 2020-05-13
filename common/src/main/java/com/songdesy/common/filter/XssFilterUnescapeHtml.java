package com.songdesy.common.filter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

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

public class XssFilterUnescapeHtml implements Filter {
    private Set<String> excludeUrls = new HashSet();

    public XssFilterUnescapeHtml() {
    }

    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String servletPath = ((HttpServletRequest) httpServletRequest).getServletPath();
        Stream var10000 = this.excludeUrls.stream();
        servletPath.getClass();
        boolean flag = var10000.noneMatch(s -> servletPath.matches(s.toString()));
        if (flag) {
            httpServletRequest = new XssFilterUnescapeHtml.XssHttpServletRequestWrapper((HttpServletRequest) request);
        }

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        BufferResponse myResponse = new BufferResponse(response); // 包装响应对象 resp 并缓存响应数据

        chain.doFilter(httpServletRequest, myResponse);


        byte[] content = myResponse.getBuffer();//获取返回值
        //判断是否有值
        if (content.length > 0) {
            String str = new String(content, "UTF-8");
            String ciphertext = null;
            try {
                //......根据需要处理返回值
                ciphertext = StringEscapeUtils.unescapeHtml4(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            GZIPOutputStream gout = new GZIPOutputStream(bout);//进行压缩
            gout.write(ciphertext.getBytes());
            gout.close();//一定要关闭，这样数据才会从缓存中写入到底层流中去
            byte gzip[] = bout.toByteArray();//从底层流中取得数据
            response.setHeader("content-encoding", "gzip");//这里需要告诉浏览器这是一个压缩数据
            response.setContentLength(gzip.length);
            ServletOutputStream os = response.getOutputStream();//写出到浏览器
            os.write(gzip);
            os.flush();
            os.close();
        }


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

class BufferResponse extends HttpServletResponseWrapper {

    private HttpServletResponse response;
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();//字节流
    private PrintWriter pw;

    public BufferResponse(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        //这里我们对此方法进行了增强，不管是图片还是文本等数据都会进行压缩，但是如果直接访问jsp却不会，因为jsp一般是调用getWriter
        //方法，所以这里我们需要对getWriter方法进行增强
        return new MyServletOutputStream(bout);
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        /*return new PrintWriter(bout);//因为PrintWriter有接受一个底层流的构造函数，所以这里我们不需要重写一个,但是这个方法也是一个包装类
        //这个类当缓存没有写满的时候是不会讲数据写到底层流中去,所以这里我们需要强制关闭此类*/
        //pw = new PrintWriter(bout);//jsp中的汉字是一个字符流，这里会将其先转换为字节流，查的码表是gb2312的码表,但是我们设置的码表是UTF-8
        //而PrintWriter有一个接受一个字符流的方法，而字符流就会有设置码表的方法，而OutputStreamWriter是字符流到字节流的一个转换流，里面就可以指定码表
        pw = new PrintWriter(new OutputStreamWriter(bout, this.response.getCharacterEncoding()));
        return pw;
    }


    public byte[] getBuffer() {
        try {
            if (pw != null) {
                pw.close();
            }
            if (bout != null) {
                bout.flush();
                return bout.toByteArray();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class MyServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream bout;

    public MyServletOutputStream(ByteArrayOutputStream bout) {
        this.bout = bout;
    }

    @Override
    public void write(int arg0) throws IOException {
        this.bout.write(arg0);

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
    }
}