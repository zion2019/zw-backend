package com.zion.bill.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.zion.bill.dao.BillCategoryDao;
import com.zion.bill.model.BillCategory;
import com.zion.learning.repository.mongo.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@AllArgsConstructor
@Repository
public class BillCategoryRepository extends ZWMongoBasicRep<BillCategory> implements BillCategoryDao {


    @Override
    public Query generateQuery(BillCategory condition){
        Query query = new Query();
        if(condition.getParentId() != null){
            query.addCriteria(Criteria.where("parentId").is(condition.getParentId()));
        }
        if(CharSequenceUtil.isNotBlank(condition.getTitle())){
            Pattern pattern = Pattern.compile(condition.getTitle(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("title").regex(pattern));
        }
        if(condition.getUserId() != null){
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if(condition.getExcludeId() != null){
            query.addCriteria(Criteria.where("id").ne(condition.getExcludeId()));
        }
        if(CollUtil.isNotEmpty(condition.getIds())){
            query.addCriteria(Criteria.where("id").in(condition.getIds()));
        }
        if(CollUtil.isNotEmpty(condition.getTopicCodes())){
            query.addCriteria(Criteria.where("code").in(condition.getTopicCodes()));
        }
        if(condition.getCode() != null && StrUtil.isNotBlank(condition.getCode())){
            query.addCriteria(Criteria.where("code").in(condition.getCode()));
        }
        if(condition.getIncludeFields() != null && condition.getIncludeFields().length > 0){
            query.fields().include(condition.getIncludeFields());
        }
        if(condition.getParentIdLike() != null){
            // 修改后的逻辑：匹配包含 condition.getParentIdLike() 的 fullPath，等价于 like '%xxx%'
            Pattern pattern = Pattern.compile(".*" + condition.getParentIdLike() + ".*", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("fullPath").regex(pattern));
        }
        return query;
    }


}
