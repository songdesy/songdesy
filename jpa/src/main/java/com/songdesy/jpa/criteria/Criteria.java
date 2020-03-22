package com.songdesy.jpa.criteria;

import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import java.io.Serializable;
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
 * Date: 18-4-30 下午9:27
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class Criteria implements Serializable {
    private static final long serialVersionUID = -4326730485797653932L;
    private List<Criterion> criterions = new ArrayList();
    private List<Order> orders = new ArrayList();
    private Map<String, Object> paramMap = new HashMap();
    private StringBuilder criterionSb = new StringBuilder();
    private StringBuilder orderSb = new StringBuilder();

    public Criteria() {
    }

    public static Criteria instance() {
        return new Criteria();
    }

    public Criteria eq(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, "=");
        return this.add(criterion);
    }

    public Criteria ne(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, "<>");
        return this.add(criterion);
    }

    public Criteria gt(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, ">");
        return this.add(criterion);
    }

    public Criteria ge(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, ">=");
        return this.add(criterion);
    }

    public Criteria lt(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, "<");
        return this.add(criterion);
    }

    public Criteria le(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, "<=");
        return this.add(criterion);
    }

    public Criteria like(String propertyName, Object value) {
        Criterion criterion = new SimpleExpression(propertyName, value, "LIKE");
        return this.add(criterion);
    }

    public Criteria like(String propertyName, String value, MatchMode matchMode) {
        Criterion criterion = new SimpleExpression(propertyName, matchMode.toMatchString(value), "LIKE");
        return this.add(criterion);
    }

    public Criteria between(String propertyName, Object lo, Object hi) {
        Criterion criterion = new BetweenExpression(propertyName, lo, hi);
        return this.add(criterion);
    }

    public Criteria in(String propertyName, Object values) {
        Criterion criterion = new InExpression(propertyName, values);
        return this.add(criterion);
    }

    public Criteria isNull(String propertyName) {
        Criterion criterion = new NullExpression(propertyName);
        return this.add(criterion);
    }

    public Criteria isNotNull(String propertyName) {
        Criterion criterion = new NotNullExpression(propertyName);
        return this.add(criterion);
    }

    public Criteria eqProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, "=");
        return this.add(criterion);
    }

    public Criteria neProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, "<>");
        return this.add(criterion);
    }

    public Criteria ltProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, "<");
        return this.add(criterion);
    }

    public Criteria leProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, "<=");
        return this.add(criterion);
    }

    public Criteria gtProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, ">");
        return this.add(criterion);
    }

    public Criteria geProperty(String propertyName, String otherPropertyName) {
        Criterion criterion = new PropertyExpression(propertyName, otherPropertyName, ">=");
        return this.add(criterion);
    }

    public Criteria add(Criterion criterion) {
        this.criterions.add(criterion);
        this.paramMap.putAll(criterion.getParamMap());
        this.criterionSb.append(criterion.key());
        return this;
    }

    public Criteria asc(String propertyName) {
        return this.addOrder(new Order(propertyName, "ASC"));
    }

    public Criteria desc(String propertyName) {
        return this.addOrder(new Order(propertyName, "DESC"));
    }

    public Criteria addOrder(Order order) {
        this.orders.add(order);
        this.orderSb.append(order.key());
        return this;
    }

    public String toSql(Map<String, Column> fieldColMap) {
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(this.criterions)) {
            sb.append(" WHERE ").append(((Criterion)this.criterions.get(0)).toSqlString(fieldColMap));
            int size = this.criterions.size();

            for(int i = 1; i < size; ++i) {
                sb.append(" AND ").append(((Criterion)this.criterions.get(i)).toSqlString(fieldColMap));
            }
        }

        return sb.toString();
    }

    public String toSelectSql(Map<String, Column> fieldColMap) {
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(this.criterions)) {
            sb.append(" WHERE ").append(((Criterion)this.criterions.get(0)).toSqlString(fieldColMap));
            int size = this.criterions.size();

            for(int i = 1; i < size; ++i) {
                sb.append(" AND ").append(((Criterion)this.criterions.get(i)).toSqlString(fieldColMap));
            }
        }

        if (!CollectionUtils.isEmpty(this.orders)) {
            sb.append(" ORDER BY ");
            Iterator var5 = this.orders.iterator();

            while(var5.hasNext()) {
                Order order = (Order)var5.next();
                sb.append(order.toSqlString(fieldColMap)).append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public String getKey() {
        return this.criterionSb.toString();
    }

    public String getSelectKey() {
        return this.criterionSb.append(this.orderSb).toString();
    }

    public Map<String, Object> getParamMap() {
        return this.paramMap;
    }

    public boolean isEmpty() {
        return 0 == this.criterions.size() + this.orders.size();
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public boolean hasOrders() {
        return this.orders.size() > 0;
    }
}
