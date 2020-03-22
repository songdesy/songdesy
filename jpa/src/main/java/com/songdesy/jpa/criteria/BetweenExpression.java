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
 * Date: 18-4-30 下午9:26
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class BetweenExpression implements Criterion {
    private static final long serialVersionUID = -8599662726301768566L;
    private final String propertyName;
    private final Object lo;
    private final Object hi;

    protected BetweenExpression(String propertyName, Object lo, Object hi) {
        this.propertyName = propertyName;
        this.lo = lo;
        this.hi = hi;
    }

    public Map<String, Object> getParamMap() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("lo" + this.propertyName, this.lo);
        paramMap.put("hi" + this.propertyName, this.hi);
        return paramMap;
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        String colName = ((Column)fieldColMap.get(this.propertyName)).name();
        return colName + " BETWEEN :lo" + this.propertyName + " AND :hi" + this.propertyName;
    }

    public String key() {
        return '#' + this.propertyName;
    }

    public String toString() {
        return this.propertyName + " between " + this.lo + " and " + this.hi;
    }
}
