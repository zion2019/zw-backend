package com.zion.learning.strategy.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.strategy.dao.LearningStrategyDao;
import com.zion.learning.strategy.model.LearningStrategy;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class LearningStrategyRepository extends ZWMongoBasicRep<LearningStrategy, BaseDaoQueryCondition> implements LearningStrategyDao {

    @Override
    public Query generateQuery(LearningStrategy condition) {
        Query query = new Query();
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if (condition.getIsSystemDefault() != null) {
            query.addCriteria(Criteria.where("isSystemDefault").is(condition.getIsSystemDefault()));
        }
        return query;
    }
}