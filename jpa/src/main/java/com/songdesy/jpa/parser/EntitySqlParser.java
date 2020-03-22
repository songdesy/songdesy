package com.songdesy.jpa.parser;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
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

public class EntitySqlParser {
    private String fid;
    private String cid;
    private String table;
    private String insert;
    private String update;
    private String delete;
    private String deleteIn;
    private final SqlParser sqlParser;
    private final GeneratedValue generatedValue;

    public EntitySqlParser(SqlParser sqlParser) {
        this.fid = sqlParser.getFid();
        this.cid = sqlParser.getColId();
        this.table = sqlParser.getTable();
        Assert.notNull(this.fid, "Must have a id field in " + sqlParser.getClassName());
        Assert.hasText(this.cid, "Must set " + this.fid + " Column name in " + sqlParser.getClassName());
        this.sqlParser = sqlParser;
        this.generatedValue = sqlParser.getGeneratedValue();
        Assert.notNull(this.generatedValue, "GeneratedValue is required in " + sqlParser.getClassName());
        this.setInsertSql();
        this.setUpdateSql();
        this.setDeleteSql();
        this.setDeleteInSql();
    }

    public String getSelectSql(String... fields) {
        return String.join(" ", this.getSelectAllSql(fields), "WHERE", this.cid, "=", ":" + this.fid);
    }

    public String getSelectAllSql(String... fields) {
        return this.sqlParser.getSelectAll(fields);
    }

    public String getUpdateSql(String... fields) {
        if (null != fields && fields.length > 0) {
            String colNames = (String)Arrays.stream(fields).map((f) -> {
                return this.sqlParser.getColName(f) + " = :" + f;
            }).collect(Collectors.joining(", "));
            return String.join(" ", "UPDATE", this.sqlParser.getTable(), "SET", colNames, "WHERE", this.sqlParser.getColId(), "= :" + this.sqlParser.getFid());
        } else {
            return this.update;
        }
    }

    public String getUpdateSql(Map<String, Object> paramMap) {
        StringBuilder sb = (new StringBuilder("UPDATE ")).append(this.sqlParser.getTable()).append(" SET ");
        Map<String, Column> map = this.sqlParser.getFieldColMap();
        Iterator var4 = map.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry)var4.next();
            String key = (String)entry.getKey();
            Column col = (Column)entry.getValue();
            if (null != paramMap.get(key) && !key.equals(this.fid) && col.updatable()) {
                sb.append(col.name()).append(" = :").append(key).append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(" WHERE ");
        sb.append(this.sqlParser.getColId()).append(" = :").append(this.sqlParser.getFid());
        return sb.toString();
    }

    public String getDeleteSql() {
        return this.delete;
    }

    public String getDeleteInSql() {
        return this.deleteIn;
    }

    public String getInsertSql() {
        return this.insert;
    }

