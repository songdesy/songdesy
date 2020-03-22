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
 * Date: 2018/11/14 下午4:39
 * Copyright(©) 2018/11/14 by songsong.wu.
 **/
public class JsonResult {
    public static JsonResult instance() {
        return new JsonResult();
    }

    public static final String SUCCESS_CODE = "1000";
    public static final String ERROR_CODE = "1001";
    public static final String WARN_CODE = "1002";

    public JsonResult success() {
        this.code = SUCCESS_CODE;
        return this;
    }

    public JsonResult success(String msg) {
        this.code = SUCCESS_CODE;
        this.msg = msg;
        return this;
    }

    public JsonResult success(String msg, Object result) {
        this.code = SUCCESS_CODE;
        this.msg = msg;
        this.result = result;
        return this;
    }

    public JsonResult error(String msg) {
        this.code = ERROR_CODE;
        this.msg = msg;
        return this;
    }

    public JsonResult error(String msg, Object result) {
        this.code = ERROR_CODE;
        this.msg = msg;
        this.result = result;
        return this;
    }

    public JsonResult warn(String msg) {
        this.code = WARN_CODE;
        this.msg = msg;
        return this;
    }

    private String code;
    private String msg;
    private Object result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
