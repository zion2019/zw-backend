package com.zion.learning.repository.mongo;

import cn.hutool.core.text.CharSequenceUtil;
import com.zion.common.basic.CommonConstant;
import com.zion.learning.dao.PointDao;
import com.zion.learning.model.Point;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@AllArgsConstructor
@Repository
public class PointRepository extends ZWMongoBasicRep<Point> implements PointDao {

    @Override
    Query generateQuery(Point condition) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(CommonConstant.DELETED_NO));
        if(CharSequenceUtil.isNotBlank(condition.getTitle())){
            Pattern pattern = Pattern.compile(condition.getTitle(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("title").regex(pattern));
        }
        if(condition.getTopicId() != null){
            query.addCriteria(Criteria.where("topicId").is(condition.getTopicId()));
        }
        if(condition.getId() != null){
            query.addCriteria(Criteria.where("id").is(condition.getId()));
        }
        return query;
    }



}
