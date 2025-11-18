package com.zion.learning.subject.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.subject.dao.SubjectDao;
import com.zion.learning.subject.model.Subject;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class SubjectRepository extends ZWMongoBasicRep<Subject, BaseDaoQueryCondition> implements SubjectDao {

    @Override
    public Query generateQuery(Subject condition) {
        Query query = new Query();
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if (condition.getTagId() != null) {
            query.addCriteria(Criteria.where("tagId").is(condition.getTagId()));
        }
        // Handle multiple IDs filter
        if (condition.getIds() != null && !condition.getIds().isEmpty()) {
            query.addCriteria(Criteria.where("id").in(condition.getIds()));
        }

        return query;
    }
}