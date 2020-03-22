package com.songdesy.jpa.exception;

import org.springframework.core.NestedRuntimeException;

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
 * Date: 18-4-30 下午9:30
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class JpaException extends NestedRuntimeException {
    private static final long serialVersionUID = 1L;

    public JpaException() {
        super("");
    }

    public JpaException(String msg) {
        super(msg);
    }

    public JpaException(Throwable cause) {
        super("", cause);
    }

    public JpaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
