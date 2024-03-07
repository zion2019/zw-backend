package com.zion.learning.repository.mongo;

import cn.hutool.core.text.CharSequenceUtil;
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
    Query generateQuery(Topic condition) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(CommonConstant.DELETED_NO));
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
        if(condition.getId() != null){
            query.addCriteria(Criteria.where("id").is(condition.getId()));
        }
        if(condition.getExcludeId() != null){
            query.addCriteria(Criteria.where("id").ne(condition.getExcludeId()));
        }
        if(condition.getIds() != null){
            query.addCriteria(Criteria.where("id").in(condition.getIds()));
        }
        if(condition.getIncludeFields() != null && condition.getIncludeFields().length > 0){
            query.fields().include(condition.getIncludeFields());
        }
        return query;
    }


}
