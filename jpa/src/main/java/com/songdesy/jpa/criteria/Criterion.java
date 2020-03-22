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
 * Date: 18-4-30 下午9:27
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public interface Criterion extends Serializable {
    Map<String, Object> getParamMap();

    String toSqlString(Map<String, Column> var1);

    String key();
}