    public String getInsertSql(Map<String, Object> paramMap) {
        StringBuilder sb = (new StringBuilder("INSERT INTO ")).append(this.sqlParser.getTable()).append("(");
        StringBuilder valSb = new StringBuilder(") VALUES (");
        GenerationType gt = this.generatedValue.strategy();
        Assert.notNull(gt, "strategy is required in " + this.sqlParser.getClassName());
        if (!GenerationType.AUTO.equals(gt) && !GenerationType.TABLE.equals(gt)) {
            if (GenerationType.SEQUENCE.equals(gt)) {
                sb.append(this.sqlParser.getColId()).append(",");
                valSb.append(this.generatedValue.generator() + ".nextval").append(",");
            }

            Map<String, Column> map = this.sqlParser.getFieldColMap();
            Iterator var6 = map.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, Column> entry = (Map.Entry)var6.next();
                String key = (String)entry.getKey();
                Column col = (Column)entry.getValue();
                if (null != paramMap.get(key) && !key.equals(this.fid) && col.insertable()) {
                    sb.append(col.name()).append(",");
                    valSb.append(":").append(key).append(",");
                }
            }

            sb.deleteCharAt(sb.length() - 1);
            valSb.deleteCharAt(valSb.length() - 1);
            sb.append(valSb).append(")");
            return sb.toString();
        } else {
            return this.sqlParser.getPersistSql(paramMap);
        }
    }

    public String getPersistSql() {
        return this.sqlParser.getPersistSql();
    }

    public String getPersistSql(Map<String, Object> paramMap) {
        return this.sqlParser.getPersistSql(paramMap);
    }

    public String getFid() {
        return this.sqlParser.getFid();
    }

    public String getCid() {
        return this.cid;
    }

    public GeneratedValue getGeneratedValue() {
        return this.generatedValue;
    }

    private void setUpdateSql(String... fields) {
        String cols = null;
        if (null != fields && 0 != fields.length) {
            cols = (String)Arrays.stream(fields).map((f) -> {
                return String.join(" ", this.sqlParser.getColName(f), "=", ":" + f);
            }).collect(Collectors.joining(","));
        } else {
            cols = (String)this.sqlParser.getFieldColMap().entrySet().stream().filter((s) -> {
                return !this.fid.equals(s.getKey()) && ((Column)s.getValue()).updatable();
            }).map((s) -> {
                return String.join(" ", ((Column)s.getValue()).name(), "=", ":" + (String)s.getKey());
            }).collect(Collectors.joining(", "));
        }

        this.update = String.join(" ", "UPDATE", this.table, "SET", cols, "WHERE", this.cid, "=", ":" + this.fid);
    }

    private void setDeleteSql() {
        this.delete = String.join(" ", "DELETE FROM", this.table, "WHERE", this.cid, "=", ":" + this.fid);
    }

    private void setDeleteInSql() {
        this.deleteIn = String.join(" ", "DELETE FROM", this.table, "WHERE", this.cid, "IN(:ids)");
    }

    private void setInsertSql() {
        switch(this.generatedValue.strategy()) {
            case IDENTITY:
                this.identitySql();
                break;
            case SEQUENCE:
                this.sequenceSql();
                break;
            default:
                this.insert = this.sqlParser.getPersistSql();
        }

    }

    private void identitySql() {
        StringBuilder sb = (new StringBuilder("INSERT INTO ")).append(this.sqlParser.getTable()).append("(");
        StringBuilder valSb = new StringBuilder(") VALUES (");
        GeneratedValue generatedValue = this.sqlParser.getGeneratedValue();
        Assert.notNull(generatedValue, "GeneratedValue is requeired in " + this.sqlParser.getClassName());
        Map<String, Column> map = this.sqlParser.getFieldColMap();
        Iterator var5 = map.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry)var5.next();
            String key = (String)entry.getKey();
            Column col = (Column)entry.getValue();
            if (!key.equals(this.fid) && col.insertable()) {
                sb.append(col.name()).append(",");
                valSb.append(":").append(key).append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        valSb.deleteCharAt(valSb.length() - 1);
        sb.append(valSb).append(")");
        this.insert = sb.toString();
    }

    private void sequenceSql() {
        StringBuilder sb = (new StringBuilder("INSERT INTO ")).append(this.sqlParser.getTable()).append("(");
        StringBuilder valSb = new StringBuilder(") VALUES (");
        String generator = this.generatedValue.generator();
        Assert.hasText(generator, "generator is reqired in " + this.sqlParser.getClassName());
        sb.append(this.sqlParser.getColId()).append(",");
        valSb.append(generator + ".nextval").append(",");
        Map<String, Column> map = this.sqlParser.getFieldColMap();
        Iterator var5 = map.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry)var5.next();
            String key = (String)entry.getKey();
            Column col = (Column)entry.getValue();
            if (!key.equals(this.fid) && col.insertable()) {
                sb.append(col.name()).append(",");
                valSb.append(":").append(key).append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        valSb.deleteCharAt(valSb.length() - 1);
        sb.append(valSb).append(")");
        this.insert = sb.toString();
    }
}

