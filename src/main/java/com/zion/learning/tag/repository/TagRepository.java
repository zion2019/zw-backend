package com.zion.learning.tag.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.tag.dao.TagDao;
import com.zion.learning.tag.model.Tag;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TagRepository extends ZWMongoBasicRep<Tag, BaseDaoQueryCondition> implements TagDao {

    @Override
    public Query generateQuery(Tag condition) {
        Query query = new Query();
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if (condition.getParentId() != null) {
            query.addCriteria(Criteria.where("parentId").is(condition.getParentId()));
        }
        return query;
    }
}