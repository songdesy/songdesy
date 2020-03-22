package com.songdesy.jpa.page;

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
public class PageResult<T> {
    private long totalCount;
    private T data;

    public PageResult() {
    }

    public PageResult(long totalCount, T data) {
        this.totalCount = totalCount;
        this.data = data;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
