package com.songdesy.jpa;

import com.songdesy.jpa.criteria.Criteria;
import com.songdesy.jpa.page.PageResult;

import java.io.Serializable;
import java.util.List;

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
 * Copyright(©) 2018
 * by songsong.wu.
 **/
public interface Jpa {
    int persist(Object var1);

    int dynamicPersist(Object var1);

    Serializable save(Object var1);

    <T extends Serializable> T save(Object var1, Class<T> var2);

    Serializable dynamicSave(Object var1);

    <T  extends Serializable> T dynamicSave(Object var1, Class<T> var2);

    int update(Object var1, String... var2);

    int dynamicUpdate(Object var1);

    int remove(Object var1);

    int remove(Class<?> var1, Serializable var2);

    int remove(Class<?> var1, Criteria var2);

    <T> T findOne(Class<T> var1, Serializable var2, String... var3);

    <T> T findOne(Class<T> var1, Criteria var2, String... var3);

    Long count(Class<?> var1);

    Long count(Class<?> var1, Criteria var2);

    <T> List<T> findAll(Class<T> var1, String... var2);

    <T> List<T> findList(Class<T> var1, Criteria var2, String... var3);

    <T> List<T> findPageList(Class<T> var1, Criteria var2, long var3, int var5, String... var6);

    <T> List<T> findPageList2(Class<T> var1, Criteria var2, long var3, int var5, String... var6);

    <T> PageResult<T> findByPage(Class<T> var1, Criteria var2, long var3, int var5, String... var6);

    <T> PageResult<T> findByPage2(Class<T> var1, Criteria var2, long var3, int var5, String... var6);

    int[] batchPersist(List<? extends Object> var1);

    int[] batchPersist(Object[] var1);

    int[] batchDynamicPersist(List<? extends Object> var1);

    int[] batchDynamicPersist(Object[] var1);

    int[] batchSave(List<? extends Object> var1);

    int[] batchSave(Object[] var1);

    int[] batchDynamicSave(List<? extends Object> var1);

    int[] batchDynamicSave(Object[] var1);

    int[] batchUpdate(List<? extends Object> var1);

    int[] batchUpdate(Object[] var1);

    int[] batchDynamicUpdate(List<? extends Object> var1);

    int[] batchDynamicUpdate(Object[] var1);

    int[] batchRemove(List<? extends Object> var1);

    int[] batchRemove(Object[] var1);

    int batchRemove(Class<?> var1, List<? extends Serializable> var2);

    int batchRemove(Class<?> var1, Serializable[] var2);

    Long createId();
}
