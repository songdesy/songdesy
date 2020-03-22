package com.songdesy.jpa.criteria;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Map;

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
 * Date: 18-4-30 下午9:28
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class Order implements Serializable {
    private static final long serialVersionUID = -6076805873357499099L;
    private String propertyName;
    private String sortType;

    public String toString() {
        return this.propertyName + ' ' + this.sortType;
    }

    public String key() {
        return this.propertyName + this.sortType;
    }

    protected Order(String propertyName, String sortType) {
        this.propertyName = propertyName;
        this.sortType = sortType;
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        return ((Column)fieldColMap.get(this.propertyName)).name() + ' ' + this.sortType;
    }

    public static Order asc(String propertyName) {
        return new Order(propertyName, "ASC");
    }

    public static Order desc(String propertyName) {
        return new Order(propertyName, "DESC");
    }
}
