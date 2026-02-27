package com.zion.common.db;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.mongodb.client.result.UpdateResult;
import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.CommonConstant;
import com.zion.common.basic.Page;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.utils.SpringSecurityUtil;
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
import java.util.List;
import java.util.Map;


@Repository
public abstract class ZDaoMongoImpl <M extends BaseEntity> implements ZDao<M>{
    @Resource
    protected MongoTemplate mongoTemplate;

    private final Class<M> entityClass;
    @SuppressWarnings("unchecked")
    protected ZDaoMongoImpl() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<M>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public M getById(Long id) {
        return mongoTemplate.findById(id, entityClass);
    }

    @Override
    public long count(ZCondition<M> condition) {
        Query query = buildQueryWithCondition(condition);
        return mongoTemplate.count(query, entityClass);
    }

    @Override
    public M queryOne(ZCondition<M> condition) {
        Query query = buildQueryWithCondition(condition);
        return mongoTemplate.findOne(query, entityClass);
    }

    @Override
    public M save(M entity) {
        BaseEntityUtil.filedBasicInfo(entity);
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public long update(ZCondition<M> condition) {
        Update update = new Update().set("updatedTime", LocalDateTimeUtil.now())
                .set("updatedUser", SpringSecurityUtil.getCurrentUsername());
        UpdateResult result = mongoTemplate.updateMulti(buildQueryWithCondition(condition), update, entityClass);
        return result.getModifiedCount();
    }

    @Override
    public long deleteById(Long id){
        Update update = new Update().set("deleted", CommonConstant.DELETED_YES)
                .set("updatedTime", LocalDateTimeUtil.now())
                .set("updatedUser", SpringSecurityUtil.getCurrentUsername());
        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)), update, entityClass).getModifiedCount();
    }

    @Override
    public long delete(ZCondition<M> condition) {
        Update update = new Update().set("deleted", CommonConstant.DELETED_YES)
                .set("updatedTime", LocalDateTimeUtil.now())
                .set("updatedUser", SpringSecurityUtil.getCurrentUsername());
        UpdateResult result = mongoTemplate.updateMulti(buildQueryWithCondition(condition), update, entityClass);
        return result.getModifiedCount();
    }

    private Query buildQueryWithCondition(ZCondition<M> condition) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(CommonConstant.DELETED_NO));
        // eq
        if(condition.getEq() != null && !condition.getEq().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getEq().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
            }
        }

        // like
        if(condition.getLike() != null && !condition.getLike().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getLike().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).regex(entry.getValue().toString()));
            }
        }

        // ne
        if(condition.getNe() != null && !condition.getNe().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getNe().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).ne(entry.getValue()));
            }
        }

        // in
        if(condition.getIn() != null && !condition.getIn().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getNe().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
            }
        }

        // sort
        if(condition.getOrderBy() != null && !condition.getOrderBy().isEmpty()){
            condition.getOrderBy().forEach(pair -> query.with(
                    pair.getValue() == ZOrder.ASC ?
                        Sort.by(Sort.Direction.ASC, pair.getKey()) :
                        Sort.by(Sort.Direction.DESC, pair.getKey())));
        }

        // le
        if(condition.getLe() != null && !condition.getLe().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getLe().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).lte(entry.getValue()));
            }
        }

        // ge
        if(condition.getGe() != null && !condition.getGe().isEmpty()){
            for (Map.Entry<String, Object> entry : condition.getGe().entrySet()) {
                query.addCriteria(Criteria.where(entry.getKey()).gte(entry.getValue()));
            }
        }

        // select
        if(condition.getIncludeFields() != null && condition.getIncludeFields().length > 0){
            query.fields().include(condition.getIncludeFields());
        }

        return query;
    }

    @Override
    public List<M> queryList(ZCondition<M> condition) {
        return mongoTemplate.find(buildQueryWithCondition(condition), entityClass);
    }

    @Override
    public Page<M> queryPage(Page<M> page, ZCondition<M> condition) {
        Query query = buildQueryWithCondition(condition);
        //  query total
        long totalCount = mongoTemplate.count(query, entityClass);
        if(totalCount <= 0 ){
            return page;
        }

        // Create a Pageable object for pagination
        Pageable pageable;
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
        page.setDataList(BeanUtil.copyToList(entities,entityClass));
        page.setTotal(totalCount);
        return page;
    }
}
