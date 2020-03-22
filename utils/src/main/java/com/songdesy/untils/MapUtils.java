package com.songdesy.untils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

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
 * Date: 18-4-30 下午10:07
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public abstract class MapUtils {
    public MapUtils() {
    }

    public static Map<String, Object> obj2Map(Object bean) {
        if (bean == null) {
            return Collections.emptyMap();
        } else if (bean instanceof Map) {
            return (Map)bean;
        } else {
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
            Map<String, Object> map = new HashMap(pds.length);
            Arrays.stream(pds).filter(MapUtils::valid).forEach((s) -> {
                map.put(s.getName(), ReflectionUtils.invokeMethod(s.getReadMethod(), bean));
            });
            return map;
        }
    }

    private static boolean valid(PropertyDescriptor pd) {
        return Objects.nonNull(pd.getReadMethod()) && !Object.class.equals(pd.getReadMethod().getDeclaringClass());
    }
}
