package com.songdesy.jpa.criteria;

import javax.persistence.Column;
import java.util.HashMap;
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
 * Date: 18-4-30 下午9:29
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class SimpleExpression implements Criterion {
    private static final long serialVersionUID = -5607108383127996360L;
    private final String propertyName;
    private final Object value;
    private final String op;

    protected SimpleExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    public Map<String, Object> getParamMap() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put(this.propertyName, this.value);
        return paramMap;
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        String colName = ((Column)fieldColMap.get(this.propertyName)).name();
        return colName + ' ' + this.op + " :" + this.propertyName;
    }

    public String key() {
        return '$' + this.propertyName + this.op;
    }

    public String toString() {
        return this.propertyName + this.op + this.value;
    }

    protected final String getOp() {
        return this.op;
    }
}
