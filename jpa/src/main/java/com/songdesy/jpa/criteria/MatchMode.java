package com.songdesy.jpa.criteria;

import java.io.Serializable;
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

public abstract class MatchMode implements Serializable {
    private static final long serialVersionUID = -519183655505270327L;
    private final String name;
    private static final Map<String, MatchMode> INSTANCES = new HashMap();
    public static final MatchMode EXACT = new MatchMode("EXACT") {
        public String toMatchString(String pattern) {
            return pattern;
        }
    };
    public static final MatchMode START = new MatchMode("START") {
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    };
    public static final MatchMode END = new MatchMode("END") {
        public String toMatchString(String pattern) {
            return '%' + pattern;
        }
    };
    public static final MatchMode ANYWHERE = new MatchMode("ANYWHERE") {
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    };

    protected MatchMode(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    private Object readResolve() {
        return INSTANCES.get(this.name);
    }

    public abstract String toMatchString(String var1);

    static {
        INSTANCES.put(EXACT.name, EXACT);
        INSTANCES.put(END.name, END);
        INSTANCES.put(START.name, START);
        INSTANCES.put(ANYWHERE.name, ANYWHERE);
    }
}
