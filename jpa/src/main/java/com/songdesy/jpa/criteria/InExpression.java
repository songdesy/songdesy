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
public class InExpression implements Criterion {
    private static final long serialVersionUID = -305689868449140051L;
    private final String propertyName;
    private final Object value;

    public InExpression(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public Map<String, Object> getParamMap() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put(this.propertyName, this.value);
        return paramMap;
    }

    public String toSqlString(Map<String, Column> fieldColMap) {
        String colName = ((Column)fieldColMap.get(this.propertyName)).name();
        return colName + " IN(:" + this.propertyName + ")";
    }

    public String key() {
        return '-' + this.propertyName;
    }
}
