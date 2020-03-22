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
 * Date: 18-4-30 下午9:27
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class LogicalExpression implements Criterion {
    private static final long serialVersionUID = -7372299668367935094L;
    private final Criterion lhs;
    private final Criterion rhs;
    private final String op;

    protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    public Map<String, Object> getParamMap() {
        Map<String, Object> map = new HashMap();
        map.putAll(this.lhs.getParamMap());
        map.putAll(this.rhs.getParamMap());
        return map;
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        return '(' + this.lhs.toSqlString(fieldColMap) + ' ' + this.op + ' ' + this.rhs.toSqlString(fieldColMap) + ')';
    }

    public String key() {
        return '&' + this.lhs.key() + this.op + this.rhs.key();
    }

    public String getOp() {
        return this.op;
    }

    public String toString() {
        return this.lhs.toString() + ' ' + this.getOp() + ' ' + this.rhs.toString();
    }
}
