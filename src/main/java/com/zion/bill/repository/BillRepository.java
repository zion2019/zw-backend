package com.zion.bill.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.zion.bill.dao.BillCategoryDao;
import com.zion.bill.dao.BillDao;
import com.zion.bill.model.BillCategory;
import com.zion.bill.model.Bills;
import com.zion.learning.repository.mongo.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@AllArgsConstructor
@Repository
public class BillRepository extends ZWMongoBasicRep<Bills> implements BillDao {


    @Override
    public Query generateQuery(Bills condition){
        Query query = new Query();

        if(condition.getUserId() != null){
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }

        if(condition.getCategoryId() != null){
            query.addCriteria(Criteria.where("categoryId").is(condition.getCategoryId()));
        }

        if(condition.getChannelId() != null){
            query.addCriteria(Criteria.where("channelId").is(condition.getChannelId()));
        }

        if(condition.getChannelId() != null){
            query.addCriteria(Criteria.where("channelId").is(condition.getChannelId()));
        }

        if(CollUtil.isNotEmpty(condition.getIds())){
            query.addCriteria(Criteria.where("id").in(condition.getIds()));
        }

        if(condition.getIncludeFields() != null && condition.getIncludeFields().length > 0){
            query.fields().include(condition.getIncludeFields());
        }

        if(condition.getQueryBillStartTime() != null && condition.getQueryBillEndTime() != null){
            query.addCriteria(Criteria.where("createdTime").gte(condition.getQueryBillStartTime()).lte(condition.getQueryBillEndTime()));
        }

        if(CollUtil.isNotEmpty(condition.getQueryCategoryIdList())){
            query.addCriteria(Criteria.where("categoryId").in(condition.getQueryCategoryIdList()));
        }

        if(CharSequenceUtil.isNotBlank(condition.getSortFiledName())){
            query.with(Sort.by(condition.getSortDirection(),condition.getSortFiledName()));
        }

        return query;
    }


}
