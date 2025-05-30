package com.zion.learning.repository.mongo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.zion.common.basic.CommonConstant;
import com.zion.learning.dao.TopicDao;
import com.zion.learning.model.Topic;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@AllArgsConstructor
@Repository
public class TopicRepository extends ZWMongoBasicRep<Topic> implements TopicDao {

    @Override
    public Query generateQuery(Topic condition) {
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
        return query;
    }


}
