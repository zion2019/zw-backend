package com.zion.learning.stage.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.stage.dao.StageDao;
import com.zion.learning.stage.model.Stage;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class StageRepository extends ZWMongoBasicRep<Stage, BaseDaoQueryCondition> implements StageDao {

    @Override
    public Query generateQuery(Stage condition) {
        Query query = new Query();
        if (condition.getSubjectId() != null) {
            query.addCriteria(Criteria.where("subjectId").is(condition.getSubjectId()));
        }
        if (condition.getOrderNum() != null) {
            query.addCriteria(Criteria.where("orderNum").is(condition.getOrderNum()));
        }
        return query;
    }
}