package com.songdesy.jpa.parser;

import com.songdesy.jpa.exception.JpaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

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


public abstract class SqlParserFactory {
    private static final Logger logger = LoggerFactory.getLogger(SqlParserFactory.class);
    private static final ConcurrentMap<Class<?>, Future<SqlParser>> cache = new ConcurrentHashMap(256);
    private static final ConcurrentMap<Class<?>, Future<EntitySqlParser>> entityCache = new ConcurrentHashMap(256);

    public SqlParserFactory() {
    }

    public static SqlParser getSqlParser(Class<?> clazz) {
        while(true) {
            Future<SqlParser> f = (Future)cache.get(clazz);
            if (null == f) {
                FutureTask<SqlParser> ft = new FutureTask(() -> {
                    return new SqlParser(clazz);
                });
                f = (Future)cache.putIfAbsent(clazz, ft);
                if (null == f) {
                    f = ft;
                    ft.run();
                }
            }

            try {
                return (SqlParser)((Future)f).get();
            } catch (InterruptedException var3) {
                cache.remove(clazz, f);
                logger.warn("Get SqlParser " + clazz + " error", var3);
            } catch (ExecutionException var4) {
                logger.error("Get SqlParser " + clazz + " error", var4);
                throw new JpaException(var4);
            }
        }
    }

    public static EntitySqlParser getEntitySqlParser(Class<?> clazz) {
        while(true) {
            Future<EntitySqlParser> f = (Future)entityCache.get(clazz);
            if (null == f) {
                FutureTask<EntitySqlParser> ft = new FutureTask(() -> {
                    return new EntitySqlParser(getSqlParser(clazz));
                });
                f = (Future)entityCache.putIfAbsent(clazz, ft);
                if (null == f) {
                    f = ft;
                    ft.run();
                }
            }

            try {
                return (EntitySqlParser)((Future)f).get();
            } catch (InterruptedException var3) {
                cache.remove(clazz, f);
                logger.warn("Get entityCache " + clazz + " error", var3);
            } catch (ExecutionException var4) {
                logger.error("Get entityCache " + clazz + " error", var4);
                throw new JpaException(var4);
            }
        }
    }
}
