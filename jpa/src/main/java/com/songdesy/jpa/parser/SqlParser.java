package com.songdesy.jpa.parser;

import com.songdesy.jpa.criteria.Criteria;
import com.songdesy.jpa.exception.JpaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
 * Date: 18-4-30 下午9:30
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/

public class SqlParser {
    private static final Logger logger = LoggerFactory.getLogger(SqlParser.class);
    private String fid;
    private String colId;
    private String table;
    private String className;
    private GeneratedValue generatedValue;
    private final Map<String, Column> fieldColMap = new HashMap();
    private String insert;
    private String countSql;
    private String selectAll;
    private final ConcurrentMap<String, Future<String>> cache = new ConcurrentHashMap();

    public SqlParser(Class<?> clazz) {
        this.className = clazz.getName();
        this.setTable(clazz);
        this.dealFields(clazz);
        this.setAllCountSql();
        this.setInsertSql();
        this.setSelectAllSql();
    }

    public String getPersistSql() {
        return this.insert;
    }

    public String getPersistSql(Map<String, Object> paramMap) {
        String keys = (String)this.fieldColMap.entrySet().stream().filter((s) -> {
            return paramMap.containsKey(s.getKey()) && ((Column)s.getValue()).insertable();
        }).map((s) -> {
            return ":" + (String)s.getKey();
        }).collect(Collectors.joining(","));
        String colNames = (String)this.fieldColMap.entrySet().stream().filter((s) -> {
            return paramMap.containsKey(s.getKey()) && ((Column)s.getValue()).insertable();
        }).map((s) -> {
            return ((Column)s.getValue()).name();
        }).collect(Collectors.joining(","));
        return String.join(" ", "INSERT INTO", this.table, "(", colNames, ") VALUES (", keys, ")");
    }

    public String getAllCountSql() {
        return this.countSql;
    }

    public String getSelectAll(String... fields) {
        if (null != fields && 0 != fields.length) {
            Arrays.sort(fields);
            String colNames = (String)Arrays.stream(fields).map(this::getColName).collect(Collectors.joining(","));
            return String.join(" ", "SELECT", colNames, "FROM", this.table);
        } else {
            return this.selectAll;
        }
    }

    public String getColName(String field) {
        Column col = (Column)this.fieldColMap.get(field);
        Assert.notNull(col, "There is not field '" + field + "' in " + this.className);
        return col.name();
    }

    public String getCountSql(Criteria criteria) {
        String key = "C." + criteria.getKey();
        return this.getSql(key, () -> {
            return this.countSql + criteria.toSql(this.fieldColMap);
        });
    }

    public String getSelectSql(Criteria criteria, String... fields) {
        if (null == criteria) {
            return this.getSelectAll(fields);
        } else {
            String selectField = this.getSelectAll(fields);
            String key = "S." + criteria.getSelectKey()+selectField;
            return this.getSql(key, () -> {
                return selectField + criteria.toSelectSql(this.fieldColMap);
            });
        }
    }

    public String getDeleteSql(Criteria criteria) {
        String key = "D." + criteria.getKey();
        return this.getSql(key, () -> {
            return "DELETE FROM " + this.table + criteria.toSql(this.fieldColMap);
        });
    }

    private void setAllCountSql() {
        this.countSql = "SELECT count(1) FROM " + this.table;
    }

    private void setInsertSql() {
        String colNames = (String)this.fieldColMap.values().stream().filter(Column::insertable).map(Column::name).collect(Collectors.joining(","));
        String keys = (String)this.fieldColMap.entrySet().stream().filter((s) -> {
            return ((Column)s.getValue()).insertable();
        }).map((s) -> {
            return ":" + (String)s.getKey();
        }).collect(Collectors.joining(","));
        this.insert = String.join(" ", "INSERT INTO", this.table, "(", colNames, ") VALUES (", keys, ")");
    }

    private void setSelectAllSql() {
        String colNameStr = (String)this.fieldColMap.values().stream().map(Column::name).collect(Collectors.joining(","));
        this.selectAll = String.join(" ", "SELECT", colNameStr, "FROM", this.table);
    }

    private void setTable(Class<?> clazz) {
        Table tab = (Table)clazz.getAnnotation(Table.class);
        Assert.notNull(tab, "@Table annotation is required.");
        String tableName = tab.name();
        Assert.hasText(tableName, "Table name is required.");
        String schema = tab.schema();
        this.table = StringUtils.isEmpty(schema) ? tableName : String.join(".", schema, tableName);
    }

    private void dealFields(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        if (Object.class.equals(superclass)) {
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields).filter((f) -> {
                return Objects.nonNull(f.getAnnotation(Column.class)) && !Modifier.isTransient(f.getModifiers());
            }).forEach(this::dealField);
        } else {
            this.dealFields(superclass);
        }

    }

    private void dealField(Field field) {
        String fieldName = field.getName();
        Column col = (Column)field.getAnnotation(Column.class);
        String cloName = col.name();
        Assert.hasText(col.name(), "Must set " + fieldName + " Column name in " + this.className);
        this.fieldColMap.put(fieldName, col);
        if (field.isAnnotationPresent(Id.class)) {
            this.fid = fieldName;
            this.colId = cloName;
            this.generatedValue = (GeneratedValue)field.getAnnotation(GeneratedValue.class);
        }

    }

    private String getSql(String key, Callable<String> callable) {
        while(true) {
            Future<String> f = (Future)this.cache.get(key);
            if (null == f) {
                FutureTask<String> ft = new FutureTask(callable);
                f = (Future)this.cache.putIfAbsent(key, ft);
                if (null == f) {
                    f = ft;
                    ft.run();
                }
            }

            try {
                return (String)((Future)f).get();
            } catch (InterruptedException var5) {
                this.cache.remove(key, f);
                logger.warn("Get Sql " + key + "  error", var5);
            } catch (ExecutionException var6) {
                logger.error("Get Sql " + key + "  error", var6);
                throw new JpaException(var6.getCause());
            }
        }
    }

    public String getFid() {
        return this.fid;
    }

    public String getColId() {
        return this.colId;
    }

    public String getTable() {
        return this.table;
    }

    public String getClassName() {
        return this.className;
    }

    public GeneratedValue getGeneratedValue() {
        return this.generatedValue;
    }

    public Map<String, Column> getFieldColMap() {
        return this.fieldColMap;
    }
}
