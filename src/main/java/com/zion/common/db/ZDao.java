package com.zion.common.db;

import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.Page;

import java.util.List;

/**
 * 描述：数据库操作crud通用接口
 * @author zion
 */
public interface ZDao<M extends BaseEntity> {
    M getById(Long id);
    long count(ZCondition<M> condition);
    M queryOne(ZCondition<M> condition);
    M save(M entity);
    long update(ZCondition<M> condition);
    long delete(ZCondition<M> condition);
    List<M> queryList(ZCondition<M> condition);
    Page<M> queryPage(Page<M> page, ZCondition<M> condition);
    long deleteById(Long id);
}
