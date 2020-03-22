package com.songdesy.untils;

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
 * Date: 18-5-5 下午6:59
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class IpUtils {
    private static final ThreadLocal<String> ip = new ThreadLocal();

    public static String getClientIp() {
        String cip = (String)ip.get();
        return null != cip ? cip : "";
    }

    public static void setClientIp(String clientIp) {
        ip.set(clientIp);
    }

    private IpUtils() {
    }
}
