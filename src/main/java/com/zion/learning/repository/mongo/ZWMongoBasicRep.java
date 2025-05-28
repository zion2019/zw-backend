package com.zion.learning.repository.mongo;

import cn.hutool.core.bean.BeanUtil;
import com.mongodb.client.result.UpdateResult;
import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.CommonConstant;
import com.zion.common.basic.Page;
import com.zion.common.basic.ZWDao;
import com.zion.common.utils.BaseEntityUtil;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

@Repository
public abstract class ZWMongoBasicRep<M extends BaseEntity> implements ZWDao<M> {

    @Resource
    protected MongoTemplate mongoTemplate;
    private final Class<M> entityClass;
    @SuppressWarnings("unchecked")
    protected ZWMongoBasicRep() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<M>) genericSuperclass.getActualTypeArguments()[0];
    }


    private  Query getQuery(M condition){
        Query query = generateQuery(condition);
        query.addCriteria(Criteria.where("deleted").is(CommonConstant.DELETED_NO));
        if(condition.getId() != null){
            query.addCriteria(Criteria.where("id").is(condition.getId()));
        }

        if(condition.getIncludeFields() != null){
            query.fields().include(condition.getIncludeFields());
        }

        // sorting
        if(condition.getIsSort() != null && condition.getIsSort()){
            query.with(Sort.by(condition.getSortDirection(),condition.getSortFiledName()));
        }

        return query;
    }

    public abstract Query generateQuery(M condition);

    @Override
    public M getById(Long id) {
        return mongoTemplate.findById(id, entityClass);
    }

    @Override
    public long conditionCount(M condition) {
        Query query = getQuery(condition);
        return mongoTemplate.count(query, entityClass);
    }

    @Override
    public M save(M m) {
        BaseEntityUtil.filedBasicInfo(m);
        mongoTemplate.save(m);
        return m;
    }


    @Override
    public List<M> condition(M condition) {
        Query query = getQuery(condition);
        return mongoTemplate.find(query, entityClass);
    }

    @Override
    public M conditionOne(M condition) {
        Query query = getQuery(condition);
        return mongoTemplate.findOne(query, entityClass);
    }


    @Override
    public long delete(M condition) {
        Update update = new Update().set("deleted", CommonConstant.DELETED_YES);
        UpdateResult result = mongoTemplate.updateMulti(getQuery(condition), update, entityClass);
        return result.getModifiedCount();
    }

    @Override
    public <T> Page<T> pageQuery(Page<T> page,Class<T> resultClazz, M condition) {
        Query query = getQuery(condition);

        //  query total
        long totalCount = mongoTemplate.count(query, entityClass);
        if(totalCount <= 0 ){
            return page;
        }

        // Create a Pageable object for pagination
        Pageable pageable = null;
        if (page.getPageSize() == -1) {
            // 全量查询时不使用分页
            pageable = PageRequest.of(0, Integer.MAX_VALUE); // 使用一个极大值模拟全量查询
        } else {
            pageable = PageRequest.of(page.getPageNo() <= 0 ? page.getPageNo() : page.getPageNo() - 1, page.getPageSize());
        }
        query.with(pageable);

        // query
        List<M> entities = mongoTemplate.find(query, entityClass);
        page.setPageNo(page.getPageNo());
        page.setPageSize(page.getPageSize());
        page.setDataList(BeanUtil.copyToList(entities,resultClazz));
        page.setTotal(totalCount);
        return page;
    }

    @Override
    public void remove(M condition) {
        Update update = new Update().set("deleted", CommonConstant.DELETED_YES);
        update.set("updateTime",new Date());
        Query query = getQuery(condition);
        UpdateResult result = mongoTemplate.updateMulti(query, update, entityClass);
        result.getModifiedCount();
    }
}
