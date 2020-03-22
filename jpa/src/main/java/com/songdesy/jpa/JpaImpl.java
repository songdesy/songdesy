package com.songdesy.jpa;

import com.songdesy.jpa.criteria.Criteria;
import com.songdesy.jpa.page.PageResult;
import com.songdesy.jpa.parser.EntitySqlParser;
import com.songdesy.jpa.parser.SqlParser;
import com.songdesy.jpa.parser.SqlParserFactory;
import com.songdesy.jpa.util.Dialect;
import com.songdesy.jpa.util.SnowflakeIdWorker;
import com.songdesy.untils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.*;
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
 * Date: 18-4-30 下午9:26
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class JpaImpl implements Jpa {
    private static final Logger logger = LoggerFactory.getLogger(JpaImpl.class);
    private static final String PAGE_SQL = "Offset :startRow Row Fetch Next :pageSize Rows Only";
    protected Dialect dialect;
    protected NamedParameterJdbcTemplate jdbc;
    protected SnowflakeIdWorker idWorker;

    public JpaImpl(DataSource dataSource) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    public JpaImpl(DataSource dataSource, Dialect dialect) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
        this.dialect = dialect;
    }

    public JpaImpl(JdbcOperations jdbcOperations) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = new NamedParameterJdbcTemplate(jdbcOperations);
    }

    public JpaImpl(JdbcOperations jdbcOperations, Dialect dialect) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = new NamedParameterJdbcTemplate(jdbcOperations);
        this.dialect = dialect;
    }

    public JpaImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = namedParameterJdbcTemplate;
    }

    public JpaImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Dialect dialect) {
        this.dialect = Dialect.MySQL;
        this.idWorker = new SnowflakeIdWorker();
        this.jdbc = namedParameterJdbcTemplate;
        this.dialect = dialect;
    }

    public void setCacheLimit(int cacheLimit) {
        Assert.isTrue(cacheLimit > 0, "SQL cache must greater 0.");
        this.jdbc.setCacheLimit(cacheLimit);
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setWorkerId(long workerId) {
        this.idWorker.setWorkerId(workerId);
    }

    public void setDataCenterId(long dataCenterId) {
        this.idWorker.setDataCenterId(dataCenterId);
    }

    public int persist(Object bean) {
        Assert.notNull(bean, "Parameter 'bean' must not be null.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(bean.getClass());
        String sql = sqlParser.getPersistSql();
        this.log(sql, bean);
        return this.jdbc.update(sql, new BeanPropertySqlParameterSource(bean));
    }

    public int dynamicPersist(Object bean) {
        Assert.notNull(bean, "Parameter 'bean' must not be null.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(bean.getClass());
        Map<String, Object> paramMap = MapUtils.obj2Map(bean);
        String sql = sqlParser.getPersistSql(paramMap);
        this.log(sql, paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public Serializable save(Object entity) {
        Assert.notNull(entity, "Parameter 'entity' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entity);
        Object idVal = paramMap.get(fid);
        if (Objects.isNull(idVal)) {
            return this.doSave(sqlParser, paramMap);
        } else {
            String sql = sqlParser.getPersistSql();
            this.log(sql, paramMap);
            this.jdbc.update(sql, paramMap);
            return (Serializable) idVal;
        }
    }

    public <T extends Serializable> T save(Object entity, Class<T> returnType) {
        Assert.notNull(returnType, "Parameter 'clazz' must not be null.");
        Serializable id = this.save(entity);
        return findOne(returnType, id);
    }

    public Serializable dynamicSave(Object entity) {
        Assert.notNull(entity, "Parameter 'entity' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entity);
        Object idVal = paramMap.get(fid);
        if (Objects.isNull(idVal)) {
            GeneratedValue gv = sqlParser.getGeneratedValue();
            GenerationType gt = gv.strategy();
            if (GenerationType.AUTO.equals(gt)) {
                Number id = this.createId(gv);
                String sql = sqlParser.getPersistSql(paramMap);
                paramMap.put(sqlParser.getFid(), id);
                this.log(sql, paramMap);
                this.jdbc.update(sql, paramMap);
                return id;
            } else {
                String sql = sqlParser.getInsertSql(paramMap);
                this.log(sql, paramMap);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                this.jdbc.update(sql, new MapSqlParameterSource(paramMap), keyHolder, new String[]{sqlParser.getCid()});
                return keyHolder.getKey();
            }
        } else {
            String sql = sqlParser.getPersistSql(paramMap);
            this.log(sql, paramMap);
            this.jdbc.update(sql, paramMap);
            return (Number) idVal;
        }
    }

    public <T extends Serializable> T dynamicSave(Object entity, Class<T> returnType) {
        Assert.notNull(returnType, "Parameter 'clazz' must not be null.");
        Serializable id = this.dynamicSave(entity);
        return findOne(returnType, id);
    }

    public int update(Object entity, String... fields) {
        Assert.notNull(entity, "Parameter 'entity' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String sql = sqlParser.getUpdateSql(fields);
        Map<String, Object> paramMap = this.toMap(entity, sqlParser.getFid());
        this.log(sql, paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public int dynamicUpdate(Object entity) {
        Assert.notNull(entity, "Parameter 'entity' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        Map<String, Object> paramMap = this.toMap(entity, sqlParser.getFid());
        String sql = sqlParser.getUpdateSql(paramMap);
        this.log(sql, paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public int remove(Object entity) {
        Assert.notNull(entity, "Parameter 'entity' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String sql = sqlParser.getDeleteSql();
        Map<String, Object> paramMap = this.toMap(entity, sqlParser.getFid());
        this.log(sql, paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public int remove(Class<?> clazz, Serializable id) {
        Assert.notNull(id, "Parameter 'id' must not be null.");
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(clazz);
        String sql = sqlParser.getDeleteSql();
        Map<String, Object> paramMap = new HashMap();
        paramMap.put(sqlParser.getFid(), id);
        this.log(sql, (Map) paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public int remove(Class<?> clazz, Criteria criteria) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        Assert.notNull(criteria, "Parameter 'criteria' must not be null.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(clazz);
        String sql = sqlParser.getDeleteSql(criteria);
        Map<String, Object> paramMap = criteria.getParamMap();
        this.log(sql, paramMap);
        return this.jdbc.update(sql, paramMap);
    }

    public <T> T findOne(Class<T> clazz, Serializable id, String... fields) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        Assert.notNull(id, "Parameter 'id' must not be null.");
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(clazz);
        String sql = sqlParser.getSelectSql(fields);
        Map<String, Object> paramMap = new HashMap();
        paramMap.put(sqlParser.getFid(), id);
        this.log(sql, (Map) paramMap);
        List<T> list = this.jdbc.query(sql, paramMap, new BeanPropertyRowMapper(clazz));
        return this.singleResult(list);
    }

    public <T> T findOne(Class<T> clazz, Criteria criteria, String... fields) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        Assert.notNull(criteria, "Parameter 'criteria' must not be null.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(clazz);
        String sql = sqlParser.getSelectSql(criteria, fields);
        Map<String, Object> paramMap = criteria.getParamMap();
        this.log(sql, paramMap);
        List<T> list = this.jdbc.query(sql, paramMap, new BeanPropertyRowMapper(clazz));
        return this.singleResult(list);
    }

    public Long count(Class<?> clazz) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        String sql = SqlParserFactory.getSqlParser(clazz).getAllCountSql();
        logger.debug("SQL: {}", sql);
        return (Long) this.jdbc.queryForObject(sql, new HashMap(0), Long.class);
    }

    public Long count(Class<?> clazz, Criteria criteria) {
        if (Objects.isNull(criteria)) {
            return this.count(clazz);
        } else {
            String sql = SqlParserFactory.getSqlParser(clazz).getCountSql(criteria);
            Map<String, Object> paramMap = criteria.getParamMap();
            this.log(sql, paramMap);
            return (Long) this.jdbc.queryForObject(sql, paramMap, Long.class);
        }
    }

    public <T> List<T> findAll(Class<T> clazz, String... fields) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        String sql = SqlParserFactory.getSqlParser(clazz).getSelectAll(fields);
        logger.debug("SQL: {}", sql);
        return this.jdbc.query(sql, new HashMap(0), new BeanPropertyRowMapper(clazz));
    }

    public <T> List<T> findList(Class<T> clazz, Criteria criteria, String... fields) {
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        if (Objects.isNull(criteria)) {
            return this.findAll(clazz, fields);
        } else {
            SqlParser sqlParser = SqlParserFactory.getSqlParser(clazz);
            String sql = sqlParser.getSelectSql(criteria, fields);
            Map<String, Object> paramMap = criteria.getParamMap();
            this.log(sql, paramMap);
            return this.jdbc.query(sql, paramMap, new BeanPropertyRowMapper(clazz));
        }
    }

    public <T> List<T> findPageList(Class<T> clazz, Criteria criteria, long page, int pageSize, String... fields) {
        long start = (page - 1L) * (long) pageSize;
        return this.findPageList2(clazz, criteria, start, pageSize, fields);
    }

    public <T> List<T> findPageList2(Class<T> clazz, Criteria criteria, long startRow, int pageSize, String... fields) {
        String orianSql = null;
        Map<String, Object> paramMap = null;
        SqlParser sqlParser = SqlParserFactory.getSqlParser(clazz);
        if (!Objects.isNull(criteria) && !criteria.isEmpty()) {
            paramMap = criteria.getParamMap();
            orianSql = sqlParser.getSelectSql(criteria, fields);
        } else {
            paramMap = new HashMap();
            orianSql = sqlParser.getSelectAll(fields);
        }

        String sql = this.pageSql(orianSql, (Map) paramMap, startRow, pageSize);
        this.log(sql, (Map) paramMap);
        return this.jdbc.query(sql, (Map) paramMap, new BeanPropertyRowMapper(clazz));
    }

    private String pageSql(String orianSql, Map<String, Object> paramMap, long startRow, int pageSize) {
        if (this.dialect == Dialect.MySQL) {
            paramMap.put("startRow", startRow);
            paramMap.put("limit", pageSize);
            return orianSql + " limit :startRow,:limit";
        } else {
            StringBuilder sb = new StringBuilder(orianSql.length() + 120);
            paramMap.put("startRow", startRow);
            paramMap.put("endRow", startRow + (long) pageSize);
            sb.append("SELECT * FROM (SELECT tmp_page.*, rownum row_num FROM (").append(orianSql).append(") tmp_page WHERE rownum <= :endRow) WHERE row_num > :startRow ");
            return sb.toString();
        }
    }

    public <T> PageResult<T> findByPage(Class<T> clazz, Criteria criteria, long page, int pageSize, String... fields) {
        long totalCount = this.count(clazz, criteria);
        List<T> list = this.findPageList(clazz, criteria, page, pageSize, fields);
        return new PageResult(totalCount, list);
    }

    public <T> PageResult<T> findByPage2(Class<T> clazz, Criteria criteria, long startRow, int pageSize, String... fields) {
        long totalCount = this.count(clazz, criteria);
        List<T> list = this.findPageList2(clazz, criteria, startRow, pageSize, fields);
        return new PageResult(totalCount, list);
    }

    public int[] batchPersist(List<? extends Object> beans) {
        Assert.notEmpty(beans, "Parameter 'beans' must not be empty.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(beans.get(0).getClass());
        return this.doBatch(sqlParser.getPersistSql(), beans);
    }

    public int[] batchPersist(Object[] beans) {
        Assert.noNullElements(beans, "Parameter 'entities' must not be empty.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(beans[0].getClass());
        return this.doBatch(sqlParser.getPersistSql(), beans);
    }

    public int[] batchDynamicPersist(List<? extends Object> beans) {
        Assert.notEmpty(beans, "Parameter 'beans' must not be empty.");
        Object bean = beans.get(0);
        SqlParser sqlParser = SqlParserFactory.getSqlParser(bean.getClass());
        Map<String, Object> paramMap = MapUtils.obj2Map(bean);
        return this.doBatch(sqlParser.getPersistSql(paramMap), beans);
    }

    public int[] batchDynamicPersist(Object[] beans) {
        Assert.noNullElements(beans, "Parameter 'entities' must not be empty.");
        SqlParser sqlParser = SqlParserFactory.getSqlParser(beans[0].getClass());
        Map<String, Object> paramMap = MapUtils.obj2Map(beans[0]);
        return this.doBatch(sqlParser.getPersistSql(paramMap), beans);
    }

    public int[] batchSave(List<? extends Object> entities) {
        Assert.notEmpty(entities, "Parameter 'entities' must not be empty.");
        Object entity = entities.get(0);
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entity);
        Object idVal = paramMap.get(fid);
        return Objects.isNull(idVal) ? this.doBatchSave(fid, entities, sqlParser) : this.doBatch(sqlParser.getPersistSql(), sqlParser.getFid(), entities);
    }

    public int[] batchSave(Object[] entities) {
        Assert.noNullElements(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities[0].getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entities[0]);
        Object idVal = paramMap.get(fid);
        return Objects.isNull(idVal) ? this.doBatchSave(fid, entities, sqlParser) : this.doBatch(sqlParser.getPersistSql(), sqlParser.getFid(), entities);
    }

    public int[] batchDynamicSave(List<? extends Object> entities) {
        Assert.notEmpty(entities, "Parameter 'entities' must not be empty.");
        Object entity = entities.get(0);
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entity);
        Object idVal = paramMap.get(fid);
        if (Objects.isNull(idVal)) {
            String sql = sqlParser.getInsertSql(paramMap);
            return this.doDynamicBatchSave(fid, sql, entities, sqlParser.getGeneratedValue());
        } else {
            return this.doBatch(sqlParser.getPersistSql(paramMap), sqlParser.getFid(), entities);
        }
    }

    public int[] batchDynamicSave(Object[] entities) {
        Assert.noNullElements(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities[0].getClass());
        String fid = sqlParser.getFid();
        Map<String, Object> paramMap = MapUtils.obj2Map(entities[0]);
        Object idVal = paramMap.get(fid);
        if (Objects.isNull(idVal)) {
            String sql = sqlParser.getInsertSql(paramMap);
            return this.doDynamicBatchSave(fid, sql, entities, sqlParser.getGeneratedValue());
        } else {
            return this.doBatch(sqlParser.getPersistSql(paramMap), sqlParser.getFid(), entities);
        }
    }

    private int[] doBatchSave(String fid, List<? extends Object> entities, EntitySqlParser sqlParser) {
        GeneratedValue gv = sqlParser.getGeneratedValue();
        GenerationType gt = gv.strategy();
        if (!GenerationType.AUTO.equals(gt) && !GenerationType.TABLE.equals(gt)) {
            return this.doBatch(sqlParser.getInsertSql(), entities);
        } else {
            int size = entities.size();
            Map<String, Object>[] batchParams = new Map[size];

            for (int i = 0; i < size; ++i) {
                batchParams[i] = MapUtils.obj2Map(entities.get(i));
                batchParams[i].put(fid, this.createId(gv));
            }

            String sql = sqlParser.getPersistSql();
            this.log(sql, (Object[]) batchParams);
            return this.jdbc.batchUpdate(sql, batchParams);
        }
    }

    private int[] doBatchSave(String fid, Object[] entities, EntitySqlParser sqlParser) {
        GeneratedValue gv = sqlParser.getGeneratedValue();
        GenerationType gt = gv.strategy();
        if (!GenerationType.AUTO.equals(gt) && !GenerationType.TABLE.equals(gt)) {
            return this.doBatch(sqlParser.getInsertSql(), entities);
        } else {
            Map<String, Object>[] batchParams = new Map[entities.length];

            for (int i = 0; i < entities.length; ++i) {
                batchParams[i] = MapUtils.obj2Map(entities[i]);
                batchParams[i].put(fid, this.createId(gv));
            }

            String sql = sqlParser.getPersistSql();
            this.log(sql, (Object[]) batchParams);
            return this.jdbc.batchUpdate(sql, batchParams);
        }
    }

    private int[] doDynamicBatchSave(String fid, String sql, List<? extends Object> entities, GeneratedValue gv) {
        GenerationType gt = gv.strategy();
        if (!GenerationType.AUTO.equals(gt) && !GenerationType.TABLE.equals(gt)) {
            return this.doBatch(sql, entities);
        } else {
            int size = entities.size();
            Map<String, Object>[] batchParams = new Map[size];

            for (int i = 0; i < size; ++i) {
                batchParams[i] = MapUtils.obj2Map(entities.get(i));
                batchParams[i].put(fid, this.createId(gv));
            }

            this.log(sql, (Object[]) batchParams);
            return this.jdbc.batchUpdate(sql, batchParams);
        }
    }

    private int[] doDynamicBatchSave(String fid, String sql, Object[] entities, GeneratedValue gv) {
        GenerationType gt = gv.strategy();
        if (!GenerationType.AUTO.equals(gt) && !GenerationType.TABLE.equals(gt)) {
            return this.doBatch(sql, entities);
        } else {
            Map<String, Object>[] batchParams = new Map[entities.length];

            for (int i = 0; i < entities.length; ++i) {
                batchParams[i] = MapUtils.obj2Map(entities[i]);
                batchParams[i].put(fid, this.createId(gv));
            }

            this.log(sql, (Object[]) batchParams);
            return this.jdbc.batchUpdate(sql, batchParams);
        }
    }

    public int[] batchUpdate(List<? extends Object> entities) {
        Assert.notEmpty(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities.get(0).getClass());
        return this.doBatch(sqlParser.getUpdateSql(new String[0]), sqlParser.getFid(), entities);
    }

    public int[] batchUpdate(Object[] entities) {
        Assert.noNullElements(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities[0].getClass());
        return this.doBatch(sqlParser.getUpdateSql(new String[0]), sqlParser.getFid(), entities);
    }

    public int[] batchDynamicUpdate(List<? extends Object> entities) {
        Assert.notEmpty(entities, "Parameter 'entities' must not be empty.");
        Object entity = entities.get(0);
        Map<String, Object> paramMap = MapUtils.obj2Map(entity);
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entity.getClass());
        return this.doBatch(sqlParser.getUpdateSql(paramMap), sqlParser.getFid(), entities);
    }

    public int[] batchDynamicUpdate(Object[] entities) {
        Assert.noNullElements(entities, "Parameter 'entities' must not be empty.");
        Map<String, Object> paramMap = MapUtils.obj2Map(entities[0]);
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities[0].getClass());
        return this.doBatch(sqlParser.getUpdateSql(paramMap), sqlParser.getFid(), entities);
    }

    public int[] batchRemove(List<? extends Object> entities) {
        Assert.notEmpty(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities.get(0).getClass());
        return this.doBatch(sqlParser.getDeleteSql(), sqlParser.getFid(), entities);
    }

    public int[] batchRemove(Object[] entities) {
        Assert.noNullElements(entities, "Parameter 'entities' must not be empty.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(entities[0].getClass());
        return this.doBatch(sqlParser.getDeleteSql(), sqlParser.getFid(), entities);
    }

    public int batchRemove(Class<?> clazz, List<? extends Serializable> ids) {
        Assert.notEmpty(ids, "Parameter 'ids' must not be empty.");
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(clazz);
        String sql = sqlParser.getDeleteInSql();
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        this.log(sql, params);
        return this.jdbc.update(sql, params);
    }

    public int batchRemove(Class<?> clazz, Serializable[] ids) {
        Assert.noNullElements(ids, "Parameter 'ids' must not be empty.");
        Assert.notNull(clazz, "Parameter 'clazz' must not be null.");
        EntitySqlParser sqlParser = SqlParserFactory.getEntitySqlParser(clazz);
        String sql = sqlParser.getDeleteInSql();
        Map<String, Object> params = Collections.singletonMap("ids", Arrays.asList(ids));
        this.log(sql, params);
        return this.jdbc.update(sql, params);
    }

    public Long createId() {
        return this.idWorker.nextId();
    }

    protected <T> T singleResult(Collection<T> results) {
        int size = results != null ? results.size() : 0;
        if (size == 1) {
            return results.iterator().next();
        } else if (size == 0) {
            return null;
        } else {
            throw new IncorrectResultSizeDataAccessException(1, size);
        }
    }

    private int[] doBatch(String sql, List<? extends Object> beans) {
        Map<String, Object>[] batchParams = (Map[]) beans.stream().map(MapUtils::obj2Map).toArray((x$0) -> {
            return new Map[x$0];
        });
        this.log(sql, (Object[]) batchParams);
        return this.jdbc.batchUpdate(sql, batchParams);
    }

    private int[] doBatch(String sql, Object[] beans) {
        Map<String, Object>[] batchParams = (Map[]) Arrays.stream(beans).map(MapUtils::obj2Map).toArray((x$0) -> {
            return new Map[x$0];
        });
        this.log(sql, (Object[]) batchParams);
        return this.jdbc.batchUpdate(sql, batchParams);
    }

    private int[] doBatch(String sql, String fid, List<? extends Object> entities) {
        Map<String, Object>[] batchParams = (Map[]) entities.stream().map((s) -> {
            return this.toMap(s, fid);
        }).toArray((x$0) -> {
            return new Map[x$0];
        });
        this.log(sql, (Object[]) batchParams);
        return this.jdbc.batchUpdate(sql, batchParams);
    }

    private int[] doBatch(String sql, String fid, Object[] entities) {
        Map<String, Object>[] batchParams = (Map[]) Arrays.stream(entities).map((s) -> {
            return this.toMap(s, fid);
        }).toArray((x$0) -> {
            return new Map[x$0];
        });
        this.log(sql, (Object[]) batchParams);
        return this.jdbc.batchUpdate(sql, batchParams);
    }

    private void log(String sql, Map<String, ?> paramMap) {
        logger.debug("SQL: {}", sql);
        logger.debug("Parameters: {}", paramMap);
    }

    private void log(String sql, Object obj) {
        logger.debug("SQL: {}", sql);
        logger.debug("Parameters: {}", MapUtils.obj2Map(obj));
    }

    private void log(String sql, Object[] array) {
        logger.debug("SQL: {}", sql);
        String str = (String) Arrays.stream(array).map(Object::toString).collect(Collectors.joining("\t"));
        logger.debug("Parameters: {}", str);
    }

    private Serializable doSave(EntitySqlParser sqlParser, Map<String, Object> paramMap) {
        GeneratedValue gv = sqlParser.getGeneratedValue();
        GenerationType gt = gv.strategy();
        if (GenerationType.AUTO.equals(gt)) {
            Number id = this.createId(gv);
            String sql = sqlParser.getPersistSql();
            paramMap.put(sqlParser.getFid(), id);
            this.log(sql, paramMap);
            this.jdbc.update(sql, paramMap);
            return id;
        } else {
            String sql = sqlParser.getInsertSql();
            this.log(sql, paramMap);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbc.update(sql, new MapSqlParameterSource(paramMap), keyHolder, new String[]{sqlParser.getCid()});
            return keyHolder.getKey();
        }
    }

    private Number createId(GeneratedValue gv) {
        return this.idWorker.nextId();
    }

    private Map<String, Object> toMap(Object entity, String fid) {
        Map<String, Object> map = MapUtils.obj2Map(entity);
        Assert.notNull(map.get(fid), "Primary key field must not be null.");
        return map;
    }
}

