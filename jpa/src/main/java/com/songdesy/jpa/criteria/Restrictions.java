package com.songdesy.jpa.criteria;

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
public abstract class Restrictions {
    public Restrictions() {
    }

    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "=");
    }

    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<>");
    }

    public static SimpleExpression like(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "LIKE");
    }

    public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
        return new SimpleExpression(propertyName, matchMode.toMatchString(value), "LIKE");
    }

    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">");
    }

    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<");
    }

    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<=");
    }

    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">=");
    }

    public static Criterion between(String propertyName, Object lo, Object hi) {
        return new BetweenExpression(propertyName, lo, hi);
    }

    public static Criterion in(String propertyName, Object values) {
        return new InExpression(propertyName, values);
    }

    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName);
    }

    public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "=");
    }

    public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<>");
    }

    public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<");
    }

    public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<=");
    }

    public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">");
    }

    public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">=");
    }

    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    public static LogicalExpression and(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "and");
    }

    public static LogicalExpression or(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "or");
    }
}
