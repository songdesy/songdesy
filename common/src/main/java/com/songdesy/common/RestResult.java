package com.songdesy.common;

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
 * Date: 18-4-27 下午1:32
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

//@ApiModel
public final class RestResult<T>
        implements Serializable {
    private static final long serialVersionUID = -4642488445252988938L;
    public static final byte SUCCESS_CODE = 1;
    public static final byte NO_DATA_CODE = 0;
    public static final byte FAIL_CODE = -1;
    public static final byte UN_AUTH_CODE = -2;
    public static final byte NO_LOGIN_CODE = -3;
    public static final RestResult<Object> SUCCESS = new RestResult((byte) 1);
    public static final RestResult<Object> NO_DATA = new RestResult((byte) 0);
    public static final RestResult<Object> FAIL = new RestResult((byte) -1);
    public static final RestResult<Object> UN_AUTH = new RestResult((byte) -2);
    public static final RestResult<Object> NO_LOGIN = new RestResult((byte) -3);
//    @ApiModelProperty(value = "数据", example = "1")
    private T data;
//    @ApiModelProperty(value = "错误信息", example = "网络连接超时")
    private String msg;
//    @ApiModelProperty(value = "状态码, 1:正常, 0:没有数据, -1:异常, -2:认证未通过, -3:未登录", example = "0")
    private byte code = 1;

    public static <T> RestResult<T> of(T data) {
        return new RestResult((byte) 1, null, data);
    }

    public static <T> RestResult<T> of(byte code, String msg) {
        return new RestResult(code, msg);
    }

    public static <T> RestResult<T> of(byte code, String msg, T data) {
        return new RestResult(code, msg, data);
    }

    public static <T> RestResult<T> fail(String msg) {
        return new RestResult((byte) -1, msg);
    }

    public T getData() {
        return (T) this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    private RestResult(byte code) {
        this.code = code;
    }

    private RestResult(byte code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private RestResult(byte code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
}
