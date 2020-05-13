package com.songdesy.common.exception;

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
 * Date: 18-4-27 下午1:29
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class AuthException
        extends RuntimeException
{
    private static final long serialVersionUID = 4921034957417813299L;

    public AuthException() {}

    public AuthException(String message)
    {
        super(message);
    }

    public AuthException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AuthException(Throwable cause)
    {
        super(cause);
    }
}
