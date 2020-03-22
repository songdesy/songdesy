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
 * Date: 18-4-30 下午9:28
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class NullExpression implements Criterion {
    private static final long serialVersionUID = 3806236310502150788L;
    private final String propertyName;

    protected NullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    public Map<String, Object> getParamMap() {
        return new HashMap(0);
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        return this.propertyName + " IS NULL";
    }

    public String key() {
        return '!' + this.propertyName;
    }

    public String toString() {
        return this.propertyName + " is null";
    }
}
