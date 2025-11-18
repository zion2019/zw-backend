package com.zion.learning.breakdown.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.breakdown.dao.BreakdownDao;
import com.zion.learning.breakdown.model.Breakdown;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class BreakdownRepository extends ZWMongoBasicRep<Breakdown, BaseDaoQueryCondition> implements BreakdownDao {

    @Override
    public Query generateQuery(Breakdown condition) {
        Query query = new Query();
        if (condition.getKnowledgePointId() != null) {
            query.addCriteria(Criteria.where("knowledgePointId").is(condition.getKnowledgePointId()));
        }
        if (condition.getSubjectId() != null) {
            query.addCriteria(Criteria.where("subjectId").is(condition.getSubjectId()));
        }
        return query;
    }
}